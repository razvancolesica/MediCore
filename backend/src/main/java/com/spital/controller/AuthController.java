package com.spital.controller;

import com.spital.DTO.UserDetails;
import com.spital.entity.Admin;
import com.spital.entity.Pacient;
import com.spital.repository.AdminRepository;
import com.spital.repository.PacientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private UserDetails userDetails = new UserDetails();

    @Autowired
    private PacientRepository pacientRepository;

    @Autowired
    private AdminRepository adminRepository;

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

        userDetails.setPassword(password);
        userDetails.setEmail(email);

        // Verificarea în baza de date
        Optional<Pacient> pacientUser = pacientRepository.findByEmail(email);
        Optional<Admin> adminUser = adminRepository.findByEmail(email);

        if (pacientUser.isPresent()) {
            if (password.equals(pacientUser.get().getPassword())) {
                userDetails.setUserType("pacient");
                return ResponseEntity.ok(userDetails); // Returnează informațiile utilizatorului
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid pacient credentials.");
            }
        }

        if (adminUser.isPresent()) {
            if (password.equals(adminUser.get().getPassword())) {
                userDetails.setUserType("admin");
                return ResponseEntity.ok(userDetails); // Returnează informațiile utilizatorului
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid admin credentials.");
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("User not found.");
    }

}
