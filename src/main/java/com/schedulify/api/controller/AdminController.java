package com.schedulify.api.controller;

import com.schedulify.api.repository.AppointmentRepository;
import com.schedulify.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getSystemStats() {
        Map<String, Long> stats = new HashMap<>();

        long totalAppointments = appointmentRepository.count();

        long totalUsers = userRepository.count();

        stats.put("totalAppointments", totalAppointments);
        stats.put("totalUsers", totalUsers);

        return ResponseEntity.ok(stats);
    }
}