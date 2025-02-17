package com.spital.mapper;

import com.spital.DTO.PacientDTO;
import com.spital.entity.Pacient;
import org.springframework.stereotype.Component;

@Component
public class PacientMapper {

    public Pacient mapToPacientEntity(PacientDTO pacientDTO){
        Pacient pacient = new Pacient();
        pacient.setPacientID(pacientDTO.getPacientID());
        pacient.setFirstName(pacientDTO.getFirstName());
        pacient.setLastName(pacientDTO.getLastName());
        pacient.setAge(pacientDTO.getAge());
        pacient.setCnp(pacientDTO.getCnp());
        pacient.setEmail(pacientDTO.getEmail());
        pacient.setPassword(pacientDTO.getPassword());
        pacient.setPhoneNumber(pacientDTO.getPhoneNumber());
        pacient.setUserType(pacientDTO.getUserType());
        return pacient;
    }
}

