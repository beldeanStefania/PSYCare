package com.goodfellas.backend.controller;

import com.goodfellas.backend.dto.PatientViewDTO;
import com.goodfellas.backend.model.Patient;
import com.goodfellas.backend.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<Patient>> getUnassignedPatients() {
        return ResponseEntity.ok(patientService.getUnassignedPatients());
    }

    @GetMapping("/assigned")
    public ResponseEntity<List<PatientViewDTO>> getAssignedPatients(Authentication authentication) {
        return ResponseEntity.ok(patientService.getAssignedPatients(authentication.getName()));
    }

    @PostMapping("/{patientId}/assign")
    public ResponseEntity<String> assignPatient(@PathVariable int patientId,
                                                Authentication authentication) {
        patientService.assignPatientToPsychologist(authentication.getName(), patientId);
        return new ResponseEntity<>("Patient assigned successfully", HttpStatus.OK);
    }
}
