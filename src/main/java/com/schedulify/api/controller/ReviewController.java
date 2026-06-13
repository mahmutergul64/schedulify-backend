package com.schedulify.api.controller;

import com.schedulify.api.entity.Review;
import com.schedulify.api.entity.User;
import com.schedulify.api.repository.ReviewRepository;
import com.schedulify.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @PostMapping("/add")
    public ResponseEntity<?> addReview(@RequestBody Map<String, Object> payload) {
        try {
            Long clientId = Long.valueOf(payload.get("clientId").toString());
            Long providerId = Long.valueOf(payload.get("providerId").toString());
            int rating = Integer.parseInt(payload.get("rating").toString());
            String comment = payload.get("comment").toString();

            User client = userRepository.findById(clientId).orElse(null);
            User provider = userRepository.findById(providerId).orElse(null);

            if (client == null || provider == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Kullanıcı bulunamadı.");
            }

            Review review = Review.builder()
                    .client(client)
                    .provider(provider)
                    .rating(rating)
                    .comment(comment)
                    .createdAt(LocalDateTime.now())
                    .build();

            reviewRepository.save(review);
            return ResponseEntity.ok("Değerlendirmeniz başarıyla kaydedildi!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    @GetMapping("/provider/{providerId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long providerId) {
        Double avg = reviewRepository.getAverageRating(providerId);
        return ResponseEntity.ok(avg != null ? avg : 0.0);
    }
}