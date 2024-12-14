package com.spital.repository;

import com.spital.entity.Pacient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PacientRepository extends JpaRepository<Pacient,String> {

    Optional<Pacient> findByFirstNameAndLastName(String firstName, String lastName);
    Optional<Pacient> findByEmail(String email);
}
