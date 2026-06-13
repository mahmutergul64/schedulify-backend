package com.schedulify.api.controller;

import com.schedulify.api.entity.User;
import com.schedulify.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final UserRepository userRepository;

    @PostMapping("/create-checkout")
    public ResponseEntity<?> createCheckoutSession() {
        return ResponseEntity.ok(Map.of("url", "/vip-success"));
    }

    @PutMapping("/upgrade/{userId}")
    public ResponseEntity<?> upgradeToVip(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsVip(true);
            userRepository.save(user);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.badRequest().body("Kullanıcı bulunamadı");
        }
    }
}