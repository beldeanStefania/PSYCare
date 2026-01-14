package com.goodfellas.backend.service;

import com.goodfellas.backend.dto.JournalEntryDTO;
import com.goodfellas.backend.model.JournalEntry;
import com.goodfellas.backend.model.Patient;
import com.goodfellas.backend.repository.JournalEntryRepository;
import com.goodfellas.backend.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JournalEntryService
{
    private final JournalEntryRepository journalRepository;
    private final PatientRepository patientRepository;

    @Autowired
    public JournalEntryService(JournalEntryRepository journalRepository, PatientRepository patientRepository)
    {
        this.journalRepository = journalRepository;
        this.patientRepository = patientRepository;
    }

    public List<JournalEntryDTO> getMyEntries(String username)
    {
        return journalRepository.findByPatient_Username(username).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public JournalEntryDTO getEntry(String username, int entryId)
    {
        JournalEntry entry = getEntryIfOwner(username, entryId);
        return mapToDTO(entry);
    }

    public void createEntry(String username, JournalEntryDTO dto)
    {
        Patient patient = patientRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Patient not found"));

        JournalEntry entry = new JournalEntry();
        entry.setTitle(dto.getTitle());
        entry.setText(dto.getText());
        entry.setTags(dto.getTags());
        entry.setDate(LocalDateTime.now());
        entry.setAllowPsychologist(false);
        entry.setPatient(patient);

        journalRepository.save(entry);
    }

    public void updateEntry(String username, int entryId, JournalEntryDTO dto)
    {
        JournalEntry entry = getEntryIfOwner(username, entryId);

        entry.setTitle(dto.getTitle());
        entry.setText(dto.getText());
        entry.setTags(dto.getTags());
        entry.setDate(LocalDateTime.now());

        journalRepository.save(entry);
    }

    public void deleteEntry(String username, int entryId)
    {
        JournalEntry entry = getEntryIfOwner(username, entryId);
        journalRepository.delete(entry);
    }

    public void shareWithPsychologist(String username, int entryId)
    {
        JournalEntry entry = getEntryIfOwner(username, entryId);
        Patient patient = entry.getPatient();

        if (patient.getPsychologist() == null)
        {
            throw new RuntimeException("Cannot share entry: You are not assigned to a psychologist.");
        }

        entry.setAllowPsychologist(true);
        journalRepository.save(entry);
    }

    private JournalEntry getEntryIfOwner(String username, int entryId)
    {
        JournalEntry entry = journalRepository.findById(entryId).orElseThrow(() -> new RuntimeException("Journal entry not found"));

        if (!entry.getPatient().getUsername().equals(username))
        {
            throw new AccessDeniedException("You do not have permission to modify this entry");
        }
        return entry;
    }

    private JournalEntryDTO mapToDTO(JournalEntry entry)
    {
        JournalEntryDTO dto = new JournalEntryDTO();
        dto.setId(entry.getId());
        dto.setTitle(entry.getTitle());
        dto.setText(entry.getText());
        dto.setTags(entry.getTags());
        dto.setDate(entry.getDate());
        dto.setAllowPsychologist(entry.isAllowPsychologist());
        dto.setPatientId(entry.getPatient().getId());
        return dto;
    }
}