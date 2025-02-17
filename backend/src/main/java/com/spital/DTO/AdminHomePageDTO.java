package com.spital.DTO;

public class AdminHomePageDTO {
    private AdminDTO admin;
    private int totalReservations;
    private int totalPacients;
    private int totalSpecializations;
    private int totalDoctors;

    // Constructor, Getters, Setters
    public AdminHomePageDTO(AdminDTO admin, int totalReservations, int totalPacients, int totalSpecializations, int totalDoctors) {
        this.admin = admin;
        this.totalReservations = totalReservations;
        this.totalPacients = totalPacients;
        this.totalSpecializations = totalSpecializations;
        this.totalDoctors = totalDoctors;
    }

    // Getters and Setters

    public AdminDTO getAdmin() {
        return admin;
    }

    public void setAdmin(AdminDTO admin) {
        this.admin = admin;
    }

    public int getTotalDoctors() {
        return totalDoctors;
    }

    public void setTotalDoctors(int totalDoctors) {
        this.totalDoctors = totalDoctors;
    }

    public int getTotalSpecializations() {
        return totalSpecializations;
    }

    public void setTotalSpecializations(int totalSpecializations) {
        this.totalSpecializations = totalSpecializations;
    }

    public int getTotalPacients() {
        return totalPacients;
    }

    public void setTotalPacients(int totalPacients) {
        this.totalPacients = totalPacients;
    }

    public int getTotalReservations() {
        return totalReservations;
    }

    public void setTotalReservations(int totalReservations) {
        this.totalReservations = totalReservations;
    }
}
