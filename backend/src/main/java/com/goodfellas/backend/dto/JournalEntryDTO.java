package com.goodfellas.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class JournalEntryDTO
{
    private int id;
    private String title;
    private String text;
    private String tags;
    private LocalDateTime date;
    private boolean allowPsychologist;
}