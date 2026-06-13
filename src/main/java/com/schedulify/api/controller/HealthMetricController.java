package com.schedulify.api.controller;

import com.schedulify.api.entity.HealthMetric;
import com.schedulify.api.entity.User;
import com.schedulify.api.repository.HealthMetricRepository;
import com.schedulify.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/health-metrics")
@RequiredArgsConstructor
public class HealthMetricController {

    private final HealthMetricRepository healthMetricRepository;
    private final UserRepository userRepository;

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<HealthMetric>> getMetrics(@PathVariable Long clientId) {
        return ResponseEntity.ok(healthMetricRepository.findByClientIdOrderByRecordDateAsc(clientId));
    }

    @PostMapping("/client/{clientId}")
    public ResponseEntity<?> addMetric(@PathVariable Long clientId, @RequestBody Map<String, String> payload) {
        User client = userRepository.findById(clientId).orElseThrow();

        HealthMetric metric = HealthMetric.builder()
                .client(client)
                .recordDate(LocalDate.parse(payload.get("recordDate")))
                .weight(payload.get("weight") != null && !payload.get("weight").isEmpty() ? Double.valueOf(payload.get("weight")) : null)
                .bloodPressure(payload.get("bloodPressure"))
                .bloodSugar(payload.get("bloodSugar") != null && !payload.get("bloodSugar").isEmpty() ? Integer.valueOf(payload.get("bloodSugar")) : null)
                .notes(payload.get("notes"))
                .build();

        return ResponseEntity.ok(healthMetricRepository.save(metric));
    }
}