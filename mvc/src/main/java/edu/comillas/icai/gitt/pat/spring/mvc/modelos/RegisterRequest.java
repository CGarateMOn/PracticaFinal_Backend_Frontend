package edu.comillas.icai.gitt.pat.spring.mvc.modelos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;



public record RegisterRequest(
        @NotBlank String nombre,
        @NotBlank String apellidos,
        @NotBlank String email,
        @NotBlank String password,
        @NotBlank String telefono
){}
