package com.goodfellas.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PatientViewDTO {
    private int id;
    private String firstName;
    private String lastName;
    private int age;
    private String username;
}
