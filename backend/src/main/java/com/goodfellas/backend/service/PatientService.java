package com.goodfellas.backend.service;

import com.goodfellas.backend.dto.PatientViewDTO;
import com.goodfellas.backend.model.Patient;
import com.goodfellas.backend.model.Psychologist;
import com.goodfellas.backend.repository.PatientRepository;
import com.goodfellas.backend.repository.PsychologistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final PsychologistRepository psychologistRepository;

    @Autowired
    public PatientService(PatientRepository patientRepository, PsychologistRepository psychologistRepository) {
        this.patientRepository = patientRepository;
        this.psychologistRepository = psychologistRepository;
    }

    public List<Patient> getUnassignedPatients() {
        return patientRepository.findByPsychologistIsNull();
    }

    public List<PatientViewDTO> getAssignedPatients(String username) {
        var psychologist = psychologistRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Psychologist not found"));

        return patientRepository.findByPsychologistId(psychologist.getId()).stream()
                .map(p -> new PatientViewDTO(
                        p.getId(),
                        p.getFirstName(),
                        p.getLastName(),
                        p.getAge(),
                        p.getUsername()
                ))
                .toList();

    }

    public void assignPatientToPsychologist(String psychologistUsername, int patientId) {
        Psychologist psychologist = psychologistRepository.findByUsername(psychologistUsername)
                .orElseThrow(() -> new RuntimeException("Psychologist not found"));

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        if (patient.getPsychologist() != null && patient.getPsychologist().getId() != psychologist.getId()) {
            throw new RuntimeException("Patient is already assigned to another psychologist");
        }

        patient.setPsychologist(psychologist);
        patientRepository.save(patient);
    }

}
