package com.goodfellas.backend;
import com.goodfellas.backend.dto.MoodDTO;
import com.goodfellas.backend.model.Mood;
import com.goodfellas.backend.model.Patient;
import com.goodfellas.backend.repository.MoodRepository;
import com.goodfellas.backend.repository.PatientRepository;
import com.goodfellas.backend.service.MoodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoodServiceTest
{
    @Mock
    private MoodRepository moodRepository;
    @Mock
    private PatientRepository patientRepository;
    @InjectMocks
    private MoodService moodService;

    private final String USERNAME = "testPatient";
    private Patient testPatient;

    @BeforeEach
    void setUp()
    {
        testPatient = new Patient();
        testPatient.setId(1);
        testPatient.setUsername(USERNAME);
    }

    @Nested
    @DisplayName("submitMood Tests")
    class SubmitMoodTests
    {
        @Test
        @DisplayName("Success: Submit valid mood value")
        void submitMood_Success()
        {
            int moodValue = 7;
            LocalDate today = LocalDate.now();
            when(moodRepository.findByPatient_UsernameAndDate(USERNAME, today)).thenReturn(Optional.empty());
            when(patientRepository.findByUsername(USERNAME)).thenReturn(Optional.of(testPatient));
            moodService.submitMood(USERNAME, moodValue);
            verify(moodRepository, times(1)).save(any(Mood.class));
        }

        @Test
        @DisplayName("Failure: Value below range (0)")
        void submitMood_ValueTooLow()
        {
            RuntimeException ex = assertThrows(RuntimeException.class, () -> moodService.submitMood(USERNAME, 0));
            assertTrue(ex.getMessage().contains("Mood value must be between 1 and 10"));
            verify(moodRepository, never()).save(any());
        }

        @Test
        @DisplayName("Failure: Value above range (11)")
        void submitMood_ValueTooHigh()
        {
            RuntimeException ex = assertThrows(RuntimeException.class, () -> moodService.submitMood(USERNAME, 11));
            assertTrue(ex.getMessage().contains("Mood value must be between 1 and 10"));
        }

        @Test
        @DisplayName("Failure: Already submitted today")
        void submitMood_AlreadySubmittedToday()
        {
            LocalDate today = LocalDate.now();
            when(moodRepository.findByPatient_UsernameAndDate(USERNAME, today)).thenReturn(Optional.of(new Mood()));
            RuntimeException ex = assertThrows(RuntimeException.class, () -> moodService.submitMood(USERNAME, 5));
            assertTrue(ex.getMessage().contains("already submitted your mood for today"));
        }

        @Test
        @DisplayName("Failure: Patient not found")
        void submitMood_PatientNotFound()
        {
            when(moodRepository.findByPatient_UsernameAndDate(USERNAME, LocalDate.now())).thenReturn(Optional.empty());
            when(patientRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
            RuntimeException ex = assertThrows(RuntimeException.class, () -> moodService.submitMood(USERNAME, 5));
            assertEquals("Patient not found", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("getMyMoodHistory Tests")
    class GetMoodHistoryTests
    {
        @Test
        @DisplayName("Success: Retrieve list of moods")
        void getMyMoodHistory_Success()
        {
            Mood mood1 = new Mood();
            mood1.setId(1);
            mood1.setValue(8);
            mood1.setDate(LocalDate.now().minusDays(1));
            Mood mood2 = new Mood();
            mood2.setId(2);
            mood2.setValue(4);
            mood2.setDate(LocalDate.now());
            when(moodRepository.findByPatient_Username(USERNAME)).thenReturn(List.of(mood1, mood2));
            List<MoodDTO> history = moodService.getMyMoodHistory(USERNAME);
            assertEquals(2, history.size());
            assertEquals(8, history.get(0).getValue());
            assertEquals(4, history.get(1).getValue());
            verify(moodRepository, times(1)).findByPatient_Username(USERNAME);
        }

        @Test
        @DisplayName("Success: Return empty list if no history")
        void getMyMoodHistory_Empty()
        {
            when(moodRepository.findByPatient_Username(USERNAME)).thenReturn(List.of());
            List<MoodDTO> history = moodService.getMyMoodHistory(USERNAME);
            assertTrue(history.isEmpty());
        }
    }
}
