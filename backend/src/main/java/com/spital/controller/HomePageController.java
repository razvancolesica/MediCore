package com.spital.controller;

import com.spital.DTO.*;
import com.spital.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/home")
public class HomePageController {

    @Autowired
    PacientService pacientService;

    @Autowired
    ReservationService reservationService;

    @Autowired
    SpecializationService specializationService;

    @Autowired
    AdminService adminService;

    @Autowired
    private PasswordResetService passwordResetService;

    @GetMapping("/homePagePacient")
    public ResponseEntity<?> homePagePacient(@RequestHeader("email") String email) {
        PacientDTO pacient = pacientService.getPacientByEmail(email);
        if (pacient == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied: not a pacient.");
        }

        List<ReservationDTO> reservations = reservationService.getReservationsForPacient(pacient.getPacientID());
        int totalReservationsForPacient = reservations.size();
        int totalReservations = reservationService.getAllReservations().size();
        int totalPacients = pacientService.getAllPacients().size();
        int totalSpecializations = specializationService.getAllSpecializations().size();
        int totalDoctors = specializationService.getAllSpecializations().size();

        return ResponseEntity.ok(new PacientHomePageDTO(
                pacient,
                reservations,
                totalReservationsForPacient,
                totalReservations,
                totalPacients,
                totalSpecializations,
                totalDoctors
        ));
    }

    @GetMapping("/homePageAdmin")
    public ResponseEntity<?> homePageAdmin(@RequestHeader("email") String email) {
        AdminDTO admin = adminService.getAdminByEmail(email);
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied: not an admin.");
        }

        int totalReservations = reservationService.getAllReservations().size();
        int totalPacients = pacientService.getAllPacients().size();
        int totalSpecializations = specializationService.getAllSpecializations().size();
        int totalDoctors = specializationService.getAllSpecializations().size();

        return ResponseEntity.ok(new AdminHomePageDTO(
                admin,
                totalReservations,
                totalPacients,
                totalSpecializations,
                totalDoctors
        ));
    }

    @GetMapping("/adminReservationsOverviewForDay")
    public ResponseEntity<?> getAdminReservationsOverview(@RequestParam(value = "date", required = false) String dateStr) {
        LocalDate date = dateStr != null ? LocalDate.parse(dateStr) : null;
        List<ReservationDTO> reservationList = reservationService.getAllReservations().stream()
                .filter(reservation -> reservation.getReservationDate().toLocalDate().equals(date))
                .collect(Collectors.toList());

        boolean hasReservations = !reservationList.isEmpty();
        boolean isFutureDate = date != null && date.isAfter(LocalDate.now());

        return ResponseEntity.ok(new ReservationOverviewDTO(reservationList, hasReservations, isFutureDate));
    }

    @GetMapping("/adminReservationDates")
    public ResponseEntity<List<LocalDate>> getAdminReservationDates() {
        List<LocalDate> reservationDates = reservationService.getAllReservations().stream()
                .map(reservation -> reservation.getReservationDate().toLocalDate())
                .distinct()
                .collect(Collectors.toList());

        return ResponseEntity.ok(reservationDates);
    }

    @PostMapping("/addReservationByPacient")
    public ResponseEntity<?> addReservationByPacient(@RequestBody ReservationDTO reservation, @RequestHeader("email") String email) {
        PacientDTO pacient = pacientService.getPacientByEmail(email);
        if (pacient == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied: not a pacient.");
        }

        reservation.setPacientID(pacient.getPacientID());
        reservation.setFirstName(pacient.getFirstName());
        reservation.setLastName(pacient.getLastName());

        if (reservation.getSpecialization().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Specialization cannot be empty");
        }
        if (reservation.getReservationDate() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reservation date cannot be empty");
        }
        if (reservation.getIssue().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Issue cannot be empty");
        }

        ReservationDTO addedReservation = reservationService.addReservation(reservation);
        if (addedReservation == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This patient/specialization does not exist!");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(addedReservation);
    }

    @GetMapping("/patientReservationDates")
    public ResponseEntity<List<LocalDate>> getPatientReservationDates(@RequestHeader("email") String email) {
        PacientDTO pacient = pacientService.getPacientByEmail(email);
        if (pacient == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<LocalDate> reservationDates = reservationService.getAllReservations().stream()
                .filter(reservation -> reservation.getPacientID().equals(pacient.getPacientID()))
                .map(reservation -> reservation.getReservationDate().toLocalDate())
                .distinct()
                .collect(Collectors.toList());

        return ResponseEntity.ok(reservationDates);
    }
}
