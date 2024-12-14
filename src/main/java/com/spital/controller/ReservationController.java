package com.spital.controller;
import com.spital.DTO.PacientDTO;
import com.spital.DTO.ReservationDTO;
import com.spital.DTO.SpecializationDTO;
import com.spital.DTO.UserDetails;
import com.spital.service.PacientService;
import com.spital.service.ReservationService;
import com.spital.service.SpecializationService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Log4j2
@Controller
public class ReservationController {

    @Autowired
    private ReservationService service;
    @Autowired
    private PacientService pacientService;
    @Autowired
    private SpecializationService specializationService;

    //Afiseaza toate rezervarile.
    @GetMapping("/reservations")
    public ModelAndView getAllReservations(HttpSession session) {
        UserDetails userDetails = (UserDetails) session.getAttribute("userDetails");
        ModelAndView redirect = new ModelAndView("startPage");
        if(!userDetails.getUserType().equals("admin"))
        {
            return redirect;
        }

        log.info("ReservationController.getAllReservations() has started...");
        List<ReservationDTO> result = service.getAllReservations();
        ModelAndView modelAndView = new ModelAndView("reservations");
        modelAndView.addObject("reservations", result);
        log.info("ReservationController.getAllReservations() has finished.");
        return modelAndView;
    }

   /* //Afiseaza rezervarea dupa ID-ul rezervarii.

    //Afiseaza rezervarea dupa ID-ul pacientului.
    @GetMapping("/getReservationForPacient")
    public List<Reservation> getReservationForPacient(@RequestParam String pacientID) {
        log.info("ReservationController.getReservationForPacient() has started...");
        return service.getReservationForPacient(pacientID);
    }
    */

    //Adaugare rezervare
    @PostMapping("/addReservation")
    public String addReservation(@ModelAttribute("reservation") ReservationDTO reservation, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "addReservation"; // Rămâne pe pagina de adăugare a rezervării
        }

        if (reservation.getFirstName().isEmpty()) {
            bindingResult.rejectValue("firstName", "error.firstName", "First name cannot be empty");
        }

        if (reservation.getLastName().isEmpty()) {
            bindingResult.rejectValue("lastName", "error.lastName", "Last name cannot be empty");
        }

        if (reservation.getSpecialization().isEmpty()) {
            bindingResult.rejectValue("specialization", "error.specialization", "Specialization cannot be empty");
        }

        if(reservation.getReservationDate() == null){
            bindingResult.rejectValue("reservationDate", "error.reservationDate", "Reservation date cannot be empty");
        }

        if(reservation.getIssue().isEmpty()){
            bindingResult.rejectValue("issue", "error.issue", "Issue cannot be empty");
        }

