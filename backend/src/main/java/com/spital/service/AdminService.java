package com.spital.service;

import com.spital.DTO.AdminDTO;
import com.spital.DTO.PacientDTO;
import com.spital.entity.Admin;
import com.spital.entity.Pacient;
import com.spital.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service

public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public AdminDTO getAdminByEmail(String email) {
        Optional<Admin> optionalAdmin = adminRepository.findByEmail(email);

        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();

            AdminDTO adminDTO = new AdminDTO();
            adminDTO.setAdminID(admin.getAdminID());
            adminDTO.setEmail(admin.getEmail());
            return adminDTO;
        } else {
            return null;
        }
    }
}
