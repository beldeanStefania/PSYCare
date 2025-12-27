package com.goodfellas.backend.service;

import com.goodfellas.backend.dto.JournalEntryDTO;
import com.goodfellas.backend.repository.JournalEntryRepository;
import com.goodfellas.backend.repository.PsychologistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PsychologistService {

    private final PsychologistRepository psychologistRepository;

    private final JournalEntryRepository journalEntryRepository;

    @Autowired
    public PsychologistService(PsychologistRepository psychologistRepository, JournalEntryRepository journalEntryRepository) {
        this.psychologistRepository = psychologistRepository;
        this.journalEntryRepository = journalEntryRepository;
    }


    public List<JournalEntryDTO> getSharedJournalEntries(String username) {

        var psychologist = psychologistRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Psychologist not found"));

        return journalEntryRepository
                .findSharedEntriesForPsychologist(psychologist.getId())
                .stream()
                .map(je -> {
                    var dto = new JournalEntryDTO();
                    dto.setId(je.getId());
                    dto.setTitle(je.getTitle());
                    dto.setText(je.getText());
                    dto.setTags(je.getTags());
                    dto.setDate(je.getDate());
                    dto.setAllowPsychologist(je.isAllowPsychologist());
                    return dto;
                })
                .toList();
    }
}
