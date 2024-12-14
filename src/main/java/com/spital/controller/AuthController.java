package com.spital.controller;

import com.spital.DTO.UserDetails;
import com.spital.entity.Admin;
import com.spital.entity.Pacient;
import com.spital.repository.AdminRepository;
import com.spital.repository.PacientRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller

public class AuthController {

    private UserDetails userDetails=new UserDetails();

    @Autowired
    private PacientRepository pacientRepository;
    @Autowired
    private AdminRepository adminRepository;


    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, RedirectAttributes redirectAttributes,
                        HttpSession session) throws NoSuchAlgorithmException{

        String emailRegex = "^[A-Za-z\\d]+@[A-Za-z\\d]+\\.[A-Za-z\\d]+$";
        Pattern emailPattern = Pattern.compile(emailRegex);
        Matcher emailMatcher = emailPattern.matcher(email);

        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]+$";
        Pattern passwordPattern = Pattern.compile(passwordRegex);
        Matcher passwordMatcher = passwordPattern.matcher(password);

        userDetails.setPassword(password);
        userDetails.setEmail(email);

        Optional<Pacient> pacientUser=pacientRepository.findByEmail(email);
        System.out.println("Pacient user " +  pacientUser.isPresent());
        Optional<Admin> adminUser=adminRepository.findByEmail(email);
        System.out.println("Admin user " +  adminUser.isPresent());

        if(pacientUser.isPresent())
            userDetails.setUserType("pacient");
        else if(adminUser.isPresent())
            userDetails.setUserType("admin");
        session.setAttribute("userDetails", userDetails);

        if (pacientUser.isPresent()) {
            if (userDetails.getPassword().equals(pacientUser.get().getPassword())){
                return "redirect:/homePagePacient";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid pacient credentials.");
            }
        } else if (adminUser.isPresent()) {
            if (userDetails.getPassword().equals(adminUser.get().getPassword())) {
                return "redirect:/homePageAdmin";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid admin credentials.");
            }
        }
        if(userDetails.getEmail()!= null) System.out.println(userDetails.getUserType()+" "+userDetails.getEmail()+" "+ userDetails.getPassword());
        else System.out.println("null");


        if (!emailMatcher.find()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid email or password!");
            return "redirect:/";
        }

        if (!passwordMatcher.find()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid email or password!");
            return "redirect:/";
        }

        if (email.isEmpty() || password.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email and password must not be empty.");
            return "redirect:/";
        }

        return "redirect:/";
    }
}
