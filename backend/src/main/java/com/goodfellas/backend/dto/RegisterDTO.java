package com.goodfellas.backend.dto;

import lombok.Data;

@Data
public class RegisterDTO
{
    private String username;
    private String password;
    private String firstName;
    private String lastName;
}