package com.schedulify.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, String>> analyzeSymptoms(@RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message").toLowerCase();
        Map<String, String> response = new HashMap<>();

        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {}

        if (userMessage.contains("baş") || userMessage.contains("migren") || userMessage.contains("uyku")) {
            response.put("specialty", "Nöroloji");
            response.put("reply", "Belirttiğiniz semptomlar (baş ağrısı/migren) doğrultusunda bir Nöroloji uzmanına görünmeniz en doğrusu olacaktır. Sizin için en iyi uzmanları randevu sayfamızda bulabilirsiniz.");
        } else if (userMessage.contains("mide") || userMessage.contains("karın") || userMessage.contains("bulantı")) {
            response.put("specialty", "Dahiliye");
            response.put("reply", "Mide ve sindirim şikayetleriniz için bir Dahiliye (İç Hastalıkları) uzmanından randevu almanızı tavsiye ederim.");
        } else if (userMessage.contains("diş") || userMessage.contains("çene") || userMessage.contains("ağrı")) {
            response.put("specialty", "Diş Hekimi");
            response.put("reply", "Şikayetleriniz diş ve çene sağlığı ile ilgili görünüyor. Hemen bir Diş Hekimi ile görüşmelisiniz.");
        } else if (userMessage.contains("cilt") || userMessage.contains("kaşıntı") || userMessage.contains("leke") || userMessage.contains("sivilce")) {
            response.put("specialty", "Dermatolog");
            response.put("reply", "Cilt problemleriniz için bir Dermatolog (Cildiye) uzmanına görünmeniz uygun olacaktır.");
        } else if (userMessage.contains("üzgün") || userMessage.contains("stres") || userMessage.contains("kaygı") || userMessage.contains("depresyon")) {
            response.put("specialty", "Psikolog");
            response.put("reply", "Yaşadığınız stres ve kaygı durumları için bir Psikolog ile görüşmek size çok iyi gelecektir. Lütfen uzmanlarımızdan destek almaktan çekinmeyin.");
        } else if (userMessage.contains("kilo") || userMessage.contains("diyet") || userMessage.contains("beslenme")) {
            response.put("specialty", "Diyetisyen");
            response.put("reply", "Sağlıklı beslenme ve kilo kontrolü hedefleriniz için bir Diyetisyen ile yola çıkmanızı öneririm.");
        } else {
            response.put("specialty", "Genel");
            response.put("reply", "Geçmiş olsun. Size en doğru teşhisi koyabilmesi için bir Dahiliye (İç Hastalıkları) veya Pratisyen Hekim ile ön görüşme yapmanızı öneririm.");
        }

        return ResponseEntity.ok(response);
    }
}