        ReservationDTO addedReservation = service.addReservation(reservation);
        if (addedReservation == null) {
            model.addAttribute("errorMessage", "This patient/specialization does not exist!");
            return "addReservation"; // Rămâne pe pagina de adăugare a rezervării
        }
        return "redirect:/reservations";
    }


    @GetMapping("/showAddReservation")
    public String showAddReservation(Model model, HttpSession session){
        UserDetails userDetails = (UserDetails) session.getAttribute("userDetails");
        if(!userDetails.getUserType().equals("admin"))
        {
            return "redirect:/";
        }
        List<SpecializationDTO> specializations = specializationService.getAllSpecializations();
        model.addAttribute("specializations", specializations);

        model.addAttribute("reservation", new ReservationDTO());
        return "addReservation";
    }


    // Sterge rezervare și reîncarcă pagina
    @PostMapping("/deleteReservation")
    public String deleteReservation(@RequestParam Integer id) {
        log.info("ReservationController.deleteReservation() has started...");
        service.deleteReservation(id);
        log.info("ReservationController.deleteReservation() has finished.");
        return "redirect:/reservations"; // Redirecționează către pagina de rezervări
    }



    //Editeaza rezervare
    @PostMapping("/editReservation")
    public String editReservation(@RequestParam Integer reservationID, @ModelAttribute("reservation") ReservationDTO updatedReservationDTO) {
        service.editReservation(reservationID, updatedReservationDTO);
        return "redirect:/reservations";
    }


    @GetMapping("/showEditReservation")
    public String showEditReservationPage(@RequestParam Integer id, Model model, HttpSession session) {
        UserDetails userDetails = (UserDetails) session.getAttribute("userDetails");

        List<SpecializationDTO> specializations = specializationService.getAllSpecializations();
        model.addAttribute("specializations", specializations);

        Optional<ReservationDTO> reservationDTOOptional = service.getReservationById(id);
        if (reservationDTOOptional.isPresent()) {
            model.addAttribute("reservation", reservationDTOOptional.get());
            return "editReservation";
        } else {
            return "redirect:/reservations";
        }
    }



    // -------------------- Vizualizare pagina Reservations pentru pacienti ------------------------

    @GetMapping("/reservationsForPacient")
    public ModelAndView getReservationsForPacient(HttpSession session, Model model ){
        UserDetails userDetails = (UserDetails) session.getAttribute("userDetails");
        ModelAndView redirect = new ModelAndView("startPage");
        if(!userDetails.getUserType().equals("pacient"))
        {
            return redirect;
        }

        PacientDTO pacient = pacientService.getPacientByEmail(userDetails.getEmail());
        model.addAttribute("pacient", pacient);

        log.info("ReservationController.getAllReservationsForPacient() has started...");
        List<ReservationDTO> result = service.getReservationsForPacient(pacient.getPacientID());
        ModelAndView modelAndView = new ModelAndView("reservationsForPacient");
        modelAndView.addObject("reservations", result);
        log.info("ReservationController.getAllReservationsForPacient() has finished.");
        return modelAndView;
    }

    // Sterge rezervare și reîncarcă pagina
    @PostMapping("/deleteReservationByPacient")
    public String deleteReservationByPacient(@RequestParam Integer id) {
        log.info("ReservationController.deleteReservation() has started...");
        service.deleteReservation(id);
        log.info("ReservationController.deleteReservation() has finished.");
        return "redirect:/reservationsForPacient"; // Redirecționează către pagina de rezervări
    }

    //Editeaza rezervare
    @PostMapping("/editReservationByPacient")
    public String editReservationByPacient(@RequestParam Integer reservationID, @ModelAttribute("reservation") ReservationDTO updatedReservationDTO) {
        service.editReservation(reservationID, updatedReservationDTO);
        return "redirect:/reservationsForPacient";
    }

    @GetMapping("/showEditReservationByPacient")
    public String showEditReservationPageByPacient(@RequestParam Integer id, Model model, HttpSession session) {
        UserDetails userDetails = (UserDetails) session.getAttribute("userDetails");

        List<SpecializationDTO> specializations = specializationService.getAllSpecializations();
        model.addAttribute("specializations", specializations);

        Optional<ReservationDTO> reservationDTOOptional = service.getReservationById(id);
        if (reservationDTOOptional.isPresent()) {
            model.addAttribute("reservation", reservationDTOOptional.get());
            return "editReservationByPacient";
        } else {
            return "redirect:/reservationsForPacient";
        }
    }

    //Adaugare rezervare de catre pacient
    @PostMapping("/addReservationByPacientReservationsPage")
    public String addReservationByPacient(@ModelAttribute("reservation") ReservationDTO reservation, BindingResult bindingResult, Model model, HttpSession session) {
        UserDetails userDetails = (UserDetails) session.getAttribute("userDetails");
        PacientDTO pacient = pacientService.getPacientByEmail(userDetails.getEmail());

        // Precompletarea detaliilor pacientului
        reservation.setPacientID(pacient.getPacientID());
        reservation.setFirstName(pacient.getFirstName());
        reservation.setLastName(pacient.getLastName());

        if (bindingResult.hasErrors()) {
            return "addReservation"; // Rămâne pe pagina de adăugare a rezervării
        }

        if (reservation.getFirstName().isEmpty()) {
            bindingResult.rejectValue("firstName", "error.firstName", "First name cannot be empty");
        }

        if (reservation.getLastName().isEmpty()) {
            bindingResult.rejectValue("lastName", "error.lastName", "Last name cannot be empty");
        }

        if (reservation.getSpecialization().isEmpty()) {
            bindingResult.rejectValue("specialization", "error.specialization", "Specialization cannot be empty");
        }

        if(reservation.getReservationDate() == null){
            bindingResult.rejectValue("reservationDate", "error.reservationDate", "Reservation date cannot be empty");
        }

        if(reservation.getIssue().isEmpty()){
            bindingResult.rejectValue("issue", "error.issue", "Issue cannot be empty");
        }

        ReservationDTO addedReservation = service.addReservation(reservation);
        if (addedReservation == null) {
            model.addAttribute("errorMessage", "This patient/specialization does not exist!");
            return "addReservationByPacientReservationsPage"; // Rămâne pe pagina de adăugare a rezervării
        }
        return "redirect:/reservationsForPacient";
    }

    @GetMapping("/showAddReservationByPacientReservationsPage")
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

        model.addAttribute("reservation", reservation);
        return "addReservationByPacientReservationsPage";
    }


}
