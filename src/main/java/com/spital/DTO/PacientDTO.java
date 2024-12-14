package com.spital.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PacientDTO {

    private Integer pacientID;
    private String firstName;
    private String lastName;
    private int age;
    private String cnp;
    private String email;
    private String password;
    private String phoneNumber;
    private String userType;
}
