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

    /**
     ENDPOINT: GET /journal
     FUNCTION: Retrieves all journal entries belonging to the currently authenticated patient.
     @param authentication The security context containing the current user's details.
     INPUT: None (However Uses the JWT from the Authorization !!header!! to identify the user).
     @return A list of journal entries belonging to the authenticated user.
     OUTPUT:
     - 200 OK: A List of JournalEntryDTO objects.
     - 401 UNAUTHORIZED: If the token is missing or invalid.
     */
    @GetMapping
    public ResponseEntity<List<JournalEntryDTO>> getJournalEntriesOfPatient(Authentication authentication)
    {
        return ResponseEntity.ok(journalService.getMyEntries(authentication.getName()));
    }

    /**
     ENDPOINT: GET /journal/{id}
     FUNCTION: Retrieves a specific journal entry by its ID, provided the authenticated user owns it.
     @param id The ID of the journal entry to retrieve.
     @param authentication The security context containing the current user's details.
     INPUT: Path Variable: 'id' (Integer). (However Uses the JWT from the Authorization !!header!! to identify the user).
     @return The specific journal entry DTO.
     OUTPUT:
     - 200 OK: The JournalEntryDTO object.
     - 403 FORBIDDEN: If the user tries to access an entry that doesn't belong to them.
     - 404 NOT FOUND: If the entry ID does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<JournalEntryDTO> getJournalEntryOfPatientById(@PathVariable int id, Authentication authentication)
    {
        return ResponseEntity.ok(journalService.getEntry(authentication.getName(), id));
    }

    /**
     * @param authentication
     * @return shared journals
     */
    @GetMapping("/shared")
    public ResponseEntity<List<JournalEntryDTO>> getSharedJournalEntriesOfPatient(Authentication authentication)
    {
        return ResponseEntity.ok(psychologistService.getSharedJournalEntries(authentication.getName()));
    }

    /**
     ENDPOINT: POST /journal
     FUNCTION: Creates a new journal entry for the authenticated patient.
     @param dto The data transfer object containing the journal entry content.
     @param authentication The security context identifying the patient.
     INPUT (JSON): (However Uses the JWT from the Authorization !!header!! to identify the user).
     {
     "title": "My Day",
     "text": "Today I felt much better",
     "tags": "progress, happy"
     }
     @return A confirmation message indicating the resource was created.
     OUTPUT:
     - 201 CREATED: "Journal entry created successfully"
     - 400 BAD REQUEST: If the user is not found or data is invalid.
     */
    @PostMapping
    public ResponseEntity<String> createJournalEntryOfPatient(@RequestBody JournalEntryDTO dto, Authentication authentication)
    {
        journalService.createEntry(authentication.getName(), dto);
        return new ResponseEntity<>("Journal entry created successfully", HttpStatus.CREATED);
    }

    /**
     ENDPOINT: PUT /journal/{id}
     FUNCTION:
     Updates an existing journal entry. The user must be the owner.
     @param id The ID of the journal entry to update.
     @param dto The new data to apply to the journal entry.
     @param authentication The security context identifying the patient.
     INPUT (JSON): (However Uses the JWT from the Authorization !!header!! to identify the user).
     {
     "title": "Updated Title",
     "text": "Updated content",
     "tags": "updated"
     }
     @return A confirmation message on success or error message on failure.
     OUTPUT:
     - 200 OK: "Journal entry updated successfully"
     - 403 FORBIDDEN: If the user does not own the entry.
     - 400 BAD REQUEST: If the update logic fails.
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateJournalEntryOfPatientById(@PathVariable int id, @RequestBody JournalEntryDTO dto, Authentication authentication)
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

    /**
     ENDPOINT: DELETE /journal/{id}
     FUNCTION: Deletes a journal entry. Ownership is verified before deletion.
     @param id The ID of the journal entry to delete.
     @param authentication The security context identifying the patient.
     INPUT: Path Variable: 'id' (Integer). (However Uses the JWT from the Authorization !!header!! to identify the user).
     @return A confirmation message on success or error message on failure.
     OUTPUT:
     - 200 OK: "Journal entry deleted successfully"
     - 400 BAD REQUEST: If the entry is not found or access is denied.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJournalEntryOfPatientById(@PathVariable int id, Authentication authentication)
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

    /**
     ENDPOINT: PUT /journal/{id}/share
     FUNCTION: Sets the 'allowPsychologist' flag to true for a specific entry.
     @param id The ID of the journal entry to share.
     @param authentication The security context identifying the patient.
     INPUT: Path Variable: 'id' (Integer).
     @return A confirmation message on success or error message on failure.
     OUTPUT:
     * - 200 OK: "Journal entry shared with your psychologist."
     * - 400 BAD REQUEST: If the patient is not assigned to a psychologist.
     */
    @PutMapping("/{id}/share")
    public ResponseEntity<String> shareJournalEntryOfPatientWithPsychologistById(@PathVariable int id, Authentication authentication)
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