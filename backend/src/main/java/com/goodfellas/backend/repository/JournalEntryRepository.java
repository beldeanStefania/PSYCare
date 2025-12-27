package com.goodfellas.backend.repository;

import com.goodfellas.backend.model.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Integer>
{
    List<JournalEntry> findByPatient_Username(String username);

    @Query("""
        select je
        from JournalEntry je
        join fetch je.patient p
        where p.psychologist.id = :psychologistId
          and je.allowPsychologist = true
        order by je.date desc
    """)
    List<JournalEntry> findSharedEntriesForPsychologist(int psychologistId);
}