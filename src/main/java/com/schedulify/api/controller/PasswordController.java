package com.schedulify.api.controller;

import com.schedulify.api.entity.User;
import com.schedulify.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PasswordController {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody java.util.Map<String, String> request) {
        String email = request.get("email");
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) return ResponseEntity.badRequest().body("Bu mail adresi kayıtlı değil.");

        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setTokenExpiryDate(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Şifre Sıfırlama İsteği");
        message.setText("Şifreni sıfırlamak için şu linke tıkla: http://localhost:5173/reset-password?token=" + token);
        mailSender.send(message);

        return ResponseEntity.ok("Sıfırlama linki mail adresine gönderildi.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody java.util.Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("password");

        User user = userRepository.findByResetPasswordToken(token).orElse(null);
        if (user == null || user.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Geçersiz veya süresi dolmuş token.");
        }

        user.setPassword(newPassword);
        user.setResetPasswordToken(null);
        user.setTokenExpiryDate(null);
        userRepository.save(user);

        return ResponseEntity.ok("Şifren başarıyla güncellendi!");
    }
}