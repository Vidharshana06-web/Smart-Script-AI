package com.example.mission.controller;

import com.example.mission.model.User;
import com.example.mission.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;  // <-- add this import
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ==============================
    // Signup endpoint
    // ==============================
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists!");
        }

        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    // ==============================
    // Login endpoint
    // ==============================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginReq, HttpSession session) {
        Optional<User> optionalUser = userRepository.findByEmail(loginReq.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body("User not found");
        }

        User user = optionalUser.get();

        // Use passwordEncoder to check hashed password
        if (passwordEncoder.matches(loginReq.getPassword(), user.getPassword())) {
            // store user info in session
            session.setAttribute("USER", user.getEmail());
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    // ==============================
    // Logout endpoint
    // ==============================
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate(); // destroy session
        return ResponseEntity.ok("Logged out successfully!");
    }
}
