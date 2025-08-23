package com.example.mission.controller;

import com.example.mission.model.GeminiRequest;
import com.example.mission.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")   // Updated to match SecurityConfig permitAll()
@CrossOrigin(origins = "*")    // Allow all origins for API testing
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateFormattedScript(@RequestBody GeminiRequest request) {
        try {
            String result = geminiService.generateFormattedContent(
                    request.getSummary(), 
                    request.getUniversityFormat()
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Exception occurred: " + e.getMessage());
        }
    }
}
