package com.goodfellas.backend.controller;

import com.goodfellas.backend.dto.AppointmentResponseDTO;
import com.goodfellas.backend.dto.AppointmentRequestDTO;
import com.goodfellas.backend.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/psychologists/me/appointments")
public class PsychologistAppointmentsController {
    private final AppointmentService appointmentService;

    @Autowired
    public PsychologistAppointmentsController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /**
     *
     * @param authentication the current authenticated user (psychologist)
     * @return all booked appointments for the authenticated psychologist
     */
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getBookedSessions(Authentication authentication) {
        return ok(appointmentService.getAppointmentsForPsychologist(authentication.getName()));
    }

    /**
     * The psychologist can create an appointment for a given patient
     */
    @PostMapping
    public ResponseEntity<String> bookSessionForPatient(@RequestBody AppointmentRequestDTO appointmentRequestDTO, Authentication authentication) {
        try {
            appointmentService.bookAppointment(authentication.getName(), appointmentRequestDTO);
            return ResponseEntity.status(CREATED).body("Session successfully booked");
        } catch (RuntimeException e) {
            return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
}
