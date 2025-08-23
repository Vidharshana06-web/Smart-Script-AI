package com.example.mission.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:3000") // allow frontend
public class TestController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "✅ Backend is running and public endpoint is accessible!";
    }
}
