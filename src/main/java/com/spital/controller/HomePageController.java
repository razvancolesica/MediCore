package com.spital.controller;

import com.spital.DTO.*;
import com.spital.entity.Reservation;
import com.spital.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller

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

    PasswordResetDTO passwordResetDTO = new PasswordResetDTO();

    @GetMapping("/")
    public String startPage(){
        return "startPage";
    }

    @GetMapping("/homePagePacient")
    public String homePagePacient(Model model, HttpSession session) {
        UserDetails userDetails = (UserDetails) session.getAttribute("userDetails");
        PacientDTO pacient = pacientService.getPacientByEmail(userDetails.getEmail());
        if(!userDetails.getUserType().equals("pacient"))
        {
            return "redirect:/";
        }
        List<ReservationDTO> reservations = reservationService.getReservationsForPacient(pacient.getPacientID());
        model.addAttribute("reservations", reservations);

        int totalReservationsForPacient = reservationService.getReservationsForPacient(pacient.getPacientID()).size();
        model.addAttribute("totalReservationsForPacient", totalReservationsForPacient);

        int totalReservations = reservationService.getAllReservations().size();
        model.addAttribute("totalReservations", totalReservations);

        int totalPacients = pacientService.getAllPacients().size();
        model.addAttribute("totalPacients", totalPacients);

        int totalSpecializations = specializationService.getAllSpecializations().size();
        model.addAttribute("totalSpecializations", totalSpecializations);

        int totalDoctors = specializationService.getAllSpecializations().size();
        model.addAttribute("totalDoctors", totalDoctors);

        model.addAttribute("pacient", pacient);
        return "homePagePacient";
    }

    @GetMapping("/homePageAdmin")
    public String homePageAdmin(Model model, HttpSession session) {
        UserDetails userDetails = (UserDetails) session.getAttribute("userDetails");
        AdminDTO admin = adminService.getAdminByEmail(userDetails.getEmail());
        if (!userDetails.getUserType().equals("admin")) {
            return "redirect:/";
        }
        model.addAttribute("admin", admin);

        int totalReservations = reservationService.getAllReservations().size();
        model.addAttribute("totalReservations", totalReservations);

        int totalPacients = pacientService.getAllPacients().size();
        model.addAttribute("totalPacients", totalPacients);

        int totalSpecializations = specializationService.getAllSpecializations().size();
        model.addAttribute("totalSpecializations", totalSpecializations);

        int totalDoctors = specializationService.getAllSpecializations().size();
        model.addAttribute("totalDoctors", totalDoctors);

        return "homePageAdmin";
    }

    @GetMapping("/adminReservationsOverviewForDay")
    public String getAdminReservationsOverview(@RequestParam(value = "date", required = false) String dateStr, Model model) {
        LocalDate date;
        if (dateStr != null) {
            date = LocalDate.parse(dateStr);
        } else {
            date = null;
        }

        List<ReservationDTO> reservationList = reservationService.getAllReservations().stream()
                .filter(reservation -> reservation.getReservationDate().toLocalDate().equals(date))
                .collect(Collectors.toList());

        boolean hasReservations = !reservationList.isEmpty();
        boolean isFutureDate = date != null && date.isAfter(LocalDate.now());

        model.addAttribute("reservationList", reservationList);
        model.addAttribute("hasReservations", hasReservations);
        model.addAttribute("isFutureDate", isFutureDate);

        return "reservationForDateTable :: table";
    }

    @GetMapping("/adminReservationDates")
    @ResponseBody
    public List<LocalDate> getAdminReservationDates() {
        List<ReservationDTO> reservations = reservationService.getAllReservations();
        List<LocalDate> reservationDates = new ArrayList<>();

        for (ReservationDTO reservation : reservations) {
            LocalDate reservationDate = reservation.getReservationDate().toLocalDate();
            if (!reservationDates.contains(reservationDate)) {
                reservationDates.add(reservationDate);
            }
        }

        return reservationDates;
    }

    @GetMapping("/validateAccount")
    public String validateAccount(HttpSession session, Model model) {
        EmailValidation emailValidation = (EmailValidation) session.getAttribute("emailValidation");
        model.addAttribute("emailValidation", emailValidation.getEmail());
        return "validateAccount";
    }
    @PostMapping("/validateAccountForm")
    public String validateAccountForm(@RequestParam("email") String email, String code) {
        if (passwordResetService.verifyResetCode(email, code)) {
            return "redirect:/";
        } else {
            return "redirect:/validateAccount";
        }
    }
    @PostMapping("/requestValidateAccount")
    public String requestValidateAccount(@RequestParam("emailValidation")String email) {
        passwordResetService.generateAndSaveResetCodeValidation(email);
        return "redirect:/validateAccount";
    }

    @GetMapping("/showResetPasswordForm")
    public String showResetPasswordForm(Model model, HttpSession session) {
        PasswordResetDTO passwordResetDTO = (PasswordResetDTO) session.getAttribute("forgotPasswordSentEmail");
        model.addAttribute("forgotPasswordSentEmail", passwordResetDTO.getEmail());
        return "showResetPasswordForm";
    }
    @PostMapping("/requestResetPassword")
    public String requestResetPassword(@RequestParam("emailReset") String email, HttpSession session) {
        passwordResetService.generateAndSaveResetCode(email);
        passwordResetDTO.setEmail(email);
        session.setAttribute("forgotPasswordSentEmail", passwordResetDTO);
        return "redirect:/showResetPasswordForm";
    }
    @PostMapping("/resetPassword")
    public String resetPassword(String email, String resetCode, String newPassword) throws NoSuchAlgorithmException {
        if (passwordResetService.verifyResetCode(email, resetCode)) {
            passwordResetService.updatePacientPassword(email, newPassword);
            return "redirect:/";
        } else {
            return "redirect:/showResetPasswordForm";
        }
    }

    @GetMapping("/reservationsOverviewForDay")
    public String getReservationsOverview(@RequestParam(value = "date", required = false) String dateStr, Model model, HttpSession session) {
        UserDetails userDetails = (UserDetails) session.getAttribute("userDetails");
        List<ReservationDTO> reservationsNotFiltered = reservationService.getAllReservations();
        List<ReservationDTO> reservationList = new ArrayList<>();
        PacientDTO pacient = pacientService.getPacientByEmail(userDetails.getEmail());
        LocalDate date = null;
        if (dateStr != null) {
            date = LocalDate.parse(dateStr);
        }
        LocalDate reservationDate = null;
        for (ReservationDTO reservation : reservationsNotFiltered) {
            reservationDate = reservation.getReservationDate().atZone(ZoneId.systemDefault()).toLocalDate();
            if (reservation.getPacientID().equals(pacient.getPacientID()) && reservationDate.equals(date)) {
                reservationList.add(reservation);
            }
        }

        boolean hasReservations = !reservationList.isEmpty();
        boolean isFutureDate = date != null && date.isAfter(LocalDate.now());

        model.addAttribute("reservationList", reservationList);
        model.addAttribute("hasReservations", hasReservations);
        model.addAttribute("isFutureDate", isFutureDate);

        return "reservationForDateTable :: table";
    }



    @GetMapping("/patientReservationDates")
    @ResponseBody
    public List<LocalDate> getPatientReservationDates(HttpSession session) {
        UserDetails userDetails = (UserDetails) session.getAttribute("userDetails");
        List<ReservationDTO> reservationsNotFiltered = reservationService.getAllReservations();
        List<LocalDate> reservationDates = new ArrayList<>();
        PacientDTO pacient = pacientService.getPacientByEmail(userDetails.getEmail());

        for (ReservationDTO reservation : reservationsNotFiltered) {
            LocalDate reservationDate = reservation.getReservationDate().atZone(ZoneId.systemDefault()).toLocalDate();
            if (reservation.getPacientID().equals(pacient.getPacientID())) {
                reservationDates.add(reservationDate);
            }
        }

        return reservationDates;
    }


    //Adaugare rezervare de catre pacient
    @PostMapping("/addReservationByPacient")
    public String addReservationByPacient(@ModelAttribute("reservation") ReservationDTO reservation, BindingResult bindingResult, Model model, HttpSession session) {
        UserDetails userDetails = (UserDetails) session.getAttribute("userDetails");
        PacientDTO pacient = pacientService.getPacientByEmail(userDetails.getEmail());

        // Precompletarea detaliilor pacientului
        reservation.setPacientID(pacient.getPacientID());
        reservation.setFirstName(pacient.getFirstName());
        reservation.setLastName(pacient.getLastName());

        if (bindingResult.hasErrors()) {
            return "addReservationByPacient"; // Rămâne pe pagina de adăugare a rezervării
        }

        if (reservation.getSpecialization().isEmpty()) {
            bindingResult.rejectValue("specialization", "error.specialization", "Specialization cannot be empty");
        }

        if (reservation.getReservationDate() == null) {
            bindingResult.rejectValue("reservationDate", "error.reservationDate", "Reservation date cannot be empty");
        }

        if (reservation.getReservationTime() == null || reservation.getReservationTime().isEmpty()) {
            bindingResult.rejectValue("reservationTime", "error.reservationTime", "Reservation time cannot be empty");
        }

        if (reservation.getIssue().isEmpty()) {
            bindingResult.rejectValue("issue", "error.issue", "Issue cannot be empty");
        }

        // Concatenare data și ora pentru a crea LocalDateTime
        String dateTimeStr = reservation.getReservationDate().toLocalDate() + "T" + reservation.getReservationTime();
        reservation.setReservationDate(LocalDateTime.parse(dateTimeStr));

        ReservationDTO addedReservation = reservationService.addReservation(reservation);
        if (addedReservation == null) {
            model.addAttribute("errorMessage", "This patient/specialization does not exist!");
            return "addReservationByPacient"; // Rămâne pe pagina de adăugare a rezervării
        }
        return "redirect:/homePagePacient";
    }

    @GetMapping("/showAddReservationByPacient")
    public String showAddReservationByPacient(@RequestParam(value = "date", required = false) String dateStr, Model model, HttpSession session) {
        UserDetails userDetails = (UserDetails) session.getAttribute("userDetails");
        if (!userDetails.getUserType().equals("pacient")) {
            return "redirect:/";
        }
        PacientDTO pacient = pacientService.getPacientByEmail(userDetails.getEmail());
        ReservationDTO reservation = new ReservationDTO();
        reservation.setPacientID(pacient.getPacientID());
        reservation.setFirstName(pacient.getFirstName());
        reservation.setLastName(pacient.getLastName());

        List<SpecializationDTO> specializations = specializationService.getAllSpecializations();
        model.addAttribute("specializations", specializations);

        if (dateStr != null) {
            reservation.setReservationDate(LocalDate.parse(dateStr).atStartOfDay(ZoneId.systemDefault()).toLocalDateTime());
        }

        model.addAttribute("reservation", reservation);
        return "addReservationByPacient";
    }

}
