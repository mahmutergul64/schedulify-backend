package com.schedulify.api.controller;

import com.schedulify.api.entity.Appointment;
import com.schedulify.api.entity.AppointmentStatus;
import com.schedulify.api.repository.AppointmentRepository;
import com.schedulify.api.service.AppointmentService;
import com.schedulify.api.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestBody Map<String, Object> payload) {
        try {
            Long clientId = Long.valueOf(payload.get("clientId").toString());
            Long providerId = Long.valueOf(payload.get("providerId").toString());
            LocalDateTime start = LocalDateTime.parse(payload.get("startTime").toString());
            LocalDateTime end = LocalDateTime.parse(payload.get("endTime").toString());

            if (start.getDayOfWeek() == java.time.DayOfWeek.SATURDAY || start.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
                return ResponseEntity.badRequest().body("Hafta sonu randevu alınamaz! Lütfen hafta içi bir gün seçiniz.");
            }

            if (start.getHour() < 9 || end.getHour() > 17 || (end.getHour() == 17 && end.getMinute() > 0)) {
                return ResponseEntity.badRequest().body("Mesai saatleri dışında randevu alınamaz! Mesai saatleri: 09:00 - 17:00");
            }

            Appointment appointment = appointmentService.bookAppointment(clientId, providerId, start, end);
            appointment.setStatus(AppointmentStatus.PENDING);
            appointmentRepository.save(appointment);

            try {
                emailService.sendProviderNotification(
                        appointment.getProvider().getEmail(),
                        appointment.getProvider().getFullName(),
                        appointment.getClient().getFullName(),
                        appointment.getStartTime().toString()
                );
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + appointment.getProvider().getId(),
                    "🔔 Yeni bir randevu talebiniz var!"
            );

            return ResponseEntity.ok(appointment);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<?> confirmAppointment(@PathVariable Long id) {
        return appointmentRepository.findById(id).map(appointment -> {
            appointment.setStatus(AppointmentStatus.CONFIRMED);
            appointmentRepository.save(appointment);

            try {
                emailService.sendClientConfirmation(
                        appointment.getClient().getEmail(),
                        appointment.getClient().getFullName(),
                        appointment.getProvider().getFullName(),
                        appointment.getStartTime().toString()
                );
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + appointment.getClient().getId(),
                    "✅ Randevunuz doktor tarafından onaylandı!"
            );

            return ResponseEntity.ok(appointment);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeAppointment(@PathVariable Long id) {
        return appointmentRepository.findById(id).map(appointment -> {
            appointment.setStatus(AppointmentStatus.COMPLETED);
            appointmentRepository.save(appointment);
            return ResponseEntity.ok(appointment);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PutMapping("/{id}/notes")
    public ResponseEntity<?> updateAppointmentNotes(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        return appointmentRepository.findById(id).map(appointment -> {
            appointment.setNotes(payload.get("notes"));
            appointmentRepository.save(appointment);
            return ResponseEntity.ok("Rapor başarıyla kaydedildi");
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Randevu bulunamadı"));
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<?> uploadDocument(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Appointment appointment = appointmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Randevu bulunamadı"));

            String uploadDir = "uploads/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.copy(file.getInputStream(), filePath);

            appointment.setDocumentUrl(fileName);
            appointmentRepository.save(appointment);

            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
        java.util.Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Randevu bulunamadı.");
        }

        Appointment appointment = appointmentOpt.get();

        if (appointment.getStatus() == AppointmentStatus.PENDING) {
            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + appointment.getClient().getId(),
                    "❌ Bir randevu talebiniz iptal edildi."
            );
        }

        appointmentRepository.deleteById(id);

        try {
            emailService.sendClientCancellation(
                    appointment.getClient().getEmail(),
                    appointment.getClient().getFullName(),
                    appointment.getProvider().getFullName(),
                    appointment.getStartTime().toString()
            );

            emailService.sendProviderCancellation(
                    appointment.getProvider().getEmail(),
                    appointment.getProvider().getFullName(),
                    appointment.getClient().getFullName(),
                    appointment.getStartTime().toString()
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<Appointment>> getProviderAppointments(@PathVariable Long providerId) {
        List<Appointment> appointments = appointmentRepository.findByProviderId(providerId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Appointment>> getClientAppointments(@PathVariable Long clientId) {
        List<Appointment> appointments = appointmentRepository.findByClientId(clientId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping
    public ResponseEntity<List<Appointment>> getAllApps() {
        return ResponseEntity.ok(appointmentRepository.findAll());
    }
}