package com.spital.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {

    private Integer id;
    private Integer pacientID;
    private String firstName;
    private String lastName;
    private LocalDateTime reservationDate;
    private String specialization;
    private String issue;
}
