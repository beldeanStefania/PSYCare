package com.goodfellas.backend.repository;

import com.goodfellas.backend.model.Psychologist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PsychologistRepository extends JpaRepository<Psychologist, Integer>
{
    Optional<Psychologist> findByUsername(String username);
    Boolean existsByUsername(String username);
}
