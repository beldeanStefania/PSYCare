package com.goodfellas.backend.repository;

import com.goodfellas.backend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Integer>
{
    Optional<Patient> findByUsername(String username);
    Boolean existsByUsername(String username);
}
