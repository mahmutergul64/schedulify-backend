package com.schedulify.api.controller;

import com.schedulify.api.entity.User;
import com.schedulify.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/providers")
    public ResponseEntity<List<User>> getAllProviders() {
        List<User> providers = userRepository.findAllProviders();
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchDoctors(@RequestParam String keyword) {
        List<User> doctors = userRepository.searchProviders(keyword);
        return ResponseEntity.ok(doctors);
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateProviderProfile(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        java.util.Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Kullanıcı bulunamadı.");
        }

        User user = userOpt.get();
        if (body.containsKey("specialty")) {
            user.setSpecialty(body.get("specialty"));
        }
        if (body.containsKey("bio")) {
            user.setBio(body.get("bio"));
        }

        userRepository.save(user);
        return ResponseEntity.ok(user);
    }
}