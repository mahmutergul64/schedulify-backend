package com.schedulify.api.service;

import com.schedulify.api.entity.Appointment;
import com.schedulify.api.entity.AppointmentStatus;
import com.schedulify.api.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentReminderService {

    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 * * * * ?")
    public void sendReminders() {
        System.out.println("Cron Job Uyandı! Yarınki randevular taranıyor...");

        LocalDateTime tomorrowStart = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime tomorrowEnd = LocalDateTime.now().plusDays(1).withHour(23).withMinute(59).withSecond(59);

        List<Appointment> appointments = appointmentRepository.findAppointmentsForTomorrow(
                AppointmentStatus.CONFIRMED, tomorrowStart, tomorrowEnd);

        for (Appointment app : appointments) {
            emailService.sendAppointmentReminder(
                    app.getClient().getEmail(),
                    app.getClient().getFullName(),
                    app.getProvider().getFullName(),
                    app.getStartTime().toString()
            );

            app.setReminderSent(true);
            appointmentRepository.save(app);

            System.out.println("Hatırlatma maili atıldı ve İŞARETLENDİ: " + app.getClient().getEmail());
        }
    }
}