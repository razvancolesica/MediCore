package com.spital.controller;
import com.spital.DTO.EmailValidation;
import com.spital.DTO.PacientDTO;
import com.spital.DTO.UserDetails;
import com.spital.entity.Pacient;
import com.spital.mapper.PacientMapper;
import com.spital.service.PacientService;
import com.spital.service.ReservationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Log4j2
@Controller
public class PacientController {

    @Autowired
    private PacientService service;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private PacientMapper pacientMapper;

    EmailValidation emailValidation = new EmailValidation();


    //Afiseaza toti pacientii.
    @GetMapping("/pacients")
    public ModelAndView getAllPacients(HttpSession session) {
        UserDetails userDetails = (UserDetails) session.getAttribute("userDetails");
        ModelAndView redirect = new ModelAndView("startPage");
        if(!userDetails.getUserType().equals("admin"))
        {
            return redirect;
        }

        log.info("ReservationController.getAllPacients() has started...");
        List<PacientDTO> pacients = service.getAllPacients();
        ModelAndView modelAndView = new ModelAndView("pacients");
        modelAndView.addObject("pacients", pacients);
        log.info("ReservationController.getAllPacients() has finished.");
        return modelAndView;
    }

    //Adaugare pacient
    @PostMapping("/addPacient")
    public String addPacient(@Valid @ModelAttribute("pacient") PacientDTO pacient,
                             BindingResult bindingResult, Model model){

        if(pacient.getFirstName().isEmpty()){
            bindingResult.rejectValue("firstName", "error.firstName", "First name cannot be empty");
        }

        if(pacient.getLastName().isEmpty()){
            bindingResult.rejectValue("lastName", "error.lastName", "Last name cannot be empty");
        }

        if(pacient.getAge() == 0){
            bindingResult.rejectValue("age", "error.age", "Age cannot be empty");
        }

        if(pacient.getCnp().isEmpty())
        {
            bindingResult.rejectValue("cnp", "error.cnp", "CNP cannot be empty");
        }

        if(pacient.getPhoneNumber().isEmpty())
        {
            bindingResult.rejectValue("phoneNumber", "error.phoneNumber", "Phone number cannot be empty");
        }

        if(bindingResult.hasErrors()){
            return "addPacient";
        }

        PacientDTO addedPacient = service.addPacient(pacient);
        if (addedPacient == null) {
            model.addAttribute("errorMessage", "Pacientul există deja!");
            return "addPacient";
        }
        return "redirect:/pacients";
    }

    @GetMapping("/showAddPacient")
    public String showAddPacient(Model model, HttpSession session){
        UserDetails userDetails = (UserDetails) session.getAttribute("userDetails");
        if(!userDetails.getUserType().equals("admin"))
        {
            return "redirect:/";
        }
        model.addAttribute("pacient", new PacientDTO());
        return "addPacient";
    }


    //Sterge pacient
    @PostMapping("/deletePacient")
    public String deletePacient(@RequestParam Integer pacientID) {
        log.info("ReservationController.deletePacient() has started...");
        service.deletePacient(pacientID);
        log.info("ReservationController.deletePacient() has finished.");
        return "redirect:/pacients";
    }


    // Editează pacient
    @PostMapping("/editPacient")
    public String editPacient(@RequestParam Integer id, @ModelAttribute("pacient") PacientDTO updatedPacientDTO) {
        service.editPacient(id, updatedPacientDTO);
        return "redirect:/pacients";
    }


    @GetMapping("/showEditPacient")
    public String showEditPacientPage(@RequestParam Integer id, Model model, HttpSession session) {

        UserDetails userDetails = (UserDetails) session.getAttribute("userDetails");
        if(!userDetails.getUserType().equals("admin"))
        {
            return "redirect:/";
        }

        Optional<PacientDTO> pacientDTOOptional = service.getPacientById(id);
        if (pacientDTOOptional.isPresent()) {
            model.addAttribute("pacient", pacientDTOOptional.get());
            return "editPacient";
        } else {
            // Tratează cazul în care pacientul nu este găsit
            return "redirect:/pacients";
        }
    }

    @PostMapping("/submitPacient")
    public String submitPacient(@ModelAttribute("pacient") PacientDTO pacientDTO,
                                Model model, HttpSession session) throws IOException, NoSuchAlgorithmException {
        pacientDTO.setUserType("pacient");
        if (service.emailExists(pacientDTO.getEmail())) {
            model.addAttribute("emailError2", "Email already exists.");
            return "pacientRegistrationForm";
        }

        Pacient pacientEntity = pacientMapper.mapToPacientEntity(pacientDTO);

        emailValidation.setEmail(pacientDTO.getEmail());
        session.setAttribute("emailValidation", emailValidation);

        service.savePacient(pacientEntity);
        model.addAttribute("msg", "Pacient registered successfully.");
        return "redirect:/validateAccount";
    }

    @PostMapping("/pacientRegistrationForm")
    public String submitPacientRegistrationForm(@ModelAttribute("pacient") PacientDTO pacientDTO,
                                   Model model) {
        model.addAttribute("pacient", new PacientDTO());
        return "pacientRegistrationForm";
    }
    @GetMapping("/pacientRegistrationForm")
    public String pacientRegistrationForm(Model model) {
        model.addAttribute("pacient", new PacientDTO());
        return "startPage";
    }


    @PostMapping("/deletePacientAccount")
    public String deletePacientAccount(@RequestParam Integer pacientID) {
        log.info("ReservationController.deletePacientAccount() has started...");
        service.deletePacient(pacientID);
        log.info("ReservationController.deletePacientAccount() has finished.");
        return "redirect:/";
    }

    @GetMapping("/myProfile")
    public String getMyProfile(HttpSession session, Model model){
        UserDetails userDetails = (UserDetails) session.getAttribute("userDetails");
        PacientDTO pacient = service.getPacientByEmail(userDetails.getEmail());
        model.addAttribute("pacient", pacient);
        return "myProfile";
    }


    @PostMapping("/editPacientProfile")
    public String editPacientProfile(@RequestParam Integer id, @ModelAttribute("pacient") PacientDTO updatedPacientDTO) {
        service.editPacient(id, updatedPacientDTO);
        return "redirect:/myProfile";
    }

    @GetMapping("/showEditPacientProfile")
    public String showEditPacientProfile(@RequestParam Integer id, Model model, HttpSession session) {
        UserDetails userDetails = (UserDetails) session.getAttribute("userDetails");
        Optional<PacientDTO> pacientDTOOptional = service.getPacientById(id);

        if (pacientDTOOptional.isPresent()) {
            model.addAttribute("pacient", pacientDTOOptional.get());
            return "editPacientProfile";
        } else {
            return "redirect:/myProfile";
        }
    }

}
