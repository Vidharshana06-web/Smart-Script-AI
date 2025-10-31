package com.example.mission.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/smartscript/api/test/hello")
    public String hello() {
        return "✅ Public endpoint is working!";
    }
}
