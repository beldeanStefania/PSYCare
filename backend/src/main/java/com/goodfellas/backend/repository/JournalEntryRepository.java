package com.goodfellas.backend.repository;

import com.goodfellas.backend.model.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Integer>
{
    List<JournalEntry> findByPatient_Username(String username);
}