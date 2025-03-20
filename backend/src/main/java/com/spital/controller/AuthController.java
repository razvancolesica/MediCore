package com.spital.controller;

import com.spital.entity.Admin;
import com.spital.entity.Pacient;
import com.spital.repository.AdminRepository;
import com.spital.repository.PacientRepository;
import com.spital.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private PacientRepository pacientRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {

        // Validarea email-ului
        String emailRegex = "^[A-Za-z\\d]+@[A-Za-z\\d]+\\.[A-Za-z\\d]+$";
        Pattern emailPattern = Pattern.compile(emailRegex);
        Matcher emailMatcher = emailPattern.matcher(email);

        if (!emailMatcher.find()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid email format.");
        }

        // Verificarea ca email-ul si parola sa nu fie goale
        if (email.isEmpty() || password.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email and password must not be empty.");
        }

        // Verificarea în baza de date
        Optional<Pacient> pacientUser = pacientRepository.findByEmail(email);
        Optional<Admin> adminUser = adminRepository.findByEmail(email);

        if (pacientUser.isPresent() && passwordEncoder.matches(password, pacientUser.get().getPassword())) {
            String token = jwtUtil.generateToken(email, pacientUser.get().getUserType().toUpperCase());
            return ResponseEntity.ok(token);
        }

        if (adminUser.isPresent() && passwordEncoder.matches(password, adminUser.get().getPassword())) {
            String token = jwtUtil.generateToken(email, adminUser.get().getUserType().toUpperCase());
            return ResponseEntity.ok(token);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid credentials.");
    }
}