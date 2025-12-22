package com.goodfellas.backend.dto;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String accessToken;
    private String role;
    private Object userData;

    public AuthResponseDTO(String accessToken, String role, Object userData)
    {
        this.accessToken = "Bearer " + accessToken;
        this.role = role;
        this.userData = userData;
    }
}