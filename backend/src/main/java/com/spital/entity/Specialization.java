package com.spital.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "specialization")
public class Specialization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "specialization_id")
    private Integer specializationID;

    @NotNull
    @Column(name = "specialization_name")
    private String specializationName;

    @NotNull
    @Column(name = "medic")
    private String medic;

    @NotNull
    @Column(name = "room")
    private String room;
}
