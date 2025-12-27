package com.goodfellas.backend.service;

import com.goodfellas.backend.dto.AppointmentResponseDTO;
import com.goodfellas.backend.dto.AppointmentRequestDTO;
import com.goodfellas.backend.model.Appointment;
import com.goodfellas.backend.model.Patient;
import com.goodfellas.backend.model.Psychologist;
import com.goodfellas.backend.repository.AppointmentRepository;
import com.goodfellas.backend.repository.PatientRepository;
import com.goodfellas.backend.repository.PsychologistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static com.goodfellas.backend.model.AppointmentStatus.BOOKED;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PsychologistRepository psychologistRepository;

    private final PatientRepository patientRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository, PsychologistRepository psychologistRepository, PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.psychologistRepository = psychologistRepository;
        this.patientRepository = patientRepository;
    }

    public List<AppointmentResponseDTO> getAllBookedAppointments(final String username) {
        var psychologist = psychologistRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Psychologist not found for username: " + username));

        return appointmentRepository.findByPsychologistIdAndStatusOrderByStartTimeAsc(psychologist.getId(), BOOKED).stream()
                .map(a -> {
                    AppointmentResponseDTO dto = new AppointmentResponseDTO();
                    dto.setId(a.getId());
                    dto.setStartTime(a.getStartTime());
                    dto.setEndTime(a.getEndTime());
                    dto.setStatus(a.getStatus());
                    dto.setPatientId(a.getPatient().getId());
                    dto.setPatientFirstName(a.getPatient().getFirstName());
                    dto.setPatientLastName(a.getPatient().getLastName());
                    return dto;
                })
                .toList();
    }

    public void bookAppointment(String psychologistUsername, AppointmentRequestDTO req) {
        validateRequest(req);

        Psychologist psychologist = psychologistRepository.findByUsername(psychologistUsername)
                .orElseThrow(() -> new RuntimeException("Psychologist not found"));

        Patient patient = patientRepository.findById(req.getId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        if (patient.getPsychologist() == null || patient.getPsychologist().getId() != psychologist.getId()) {
            throw new RuntimeException("Cannot make an appointment for user with id: " + patient.getId());
        }

        if (appointmentRepository.existsOverlapForPsychologist(psychologist.getId(), req.getStartTime(), req.getEndTime())) {
            throw new RuntimeException("aici ii pb");
        }

        Appointment appointment = new Appointment();
        appointment.setPsychologist(psychologist);
        appointment.setPatient(patient);
        appointment.setStartTime(req.getStartTime());
        appointment.setEndTime(req.getEndTime());
        appointment.setStatus(BOOKED);

        appointmentRepository.save(appointment);
    }

    private void validateRequest(AppointmentRequestDTO req) {
        if (req == null) {
            throw new RuntimeException("Request body is required");
        }
        if (req.getId() <= 0) {
            throw new RuntimeException("patientId must be > 0");
        }
        if (req.getStartTime() == null || req.getEndTime() == null) {
            throw new RuntimeException("startTime and endTime are required");
        }
        if (!req.getStartTime().isBefore(req.getEndTime())) {
            throw new RuntimeException("startTime must be before endTime");
        }

        if (req.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("startTime must be in the future");
        }
    }
}
