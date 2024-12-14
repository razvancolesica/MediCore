package com.spital.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.id.GUIDGenerator;

@Getter
@Setter
@Entity
@Table(name = "administrator")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Integer adminID;

    @NotNull
    @Column(name = "email")
    private String email;

    @NotNull
    @Column(name = "password_admin")
    private String password;

    @NotNull
    @Column(name = "user_type")
    private String userType;

}
