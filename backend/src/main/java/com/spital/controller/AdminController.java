package com.spital.controller;

import com.spital.DTO.PacientDTO;
import com.spital.service.PacientService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private PacientService service;

    // Obține lista tuturor pacienților (Admin Access)
    @GetMapping("/allPacients")
    public ResponseEntity<List<PacientDTO>> getAllPacients() {
        log.info("PacientController.getAllPacients() has started...");
        List<PacientDTO> pacients = service.getAllPacients();
        log.info("PacientController.getAllPacients() has finished.");
        return ResponseEntity.ok(pacients);
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

    // Obține detalii pacient după ID (Admin Access)
    @GetMapping("/{id}")
    public ResponseEntity<Optional<PacientDTO>> getPacientById(@PathVariable Integer id) {
        Optional<PacientDTO> pacient = service.getPacientById(id);
        return pacient.isPresent() ? ResponseEntity.ok(pacient) : ResponseEntity.notFound().build();
    }
}
