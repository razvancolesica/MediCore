package com.spital.service;

import com.spital.entity.Admin;
import com.spital.entity.Pacient;
import com.spital.entity.PasswordReset;
import com.spital.repository.AdminRepository;
import com.spital.repository.PacientRepository;
import com.spital.repository.PasswordResetRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
public class PasswordResetService {

    @Autowired
    private EmailService emailService;
    @Autowired
    private PacientRepository pacientRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private PasswordResetRepository passwordResetRepository;


    public void generateAndSaveResetCode(String email) {
        String resetCode = UUID.randomUUID().toString().substring(0, 6);
        PasswordReset passwordReset = PasswordReset.builder()
                .email(email)
                .resetCode(resetCode)
                .build();

        passwordResetRepository.save(passwordReset);
        emailService.sendResetCodeEmail(email, resetCode);
    }
    public void generateAndSaveResetCodeValidation(String email) {
        String resetCode = UUID.randomUUID().toString().substring(0, 6);
        PasswordReset passwordReset = PasswordReset.builder()
                .email(email)
                .resetCode(resetCode)
                .build();

        passwordResetRepository.save(passwordReset);
        emailService.sendValidateEmail(email, resetCode);
    }


    public boolean verifyResetCode(String email, String inputCode) {
        List<PasswordReset> resets = passwordResetRepository.findByEmail(email); //det email
        if (!resets.isEmpty()) {
            PasswordReset latestReset = resets.stream()
                    .max(Comparator.comparing(PasswordReset::getIdPasswordReset))
                    .orElse(null);
            return latestReset.getResetCode().equals(inputCode);
        }
        return false;
    }
    public void updatePacientPassword(String email, String newPassword) throws NoSuchAlgorithmException {
        Optional<Pacient> pacientOptional = pacientRepository.findByEmail(email);
        Pacient pacient = pacientOptional.get();
        pacient.setPassword(newPassword);
        pacientRepository.save(pacient);
    }
    public void updateAdminPassword(String email, String newPassword) throws NoSuchAlgorithmException {
        Optional<Admin>adminOptional = adminRepository.findByEmail(email);
        Admin admin = adminOptional.get();
        admin.setPassword(newPassword);
        adminRepository.save(admin);
    }
}
