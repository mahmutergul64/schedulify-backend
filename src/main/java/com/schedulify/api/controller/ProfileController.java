package com.schedulify.api.controller;

import com.schedulify.api.entity.User;
import com.schedulify.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;
    private final String UPLOAD_DIR = "uploads/";

    @PostMapping("/{id}/avatar")
    public String uploadAvatar(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        User user = userRepository.findById(id).orElseThrow();

        if (user.getAvatarUrl() != null) {
            Files.deleteIfExists(Paths.get(UPLOAD_DIR + user.getAvatarUrl()));
        }

        Files.createDirectories(Paths.get(UPLOAD_DIR));
        String filename = id + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), Paths.get(UPLOAD_DIR + filename), StandardCopyOption.REPLACE_EXISTING);

        user.setAvatarUrl(filename);
        userRepository.save(user);
        return filename;
    }

    @DeleteMapping("/{id}/avatar")
    public void deleteAvatar(@PathVariable Long id) throws IOException {
        User user = userRepository.findById(id).orElseThrow();
        if (user.getAvatarUrl() != null) {
            Files.deleteIfExists(Paths.get(UPLOAD_DIR + user.getAvatarUrl()));
            user.setAvatarUrl(null);
            userRepository.save(user);
        }
    }
}