package com.goodfellas.backend.dto;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class LoginDTO
{
    private String username;
    private String password;
}