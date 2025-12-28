package com.goodfellas.backend.controller;

import com.goodfellas.backend.dto.PatientViewDTO;
import com.goodfellas.backend.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/psychologists/me/patients")
public class PsychologistPatientController {

    private final PatientService patientService;

    @Autowired
    public PsychologistPatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     *
     * @return unassigned patients
     */
    @GetMapping("/unassigned")
    public ResponseEntity<List<PatientViewDTO>> getUnassignedPatients() {
        return ok(patientService.getUnassignedPatients());
    }

    /**
     *
     * @param authentication
     * @return already assigned patients
     */
    @GetMapping("/assigned")
    public ResponseEntity<List<PatientViewDTO>> getAssignedPatients(Authentication authentication) {
        return ok(patientService.getAssignedPatients(authentication.getName()));
    }

    /**
     * Assigns an unassigned patient to the authenticated psychologist.
     */
    @PostMapping("/{patientId}")
    public ResponseEntity<String> assignPatient(@PathVariable int patientId, Authentication authentication) {
        try {
            patientService.assignPatientToPsychologist(authentication.getName(), patientId);
            return new ResponseEntity<>("Patient assigned successfully", OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
        }
    }
}
