package com.spital.controller;

import com.spital.DTO.PacientDTO;
import com.spital.DTO.PacientHomePageDTO;
import com.spital.service.PacientService;
import com.spital.service.ReservationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // Obține lista tuturor pacienților (Admin Access)
    @GetMapping
    public ResponseEntity<List<PacientDTO>> getAllPacients() {
        log.info("PacientController.getAllPacients() has started...");
        List<PacientDTO> pacients = service.getAllPacients();
        log.info("PacientController.getAllPacients() has finished.");
        return ResponseEntity.ok(pacients);
    }

    // Obține profilul unui pacient după email (Pacient Access)
    @GetMapping("/profile")
    public ResponseEntity<PacientHomePageDTO> getMyProfile(@RequestParam String email) {
        PacientHomePageDTO pacientHomePageDTO = service.getPacientHomePageDTO(email);
        return ResponseEntity.ok(pacientHomePageDTO);
    }

    // Adaugă un pacient nou (Admin Access)
    @PostMapping
    public ResponseEntity<PacientDTO> addPacient(@RequestBody PacientDTO pacientDTO) {
        PacientDTO addedPacient = service.addPacient(pacientDTO);
        if (addedPacient == null) {
            return ResponseEntity.badRequest().build(); // Pacientul există deja
        }
        return ResponseEntity.ok(addedPacient);
    }

    // Editare pacient existent (Admin Access)
    @PutMapping("/{id}")
    public ResponseEntity<PacientDTO> editPacient(@PathVariable Integer id, @RequestBody PacientDTO pacientDTO) {
        PacientDTO updatedPacient = service.editPacient(id, pacientDTO);
        return ResponseEntity.ok(updatedPacient);
    }

    // Șterge pacient după ID (Admin Access)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePacient(@PathVariable Integer id) {
        service.deletePacient(id);
        log.info("PacientController.deletePacient() has finished.");
        return ResponseEntity.noContent().build();
    }

    // Înregistrare pacient (Pacient Access)
    @PostMapping("/register")
    public ResponseEntity<PacientDTO> registerPacient(@RequestBody PacientDTO pacientDTO) {
        if (service.emailExists(pacientDTO.getEmail())) {
            return ResponseEntity.badRequest().build(); // Email deja folosit
        }
        PacientDTO savedPacient = service.registerPacient(pacientDTO);
        return ResponseEntity.ok(savedPacient);
    }

    // Obține detalii pacient după ID (Admin Access)
    @GetMapping("/{id}")
    public ResponseEntity<Optional<PacientDTO>> getPacientById(@PathVariable Integer id) {
        Optional<PacientDTO> pacient = service.getPacientById(id);
        return pacient.isPresent() ? ResponseEntity.ok(pacient) : ResponseEntity.notFound().build();
    }
}
