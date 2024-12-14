package com.spital.service;
import com.spital.DTO.PacientDTO;
import com.spital.DTO.ReservationDTO;
import com.spital.entity.Pacient;
import com.spital.repository.AdminRepository;
import com.spital.repository.PacientRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class PacientService {

    @Autowired
    PacientRepository pacientRepository;
    @Autowired
    AdminRepository adminRepository;
    @Autowired
    ReservationService reservationService;


    ModelMapper mapper = new ModelMapper();



    //Afiseaza toti pacientii.
    public List<PacientDTO> getAllPacients()
    {
        log.info("ReservationService.getAllPacients() retrieving all pacients...");
        return pacientRepository.findAll().stream()
                .map(pacient -> mapper.map(pacient,PacientDTO.class))
                .collect(Collectors.toList());
    }


    //Adauga pacient
    public PacientDTO addPacient(PacientDTO pacientDTO)
    {
        log.info("ReservationService.addPacient(Pacient pacient) adding pacient...");
        Pacient newPacient = mapper.map(pacientDTO, Pacient.class);
        List<PacientDTO> pacients = getAllPacients();
        for(PacientDTO pacient : pacients)
        {
            if(pacient.getFirstName().equals(newPacient.getFirstName()) && pacient.getLastName().equals(newPacient.getLastName()))
            {
                return null;
            }
        }
        pacientRepository.save(newPacient);
        return mapper.map(newPacient, PacientDTO.class);
    }


    //Sterge pacient
    public void deletePacient(Integer pacientID)
    {
        log.info("ReservationService.deletePacient(String pacientID) deleting pacient...");
        pacientRepository.deleteById(String.valueOf(pacientID));
        List<ReservationDTO> reservationDTO = reservationService.getAllReservations();
        for(ReservationDTO reservation : reservationDTO)
        {
            if(reservation.getPacientID().equals(pacientID))
            {
                reservationService.deleteReservation(reservation.getId());
            }
        }
    }


    //Editeaza pacient
    public PacientDTO editPacient(Integer pacientID, PacientDTO updatedPacientDTO) {
        log.info("ReservationService.editPacient(Integer pacientID, PacientDTO updatedPacientDTO) editing pacient...");

        Optional<Pacient> pacientOptional = pacientRepository.findById(String.valueOf(pacientID));
        if (pacientOptional.isPresent()) {
            Pacient pacient = pacientOptional.get();

            // Actualizăm valorile pacientului cu cele primite
            pacient.setFirstName(updatedPacientDTO.getFirstName());
            pacient.setLastName(updatedPacientDTO.getLastName());
            pacient.setCnp(updatedPacientDTO.getCnp());
            pacient.setAge(updatedPacientDTO.getAge());
            pacient.setEmail(updatedPacientDTO.getEmail());
            pacient.setPhoneNumber(updatedPacientDTO.getPhoneNumber());

            // Salvăm modificările în baza de date
            pacientRepository.save(pacient);

            return mapper.map(pacient, PacientDTO.class);
        } else {
            log.error("Pacient with ID " + pacientID + " not found.");
            return null;
        }
    }


    public Optional<PacientDTO> getPacientById(Integer pacientID) {
        log.info("ReservationService.getPacientById(Integer pacientID) retrieving pacient by ID...");
        Optional<Pacient> pacientOptional = pacientRepository.findById(String.valueOf(pacientID));
        return pacientOptional.map(p -> mapper.map(p, PacientDTO.class));
    }

    public PacientDTO getPacientByEmail(String email) {
        Optional<Pacient> optionalPacient = pacientRepository.findByEmail(email);

        if (optionalPacient.isPresent()) {
            Pacient pacient = optionalPacient.get();

            PacientDTO pacientDTO = new PacientDTO();
            pacientDTO.setPacientID(pacient.getPacientID());
            pacientDTO.setFirstName(pacient.getFirstName());
            pacientDTO.setLastName(pacient.getLastName());
            pacientDTO.setEmail(pacient.getEmail());
            pacientDTO.setAge(pacient.getAge());
            pacientDTO.setCnp(pacient.getCnp());
            pacientDTO.setPhoneNumber(pacient.getPhoneNumber());
            return pacientDTO;
        } else {
            return null;
        }
    }

    public boolean emailExists(String email) {
        return pacientRepository.findByEmail(email).isPresent() || adminRepository.findByEmail(email).isPresent();
    }

    public void savePacient(Pacient pacient)
    {
        pacientRepository.save(pacient);
    }

}
