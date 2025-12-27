package com.goodfellas.backend.controller;

import com.goodfellas.backend.dto.AppointmentResponseDTO;
import com.goodfellas.backend.dto.AppointmentRequestDTO;
import com.goodfellas.backend.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getBookedSessions(Authentication authentication) {
        return ResponseEntity.ok(appointmentService.getAllBookedAppointments(authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<String> bookSessionForPatient(@RequestBody AppointmentRequestDTO appointmentRequestDTO, Authentication authentication) {
        appointmentService.bookAppointment(authentication.getName(), appointmentRequestDTO);
        return new ResponseEntity<>("Session successfully booked ", HttpStatus.CREATED);
    }
}
