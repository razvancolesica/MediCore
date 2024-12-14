package com.spital.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpecializationDTO {
    private Integer specializationID;
    private String specializationName;
    private String medic;
    private String room;

}
