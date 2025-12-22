package com.goodfellas.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MoodDTO
{
    private int id;
    private int value;
    private LocalDate date;
}