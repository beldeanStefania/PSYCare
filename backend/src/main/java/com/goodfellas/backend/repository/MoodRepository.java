package com.goodfellas.backend.repository;

import com.goodfellas.backend.model.Mood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MoodRepository extends JpaRepository<Mood, Integer>
{
    List<Mood> findByPatient_Username(String username);
    Optional<Mood> findByPatient_UsernameAndDate(String username, LocalDate date);
}