package edu.comillas.icai.gitt.pat.spring.mvc.modelos;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest (
    @NotBlank String email,
    @NotBlank String password
)
{}
