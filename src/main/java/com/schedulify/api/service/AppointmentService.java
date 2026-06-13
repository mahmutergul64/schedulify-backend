package com.schedulify.api.service;

import com.schedulify.api.entity.Appointment;
import com.schedulify.api.entity.AppointmentStatus;
import com.schedulify.api.entity.User;
import com.schedulify.api.repository.AppointmentRepository;
import com.schedulify.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public Appointment bookAppointment(Long clientId, Long providerId, LocalDateTime startTime, LocalDateTime endTime) {

        User client = userRepository.findById(clientId).orElseThrow(() -> new RuntimeException("Müşteri bulunamadı!"));
        User provider = userRepository.findById(providerId).orElseThrow(() -> new RuntimeException("Danışman bulunamadı!"));

        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new RuntimeException("Geçersiz saat aralığı! Bitiş saati başlangıçtan sonra olmalıdır.");
        }

        boolean isOverlapping = appointmentRepository.hasOverlappingAppointment(
                providerId, startTime, endTime, AppointmentStatus.CONFIRMED);
        if (isOverlapping) {
            throw new RuntimeException("Üzgünüz, doktorun bu saatler arasında başka bir randevusu var.");
        }

        Appointment newAppointment = Appointment.builder()
                .client(client)
                .provider(provider)
                .startTime(startTime)
                .endTime(endTime)
                .status(AppointmentStatus.CONFIRMED)
                .build();

        Appointment savedAppointment = appointmentRepository.save(newAppointment);

        emailService.sendAppointmentConfirmation(
                client.getEmail(),
                client.getFullName(),
                provider.getFullName(),
                startTime.toString()
        );

        return savedAppointment;
    }
}