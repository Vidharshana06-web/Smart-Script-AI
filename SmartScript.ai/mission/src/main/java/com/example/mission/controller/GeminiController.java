package com.example.mission.controller;

import com.example.mission.model.GeminiRequest;
import com.example.mission.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/smartscript/api/test")   // Matches SecurityConfig permitAll()
@CrossOrigin(origins = "*")                // Allow all origins for API testing
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    /**
     * ✅ POST endpoint: Generate formatted script using Gemini model
     * URL: http://localhost:8080/smartscript/api/test/generate
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateFormattedScript(@RequestBody GeminiRequest request) {
        try {
            // ✅ Validate input
            if (request.getSummary() == null || request.getSummary().isEmpty()) {
                return ResponseEntity.badRequest().body("❌ Summary cannot be empty!");
            }

            if (request.getUniversityFormat() == null || request.getUniversityFormat().isEmpty()) {
                return ResponseEntity.badRequest().body("❌ University format cannot be empty!");
            }

            // ✅ Call service to get generated content
            String result = geminiService.generateFormattedContent(
                    request.getSummary(),
                    request.getUniversityFormat()
            );

            // ✅ Handle specific error responses
            if (result.startsWith("❌ Error:") || result.startsWith("❌ Exception:")) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(result);
            }

            if (result.contains("⚠️ No text found")) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
            }

            // ✅ Success response
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            // ✅ Log exception with details
            System.err.println("🔥 Error while generating content: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Exception occurred while processing request: " + e.getMessage());
        }
    }

    /**
     * ✅ Simple GET endpoint to test server health
     * URL: http://localhost:8080/smartscript/api/test/ping
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("✅ Server is running! Endpoint reachable.");
    }
}
