package com.spital.service;
import com.spital.DTO.PacientDTO;
import com.spital.DTO.ReservationDTO;
import com.spital.DTO.SpecializationDTO;
import com.spital.entity.Pacient;
import com.spital.entity.Reservation;
import com.spital.entity.Specialization;
import com.spital.repository.PacientRepository;
import com.spital.repository.ReservationRepository;
import com.spital.repository.SpecializationRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ReservationService {

    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    PacientRepository pacientRepository;
    @Autowired
    SpecializationRepository specializationRepository;

    @Autowired
    private EmailService emailService;

    ModelMapper mapper = new ModelMapper();

    //Afiseaza toate rezervarile
    public List<ReservationDTO> getAllReservations()
    {
        log.info("ReservationService.getReservations() retrieving all reservations...");
        return reservationRepository.findAll().stream()
                .map(reservation -> mapper.map(reservation,ReservationDTO.class))
                .collect(Collectors.toList());
    }

    // Obține un pacient în funcție de nume pentru adaugarea rezervarii
    public PacientDTO getPacientByName(String firstName, String lastName) {
        log.info("ReservationService.getPacientByName(String firstName, String lastName) retrieving pacient by name...");
        Optional<Pacient> pacientOptional = pacientRepository.findByFirstNameAndLastName(firstName, lastName);
        return pacientOptional.map(p -> mapper.map(p, PacientDTO.class)).orElse(null);
    }

    public SpecializationDTO getSpecializationByName(String specializationName) {
        log.info("ReservationService.getSpecializationByName(String specializationName) retrieving specialization by name...");
        Optional<Specialization> specializationOptional = specializationRepository.findBySpecializationName(specializationName);
        return specializationOptional.map(s -> mapper.map(s, SpecializationDTO.class)).orElse(null);
    }

    // Adaugă rezervare
    public ReservationDTO addReservation(ReservationDTO reservationDTO) {
        log.info("ReservationService.addReservation(ReservationDTO reservationDTO) adding reservation...");

        SpecializationDTO specialization = getSpecializationByName(reservationDTO.getSpecialization());
        if (specialization != null) {
            // Setăm numele specializării în obiectul ReservationDTO
            reservationDTO.setSpecialization(specialization.getSpecializationName());
        } else {
            // Dacă specializarea nu există, afișăm un mesaj de eroare și returnăm null
            log.error("Specialization does not exist!");
            return null;
        }
        // Căutăm pacientul în funcție de nume
        PacientDTO pacient = getPacientByName(reservationDTO.getFirstName(), reservationDTO.getLastName());
        emailService.sendReservationDoneEmail(pacient.getEmail(), pacient.getFirstName(), pacient.getLastName(), reservationDTO.getReservationDate(), reservationDTO.getSpecialization());
        if (pacient != null) {
            // Dacă pacientul există, setăm id-ul rezervării conform acestuia
            reservationDTO.setPacientID(pacient.getPacientID());
            Reservation reservation = mapper.map(reservationDTO, Reservation.class);
            reservationRepository.save(reservation);
            return mapper.map(reservation, ReservationDTO.class);
        } else {
            // Dacă pacientul nu există, afișăm un mesaj de eroare
            log.error("Pacient does not exist!");
            return null;
        }
    }

    //Sterge rezervare
    public void deleteReservation(Integer reservationID) {
        log.info("ReservationService.deleteReservation(String reservationID) deleting reservation...");
        reservationRepository.deleteById(String.valueOf(reservationID));
    }


    //Editeaza rezervare
    public ReservationDTO editReservation(Integer reservationID, ReservationDTO updatedRezervationDTO)
    {
        log.info("ReservationService.editReservation(Integer reservationID, ReservationDTO updatedRezervationDTO) editing reservation...");
        Optional<Reservation> reservationOptional = reservationRepository.findById(String.valueOf(reservationID));
        if (reservationOptional.isPresent()) {
            Reservation reservation = reservationOptional.get();

            // Actualizăm valorile rezervării cu cele primite
            reservation.setFirstName(updatedRezervationDTO.getFirstName());
            reservation.setLastName(updatedRezervationDTO.getLastName());
            reservation.setSpecialization(updatedRezervationDTO.getSpecialization());
            reservation.setReservationDate(updatedRezervationDTO.getReservationDate());

            // Salvăm rezervarea actualizată
            reservationRepository.save(reservation);

            return mapper.map(reservation, ReservationDTO.class);
        } else {
            log.error("Reservation with ID " + reservationID + " not found.");
            return null;
        }
    }



    //Afiseaza rezervarea dupa ID-ul pacientului.
    public List<ReservationDTO> getReservationsForPacient(Integer pacientID) {
        log.info("ReservationService.getReservationsForPacient(Integer pacientID) retrieving reservations for pacient...");
        return reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getPacientID().equals(pacientID))
                .map(reservation -> mapper.map(reservation, ReservationDTO.class))
                .collect(Collectors.toList());
    }


    public Optional<ReservationDTO> getReservationById(Integer id) {
        log.info("ReservationService.getReservationById(Integer id) retrieving reservation by ID...");
        Optional<Reservation> reservationOptional = reservationRepository.findById(String.valueOf(id));
        return reservationOptional.map(r -> mapper.map(r, ReservationDTO.class));
    }

    public List<ReservationDTO> getReservationsByDate(LocalDate date) {
        log.info("ReservationService.getReservationsByDate(LocalDate date) retrieving reservations for date...");
        return reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getReservationDate().toLocalDate().equals(date))
                .map(reservation -> mapper.map(reservation, ReservationDTO.class))
                .collect(Collectors.toList());
    }
}
