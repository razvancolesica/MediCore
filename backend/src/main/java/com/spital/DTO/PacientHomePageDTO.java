package com.spital.DTO;

import java.util.List;

public class PacientHomePageDTO {
    private PacientDTO pacient;
    private List<ReservationDTO> reservations;
    private int totalReservationsForPacient;
    private int totalReservations;
    private int totalPacients;
    private int totalSpecializations;
    private int totalDoctors;

    // Constructor, Getters, Setters
    public PacientHomePageDTO(PacientDTO pacient, List<ReservationDTO> reservations, int totalReservationsForPacient,
                              int totalReservations, int totalPacients, int totalSpecializations, int totalDoctors) {
        this.pacient = pacient;
        this.reservations = reservations;
        this.totalReservationsForPacient = totalReservationsForPacient;
        this.totalReservations = totalReservations;
        this.totalPacients = totalPacients;
        this.totalSpecializations = totalSpecializations;
        this.totalDoctors = totalDoctors;
    }

    // Getters and Setters

    public PacientDTO getPacient() {
        return pacient;
    }

    public void setPacient(PacientDTO pacient) {
        this.pacient = pacient;
    }

    public List<ReservationDTO> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationDTO> reservations) {
        this.reservations = reservations;
    }

    public int getTotalReservationsForPacient() {
        return totalReservationsForPacient;
    }

    public void setTotalReservationsForPacient(int totalReservationsForPacient) {
        this.totalReservationsForPacient = totalReservationsForPacient;
    }

    public int getTotalReservations() {
        return totalReservations;
    }

    public void setTotalReservations(int totalReservations) {
        this.totalReservations = totalReservations;
    }

    public int getTotalPacients() {
        return totalPacients;
    }

    public void setTotalPacients(int totalPacients) {
        this.totalPacients = totalPacients;
    }

    public int getTotalSpecializations() {
        return totalSpecializations;
    }

    public void setTotalSpecializations(int totalSpecializations) {
        this.totalSpecializations = totalSpecializations;
    }

    public int getTotalDoctors() {
        return totalDoctors;
    }

    public void setTotalDoctors(int totalDoctors) {
        this.totalDoctors = totalDoctors;
    }
}
