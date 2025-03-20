package com.spital.controller;

import com.spital.DTO.PacientDTO;
import com.spital.DTO.ReservationDTO;
import com.spital.DTO.SpecializationDTO;
import com.spital.DTO.UserDetails;
import com.spital.service.PacientService;
import com.spital.service.ReservationService;
import com.spital.service.SpecializationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService service;

    @Autowired
    private PacientService pacientService;

    @Autowired
    private SpecializationService specializationService;

    // Endpoint pentru administratori: Obține toate rezervările
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        log.info("getAllReservations() has started...");
        List<ReservationDTO> result = service.getAllReservations();
        log.info("getAllReservations() has finished.");
        return ResponseEntity.ok(result);
    }

    // Adaugă o rezervare (pentru administratori)
    @PostMapping
    public ResponseEntity<?> addReservation(@RequestBody ReservationDTO reservation) {
        if (reservation.getFirstName().isEmpty()) {
            return ResponseEntity.badRequest().body("First name cannot be empty");
        }
        if (reservation.getLastName().isEmpty()) {
            return ResponseEntity.badRequest().body("Last name cannot be empty");
        }
        if (reservation.getSpecialization().isEmpty()) {
            return ResponseEntity.badRequest().body("Specialization cannot be empty");
        }
        if (reservation.getReservationDate() == null) {
            return ResponseEntity.badRequest().body("Reservation date cannot be empty");
        }
        if (reservation.getIssue().isEmpty()) {
            return ResponseEntity.badRequest().body("Issue cannot be empty");
        }

        ReservationDTO addedReservation = service.addReservation(reservation);
        if (addedReservation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("This patient/specialization does not exist!");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(addedReservation);
    }

    // Șterge o rezervare (pentru administratori)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Integer id) {
        log.info("deleteReservation() has started...");
        service.deleteReservation(id);
        log.info("deleteReservation() has finished.");
        return ResponseEntity.ok("Reservation deleted successfully");
    }

    // Actualizează o rezervare (pentru administratori)
    @PutMapping("/{id}")
    public ResponseEntity<?> editReservation(@PathVariable Integer id,
                                             @RequestBody ReservationDTO updatedReservationDTO) {
        service.editReservation(id, updatedReservationDTO);
        return ResponseEntity.ok("Reservation updated successfully");
    }

    // ------------------ Endpoint-uri pentru pacienți ------------------

    // Obtinere rezervari personale de catre pacientul logat
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping("/my-reservations")
    public ResponseEntity<?> getReservationsForPacient(
            @AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt
    ) {
        String email = jwt.getSubject();
        log.info("getReservationsForPacient() has started...");
        PacientDTO pacient = pacientService.getPacientByEmail(email);
        List<ReservationDTO> reservations = service.getReservationsForPacient(pacient.getPacientID());
        log.info("getReservationsForPacient() has finished.");
        return ResponseEntity.ok(reservations);
    }

    // Adaugare rezervare de catre pacient
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    @PostMapping("/add-reservation")
    public ResponseEntity<?> addReservationByPacient(
            @AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt,
            @RequestBody ReservationDTO reservation
    ) {
        String email = jwt.getSubject();

        PacientDTO pacient = pacientService.getPacientByEmail(email);

        reservation.setPacientID(pacient.getPacientID());
        reservation.setFirstName(pacient.getFirstName());
        reservation.setLastName(pacient.getLastName());

        if (reservation.getFirstName() == null || reservation.getFirstName().isEmpty()) {
            return ResponseEntity.badRequest().body("First name cannot be empty");
        }
        if (reservation.getLastName() == null || reservation.getLastName().isEmpty()) {
            return ResponseEntity.badRequest().body("Last name cannot be empty");
        }
        if (reservation.getSpecialization() == null || reservation.getSpecialization().isEmpty()) {
            return ResponseEntity.badRequest().body("Specialization cannot be empty");
        }
        if (reservation.getReservationDate() == null) {
            return ResponseEntity.badRequest().body("Reservation date cannot be empty");
        }
        if (reservation.getIssue() == null || reservation.getIssue().isEmpty()) {
            return ResponseEntity.badRequest().body("Issue cannot be empty");
        }

        ReservationDTO addedReservation = service.addReservation(reservation);
        if (addedReservation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("This patient/specialization does not exist!");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(addedReservation);
    }


    // Actualizează o rezervare din partea pacientului
    @PutMapping("/pacient/{id}")
    public ResponseEntity<?> editReservationByPacient(@PathVariable Integer id,
                                                      @RequestBody ReservationDTO updatedReservationDTO) {
        service.editReservation(id, updatedReservationDTO);
        return ResponseEntity.ok("Reservation updated successfully");
    }

    // Șterge o rezervare din partea pacientului
    @DeleteMapping("/pacient/{id}")
    public ResponseEntity<?> deleteReservationByPacient(@PathVariable Integer id) {
        service.deleteReservation(id);
        return ResponseEntity.ok("Reservation deleted successfully");
    }

    // Obține o rezervare după ID (util pentru editare)
    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Integer id) {
        Optional<ReservationDTO> reservationOptional = service.getReservationById(id);
        if (reservationOptional.isPresent()) {
            return ResponseEntity.ok(reservationOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reservation not found");
        }
    }
}
