package com.spital.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "password_reset")
public class PasswordReset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_password_reset" )
    private Integer idPasswordReset;

    @Column(name = "email")
    private String email;

    @Column(name = "reset_code")
    private String resetCode;

}
