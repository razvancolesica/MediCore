package com.spital.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Integer id;

    @NotNull
    @Column(name = "pacient_id")
    private Integer pacientID;
    @NotNull
    @Column(name = "first_name")
    private String firstName;
    @NotNull
    @Column(name = "last_name")
    private String lastName;
    @NotNull
    @Column(name = "reservation_date")
    private LocalDateTime reservationDate;
    @NotNull
    @Column(name = "specialization")
    String specialization;
    @NotNull
    @Column(name = "issue")
    String issue;

}
