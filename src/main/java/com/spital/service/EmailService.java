package com.spital.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendResetCodeEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reset your password");
        message.setText("Your reset code is: " + code);
        emailSender.send(message);
    }
    public void sendValidateEmail(String to, String code)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Validate your account");
        message.setText("Your validation code is: " + code);
        emailSender.send(message);
    }

    public void sendReservationDoneEmail(String to, String firstName, String lastName, LocalDateTime date, String specialization) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reservation submitted successfully");
        message.setText("Dear " + firstName + " " + lastName + ", your reservation for " + date + " at " + specialization + " has been submitted successfully.");
        emailSender.send(message);
    }
}
