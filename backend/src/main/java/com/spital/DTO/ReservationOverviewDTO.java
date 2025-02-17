package com.spital.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ReservationOverviewDTO {
    @Setter
    @Getter
    private List<ReservationDTO> reservationList;
    @Setter
    @Getter
    private boolean hasReservations;
    private boolean isFutureDate;

    // Constructor, Getters, Setters
    public ReservationOverviewDTO(List<ReservationDTO> reservationList, boolean hasReservations, boolean isFutureDate) {
        this.reservationList = reservationList;
        this.hasReservations = hasReservations;
        this.isFutureDate = isFutureDate;
    }

    // Getters and Setters

    public boolean isFutureDate() {
        return isFutureDate;
    }

    public void setFutureDate(boolean futureDate) {
        isFutureDate = futureDate;
    }
}
