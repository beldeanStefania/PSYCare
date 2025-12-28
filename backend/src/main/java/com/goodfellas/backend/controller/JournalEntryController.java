package com.goodfellas.backend.controller;

import com.goodfellas.backend.dto.JournalEntryDTO;
import com.goodfellas.backend.service.JournalEntryService;
import com.goodfellas.backend.service.PsychologistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    private final JournalEntryService journalService;

    private final PsychologistService psychologistService;

    @Autowired
    public JournalEntryController(JournalEntryService journalService, PsychologistService psychologistService)
    {
        this.journalService = journalService;
        this.psychologistService = psychologistService;
    }

    @GetMapping
    public ResponseEntity<List<JournalEntryDTO>> getMyEntries(Authentication authentication)
    {
        return ResponseEntity.ok(journalService.getMyEntries(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JournalEntryDTO> getMyEntry(@PathVariable int id, Authentication authentication)
    {
        return ResponseEntity.ok(journalService.getEntry(authentication.getName(), id));
    }

    /**
     *
     * @param authentication
     * @return shared journals
     */
    @GetMapping("/shared")
    public ResponseEntity<List<JournalEntryDTO>> getSharedJournalEntries(
            Authentication authentication) {

        return ResponseEntity.ok(
                psychologistService.getSharedJournalEntries(authentication.getName())
        );
    }

    @PostMapping
    public ResponseEntity<String> createEntry(@RequestBody JournalEntryDTO dto, Authentication authentication)
    {
        journalService.createEntry(authentication.getName(), dto);
        return new ResponseEntity<>("Journal entry created successfully", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateEntry(@PathVariable int id, @RequestBody JournalEntryDTO dto, Authentication authentication)
    {
        try
        {
            journalService.updateEntry(authentication.getName(), id, dto);
            return ResponseEntity.ok("Journal entry updated successfully");
        }
        catch (RuntimeException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEntry(@PathVariable int id, Authentication authentication)
    {
        try
        {
            journalService.deleteEntry(authentication.getName(), id);
            return ResponseEntity.ok("Journal entry deleted successfully");
        }
        catch (RuntimeException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/share")
    public ResponseEntity<String> shareWithPsychologist(@PathVariable int id, Authentication authentication)
    {
        try
        {
            journalService.shareWithPsychologist(authentication.getName(), id);
            return ResponseEntity.ok("Journal entry shared with your psychologist.");
        }
        catch (RuntimeException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}