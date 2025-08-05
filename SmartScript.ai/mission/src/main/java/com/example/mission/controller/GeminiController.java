package com.example.mission.controller;

import com.example.mission.model.GeminiRequest;
import com.example.mission.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/generate")
    public String generateFormattedScript(@RequestBody GeminiRequest request) {
        try {
            return geminiService.generateFormattedContent(request.getSummary(), request.getUniversityFormat());
        } catch (Exception e) {
            return "❌ Exception occurred: " + e.getMessage();
        }
    }
}
