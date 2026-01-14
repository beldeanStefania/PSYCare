package com.goodfellas.backend;
import com.goodfellas.backend.dto.JournalEntryDTO;
import com.goodfellas.backend.model.JournalEntry;
import com.goodfellas.backend.model.Patient;
import com.goodfellas.backend.model.Psychologist;
import com.goodfellas.backend.repository.JournalEntryRepository;
import com.goodfellas.backend.repository.PatientRepository;
import com.goodfellas.backend.service.JournalEntryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JournalEntryServiceTest
{
    @Mock
    private JournalEntryRepository journalRepository;
    @Mock
    private PatientRepository patientRepository;
    @InjectMocks
    private JournalEntryService journalEntryService;

    private Patient testPatient;
    private JournalEntry testEntry;
    private JournalEntryDTO testDTO;
    private final String USERNAME = "testUser";
    private final int ENTRY_ID = 1;

    @BeforeEach
    void setUp()
    {
        testPatient = new Patient();
        testPatient.setId(100);
        testPatient.setUsername(USERNAME);

        testEntry = new JournalEntry();
        testEntry.setId(ENTRY_ID);
        testEntry.setTitle("My Day");
        testEntry.setText("Content");
        testEntry.setPatient(testPatient);

        testDTO = new JournalEntryDTO();
        testDTO.setTitle("Updated Title");
        testDTO.setText("Updated Content");
    }

    @Nested
    @DisplayName("Get Entries Tests")
    class GetEntriesTests
    {
        @Test
        void getMyEntries_ReturnsList()
        {
            when(journalRepository.findByPatient_Username(USERNAME)).thenReturn(List.of(testEntry));
            List<JournalEntryDTO> result = journalEntryService.getMyEntries(USERNAME);
            assertEquals(1, result.size());
            assertEquals("My Day", result.get(0).getTitle());
        }

        @Test
        void getEntry_Success_WhenOwner()
        {
            when(journalRepository.findById(ENTRY_ID)).thenReturn(Optional.of(testEntry));
            JournalEntryDTO result = journalEntryService.getEntry(USERNAME, ENTRY_ID);
            assertNotNull(result);
            assertEquals(ENTRY_ID, result.getId());
        }

        @Test
        void getEntry_ThrowsException_WhenNotOwner()
        {
            testPatient.setUsername("otherUser");
            when(journalRepository.findById(ENTRY_ID)).thenReturn(Optional.of(testEntry));
            assertThrows(AccessDeniedException.class, () -> journalEntryService.getEntry(USERNAME, ENTRY_ID));
        }
    }

    @Nested
    @DisplayName("Create Entry Tests")
    class CreateEntryTests
    {
        @Test
        void createEntry_Success()
        {
            when(patientRepository.findByUsername(USERNAME)).thenReturn(Optional.of(testPatient));
            journalEntryService.createEntry(USERNAME, testDTO);
            verify(journalRepository, times(1)).save(any(JournalEntry.class));
        }

        @Test
        void createEntry_PatientNotFound_ThrowsException()
        {
            when(patientRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
            RuntimeException ex = assertThrows(RuntimeException.class, () -> journalEntryService.createEntry(USERNAME, testDTO));
            assertEquals("Patient not found", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Update & Delete Tests")
    class UpdateDeleteTests
    {
        @Test
        void updateEntry_Success_WhenOwner()
        {
            when(journalRepository.findById(ENTRY_ID)).thenReturn(Optional.of(testEntry));
            journalEntryService.updateEntry(USERNAME, ENTRY_ID, testDTO);
            verify(journalRepository).save(testEntry);
            assertEquals("Updated Title", testEntry.getTitle());
        }

        @Test
        void deleteEntry_Success_WhenOwner()
        {
            when(journalRepository.findById(ENTRY_ID)).thenReturn(Optional.of(testEntry));
            journalEntryService.deleteEntry(USERNAME, ENTRY_ID);
            verify(journalRepository).delete(testEntry);
        }
    }

    @Nested
    @DisplayName("Sharing Tests")
    class SharingTests
    {
        @Test
        void shareWithPsychologist_Success() {
            testPatient.setPsychologist(new Psychologist());
            when(journalRepository.findById(ENTRY_ID)).thenReturn(Optional.of(testEntry));
            journalEntryService.shareWithPsychologist(USERNAME, ENTRY_ID);
            assertTrue(testEntry.isAllowPsychologist());
            verify(journalRepository).save(testEntry);
        }

        @Test
        void shareWithPsychologist_NoPsychologistAssigned_ThrowsException()
        {
            testPatient.setPsychologist(null);
            when(journalRepository.findById(ENTRY_ID)).thenReturn(Optional.of(testEntry));
            RuntimeException ex = assertThrows(RuntimeException.class, () -> journalEntryService.shareWithPsychologist(USERNAME, ENTRY_ID));
            assertTrue(ex.getMessage().contains("not assigned to a psychologist"));
        }
    }
}