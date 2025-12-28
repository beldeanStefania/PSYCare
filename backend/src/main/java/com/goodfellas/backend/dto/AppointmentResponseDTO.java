package com.goodfellas.backend.dto;

import com.goodfellas.backend.model.AppointmentStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AppointmentResponseDTO extends AppointmentDTO{
    private int patientId;
    private AppointmentStatus status;
    private String patientFirstName;
    private String patientLastName;
}
