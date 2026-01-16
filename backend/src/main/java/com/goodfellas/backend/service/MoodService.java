package com.goodfellas.backend.service;

import com.goodfellas.backend.dto.MoodDTO;
import com.goodfellas.backend.model.Mood;
import com.goodfellas.backend.model.Patient;
import com.goodfellas.backend.repository.MoodRepository;
import com.goodfellas.backend.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MoodService
{
    private final MoodRepository moodRepository;
    private final PatientRepository patientRepository;

    @Autowired
    public MoodService(MoodRepository moodRepository, PatientRepository patientRepository)
    {
        this.moodRepository = moodRepository;
        this.patientRepository = patientRepository;
    }

    public void submitMood(String username, int value)
    {
        if (value < 1 || value > 10)
        {
            throw new RuntimeException("Error: Mood value must be between 1 and 10.");
        }

        LocalDate today = LocalDate.now();
        if (moodRepository.findByPatient_UsernameAndDate(username, today).isPresent())
        {
            throw new RuntimeException("Error: You have already submitted your mood for today (" + today + ").");
        }

        Patient patient = patientRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Patient not found"));
        Mood mood = new Mood();
        mood.setValue(value);
        mood.setDate(today);
        mood.setPatient(patient);

        moodRepository.save(mood);
    }

    public List<MoodDTO> getMyMoodHistory(String username)
    {
        return moodRepository.findByPatient_Username(username).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private MoodDTO mapToDTO(Mood mood)
    {
        MoodDTO dto = new MoodDTO();
        dto.setId(mood.getId());
        dto.setValue(mood.getValue());
        dto.setDate(mood.getDate());
        return dto;
    }

    public Optional<MoodDTO> getTodayMood(String username)
    {
        return moodRepository
                .findByPatient_UsernameAndDate(username, LocalDate.now())
                .map(this::mapToDTO);
    }

}