package com.spital.DTO;

import com.spital.entity.Specialization;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private String reservationTime;
    private String specialization;
    private String issue;
}
