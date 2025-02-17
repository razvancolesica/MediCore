package com.spital.service;
import com.spital.DTO.PacientDTO;
import com.spital.DTO.PacientHomePageDTO;
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
    @Autowired
    SpecializationService specializationService;

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


    //register pacient
    public PacientDTO registerPacient(PacientDTO pacientDTO) {
        log.info("PacientService.registerPacient() registering pacient...");

        // Verificăm dacă email-ul pacientului există deja
        if (emailExists(pacientDTO.getEmail())) {
            log.warn("Email already exists: " + pacientDTO.getEmail());
            return null; // Sau returnăm un mesaj de eroare mai detaliat, dacă este cazul
        }

        // Creăm un obiect Pacient din DTO
        Pacient pacient = mapper.map(pacientDTO, Pacient.class);

        // Salvăm pacientul în baza de date
        pacientRepository.save(pacient);

        // Returnăm DTO-ul pacientului salvat
        return mapper.map(pacient, PacientDTO.class);
    }


    public PacientHomePageDTO getPacientHomePageDTO(String email) {
        log.info("PacientService.getPacientHomePageDTO() retrieving pacient profile for email: " + email);

        // Căutăm pacientul în baza de date pe baza email-ului
        Optional<Pacient> optionalPacient = pacientRepository.findByEmail(email);

        if (optionalPacient.isPresent()) {
            Pacient pacient = optionalPacient.get();

            // Creăm obiectul PacientDTO
            PacientDTO pacientDTO = mapper.map(pacient, PacientDTO.class);

            // Obținem rezervările pacientului
            List<ReservationDTO> reservations = reservationService.getReservationsForPacient(pacient.getPacientID());

            // Calculăm totalul rezervărilor pentru pacient
            int totalReservationsForPacient = reservations.size();

            // Calculăm totalul rezervărilor
            int totalReservations = reservationService.getAllReservations().size();

            // Calculăm totalul pacienților
            int totalPacients = getAllPacients().size();

            // Calculăm totalul specializărilor
            int totalSpecializations = specializationService.getAllSpecializations().size();

            // Calculăm totalul medicilor
            int totalDoctors = specializationService.getAllSpecializations().size();

            // Creăm obiectul PacientHomePageDTO
            PacientHomePageDTO pacientHomePageDTO = new PacientHomePageDTO(
                    pacientDTO,
                    reservations,
                    totalReservationsForPacient,
                    totalReservations,
                    totalPacients,
                    totalSpecializations,
                    totalDoctors
            );

            return pacientHomePageDTO;
        } else {
            log.warn("Pacient not found with email: " + email);
            return null; // Sau ar putea arunca o excepție sau returna un mesaj de eroare
        }
    }



}
