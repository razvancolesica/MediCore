package com.spital.controller;

import com.spital.DTO.PacientDTO;
import com.spital.DTO.SpecializationDTO;
import com.spital.DTO.UserDetails;
import com.spital.service.PacientService;
import com.spital.service.SpecializationService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("/api/specializations")
public class SpecializationController {

    @Autowired
    private SpecializationService service;

    @Autowired
    private PacientService pacientService;

    // Obține toate specializările
    @GetMapping
    public ResponseEntity<List<SpecializationDTO>> getAllSpecializations() {
        log.info("SpecializationController.getAllSpecializations() started...");
        List<SpecializationDTO> specializations = service.getAllSpecializations();
        log.info("SpecializationController.getAllSpecializations() finished.");
        return ResponseEntity.ok(specializations);
    }

    // Adaugă o specializare
    @PostMapping
    public ResponseEntity<?> addSpecialization(@Valid @RequestBody SpecializationDTO specialization,
                                               BindingResult bindingResult) {
        if (specialization.getSpecializationName() == null || specialization.getSpecializationName().isEmpty()) {
            bindingResult.rejectValue("specializationName", "error.specializationName", "Specialization name cannot be empty");
        }
        if (specialization.getMedic() == null || specialization.getMedic().isEmpty()) {
            bindingResult.rejectValue("medic", "error.medic", "Medic cannot be empty");
        }
        if (specialization.getRoom() == null || specialization.getRoom().isEmpty()) {
            bindingResult.rejectValue("room", "error.room", "Room cannot be empty");
        }
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        SpecializationDTO addedSpecialization = service.addSpecialization(specialization);
        if (addedSpecialization == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Specialization already exists!");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(addedSpecialization);
    }

    // Obține o specializare după ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getSpecializationById(@PathVariable Integer id) {
        Optional<SpecializationDTO> specializationOptional = service.getSpecializationById(id);
        if (specializationOptional.isPresent()) {
            return ResponseEntity.ok(specializationOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Specialization not found");
        }
    }

    // Actualizează o specializare
    @PutMapping("/{id}")
    public ResponseEntity<?> editSpecialization(@PathVariable Integer id,
                                                @RequestBody SpecializationDTO updatedSpecializationDTO) {
        service.editSpecialization(id, updatedSpecializationDTO);
        return ResponseEntity.ok("Specialization updated successfully");
    }

    // Șterge o specializare
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSpecialization(@PathVariable Integer id) {
        log.info("SpecializationController.deleteSpecialization() started...");
        service.deleteSpecialization(id);
        log.info("SpecializationController.deleteSpecialization() finished.");
        return ResponseEntity.ok("Specialization deleted successfully");
    }

    // Obține specializările pentru un pacient
    @GetMapping("/pacient")
    public ResponseEntity<?> getSpecializationsForPacient(@AuthenticationPrincipal UserDetails userDetails) {
        PacientDTO pacient = pacientService.getPacientByEmail(userDetails.getEmail());
        log.info("SpecializationController.getSpecializationsForPacient() started...");
        List<SpecializationDTO> specializations = service.getAllSpecializations();
        log.info("SpecializationController.getSpecializationsForPacient() finished.");
        return ResponseEntity.ok(specializations);
    }
}
