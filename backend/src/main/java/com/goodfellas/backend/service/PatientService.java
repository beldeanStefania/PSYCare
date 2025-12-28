package com.goodfellas.backend.service;

import com.goodfellas.backend.dto.PatientViewDTO;
import com.goodfellas.backend.model.Psychologist;
import com.goodfellas.backend.repository.PatientRepository;
import com.goodfellas.backend.repository.PsychologistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Optional.ofNullable;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final PsychologistRepository psychologistRepository;

    @Autowired
    public PatientService(PatientRepository patientRepository, PsychologistRepository psychologistRepository) {
        this.patientRepository = patientRepository;
        this.psychologistRepository = psychologistRepository;
    }

    public List<PatientViewDTO> getUnassignedPatients() {

        return patientRepository.findByPsychologistIsNull().stream()
                .map(p -> new PatientViewDTO(
                        p.getId(),
                        p.getFirstName(),
                        p.getLastName(),
                        p.getAge(),
                        p.getUsername()
                ))
                .toList();
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
        var psychologist = psychologistRepository.findByUsername(psychologistUsername)
                .orElseThrow(() -> new RuntimeException("Psychologist not found"));

        var patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        ofNullable(patient.getPsychologist())
                .map(Psychologist::getId)
                .filter(id -> !id.equals(psychologist.getId()))
                .ifPresent(e -> {
                    throw new RuntimeException("Patient is already assigned to another psychologist");
                });

        patient.setPsychologist(psychologist);
        patientRepository.save(patient);
    }

}
