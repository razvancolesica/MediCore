package com.spital.controller;

import com.spital.DTO.PacientDTO;
import com.spital.DTO.PacientHomePageDTO;
import com.spital.service.PacientService;
import com.spital.service.ReservationService;
import com.spital.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("/api/pacients")
public class PacientController {

    @Autowired
    private PacientService service;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;



    // Obține profilul pacientului autentificat
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping("/profile")
    public ResponseEntity<PacientHomePageDTO> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        PacientHomePageDTO pacientHomePageDTO = service.getPacientHomePageDTO(email);
        return ResponseEntity.ok(pacientHomePageDTO);
    }

    // Înregistrare pacient (Pacient Access)
    @PostMapping("/register")
    public ResponseEntity<PacientDTO> registerPacient(@RequestBody PacientDTO pacientDTO) {
        if (service.emailExists(pacientDTO.getEmail())) {
            return ResponseEntity.badRequest().build(); // Email deja folosit
        }

        // Encodăm parola înainte de a o salva
        pacientDTO.setPassword(passwordEncoder.encode(pacientDTO.getPassword()));

        PacientDTO savedPacient = service.registerPacient(pacientDTO);
        return ResponseEntity.ok(savedPacient);
    }


}