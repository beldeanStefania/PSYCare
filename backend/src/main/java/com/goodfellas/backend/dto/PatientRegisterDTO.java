package com.goodfellas.backend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatientRegisterDTO extends RegisterDTO
{
    private int age;
}