package com.goodfellas.backend.controller;

import com.goodfellas.backend.dto.AppointmentResponseDTO;
import com.goodfellas.backend.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/patients/me/appointments")
public class PatientAppointmentsController {
    private final AppointmentService appointmentService;

    @Autowired
    public PatientAppointmentsController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /**
     *
     * @param authentication the current authenticated user (patient)
     * @return list of appointments belonging to the patient
     */
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getBookedSessions(Authentication authentication) {
        return ok(appointmentService.getAppointmentsForPatient(authentication.getName()));
    }
}
