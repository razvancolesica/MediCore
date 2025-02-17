package com.spital.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "pacient")
public class Pacient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pacient_id")
    private Integer pacientID;

    @NotNull
    @Column(name = "first_name")
    private String firstName;
    @NotNull
    @Column(name = "last_name")
    private String lastName;
    @NotNull
    @Column(name = "age")
    private int age;
    @NotNull
    @Column(name = "CNP")
    private String cnp;
    @NotNull
    @Column(name = "email")
    private String email;
    @NotNull
    @Column(name = "password_pacient")
    private String password;
    @NotNull
    @Column(name = "phone_number")
    private String phoneNumber;
    @NotNull
    @Column(name = "user_type")
    private String userType;
}
