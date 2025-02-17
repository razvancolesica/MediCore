package com.spital.service;

import com.spital.DTO.PacientDTO;
import com.spital.DTO.ReservationDTO;
import com.spital.DTO.SpecializationDTO;
import com.spital.entity.Pacient;
import com.spital.entity.Specialization;
import com.spital.repository.SpecializationRepository;
import io.micrometer.observation.ObservationFilter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class SpecializationService {

    @Autowired
    SpecializationRepository specializationRepository;
    ModelMapper mapper = new ModelMapper();

    public List<SpecializationDTO> getAllSpecializations()
    {
        log.info("SpecializationService.getAllSpecializations() retrieving all specializations...");
        return specializationRepository.findAll().stream()
                .map(specialization -> mapper.map(specialization,SpecializationDTO.class))
                .collect(Collectors.toList());
    }

    public SpecializationDTO addSpecialization(SpecializationDTO specializationDTO)
    {
        log.info("SpecializationService.addSpecialization(Specialization specialization) adding specialization...");
        Specialization newSpecialization = mapper.map(specializationDTO, Specialization.class);
        List<SpecializationDTO> specializations = getAllSpecializations();
        for(SpecializationDTO specialization : specializations)
        {
            if(specialization.getSpecializationName().equals(newSpecialization.getSpecializationName()) && specialization.getMedic().equals(newSpecialization.getMedic()))
            {
                return null;
            }
        }
        specializationRepository.save(newSpecialization);
        return mapper.map(newSpecialization, SpecializationDTO.class);
    }

    public SpecializationDTO editSpecialization(Integer specializationID, SpecializationDTO updatedSpecializationDTO) {
        log.info("SpecializationService.editSpecialization(Integer specializationID, SpecializationDTO updatedSpecializationDTO) editing specialization...");

        Optional<Specialization> specializationOptional = specializationRepository.findById(String.valueOf(specializationID));
        if (specializationOptional.isPresent()) {
            Specialization specialization = specializationOptional.get();

            // Actualizăm valorile specializării cu cele primite
            specialization.setSpecializationName(updatedSpecializationDTO.getSpecializationName());
            specialization.setMedic(updatedSpecializationDTO.getMedic());
            specialization.setRoom(updatedSpecializationDTO.getRoom());

            // Salvăm modificările în baza de date
            specializationRepository.save(specialization);

            return mapper.map(specialization, SpecializationDTO.class);
        } else {
            log.error("Specialization with ID " + specializationID + " not found.");
            return null;
        }
    }

    public Optional<SpecializationDTO> getSpecializationById(Integer specializationID) {
        log.info("SpecializationService.getSpecializationById(Integer specializationID) retrieving specialization by ID...");
        Optional<Specialization> specializationOptional = specializationRepository.findById(String.valueOf(specializationID));
        return specializationOptional.map(s -> mapper.map(s, SpecializationDTO.class));
    }

    public void deleteSpecialization(Integer specializationID)
    {
        log.info("SpecializationService.deleteSpecialization(String specializationID) deleting specialization...");
        specializationRepository.deleteById(String.valueOf(specializationID));
    }
}
