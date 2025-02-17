package com.spital.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetails {
    private String email;
    private String password;
    private String userType;
}
