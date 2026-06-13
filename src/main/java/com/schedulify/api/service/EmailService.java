package com.schedulify.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendAppointmentConfirmation(String toEmail, String clientName, String providerName, String startTime) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("noreply@schedulify.com");
        message.setTo(toEmail);
        message.setSubject("Randevunuz Onaylandı - Schedulify");

        String mailBody = "Merhaba " + clientName + ",\n\n" +
                "Randevunuz başarıyla oluşturulmuştur.\n" +
                "Danışman: " + providerName + "\n" +
                "Tarih ve Saat: " + startTime + "\n\n" +
                "Bizi tercih ettiğiniz için teşekkür ederiz.\n" +
                "Schedulify Ekibi";

        message.setText(mailBody);

        mailSender.send(message);
    }

    @Async
    public void sendAppointmentReminder(String toEmail, String clientName, String providerName, String startTime) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("noreply@schedulify.com");
        message.setTo(toEmail);
        message.setSubject("⏰ Hatırlatma: Yarın Randevunuz Var!");

        String mailBody = "Merhaba " + clientName + ",\n\n" +
                "Bu otomatik bir hatırlatma mesajıdır.\n" +
                "Yarın " + providerName + " ile saat " + startTime + " sularında randevunuz bulunmaktadır.\n\n" +
                "Lütfen zamanında katılmayı unutmayınız.\n" +
                "Schedulify Ekibi";

        message.setText(mailBody);
        mailSender.send(message);
    }

    public void sendClientConfirmation(String toEmail, String clientName, String providerName, String time) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Randevunuz Onaylandı - SchedulifyPro");
        message.setText("Merhaba " + clientName + ",\n\n" +
                "Sayın " + providerName + " ile " + time + " tarihli randevunuz başarıyla oluşturulmuştur.\n\n" +
                "Sağlıklı günler dileriz.");
        mailSender.send(message);
    }

    public void sendProviderNotification(String toEmail, String providerName, String clientName, String time) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Yeni Randevu Bildirimi - SchedulifyPro");
        message.setText("Sayın " + providerName + ",\n\n" +
                "Takviminize yeni bir randevu eklendi.\n\n" +
                "Hasta Adı: " + clientName + "\n" +
                "Tarih/Saat: " + time + "\n\n" +
                "İyi çalışmalar dileriz.");
        mailSender.send(message);
    }

    public void sendClientCancellation(String toEmail, String clientName, String providerName, String time) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Randevunuz İptal Edildi - SchedulifyPro");
        message.setText("Merhaba " + clientName + ",\n\n" +
                "Sayın " + providerName + " ile " + time + " tarihindeki randevunuz iptal edilmiştir.\n\n" +
                "Yeni bir randevu planlamak için sistemimizi ziyaret edebilirsiniz.");
        mailSender.send(message);
    }

    public void sendProviderCancellation(String toEmail, String providerName, String clientName, String time) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Randevu İptal Bildirimi - SchedulifyPro");
        message.setText("Sayın " + providerName + ",\n\n" +
                "Takviminize ait bir randevu iptal edilmiştir.\n\n" +
                "Hasta Adı: " + clientName + "\n" +
                "İptal Edilen Tarih/Saat: " + time + "\n\n" +
                "Ajandanız ilgili saat aralığı için yeniden boşa çıkarılmıştır.");
        mailSender.send(message);
    }
}