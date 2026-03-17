package edu.comillas.icai.gitt.pat.spring.mvc.modelos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;



public record RegisterRequest(
        @NotBlank String nombre,
        @NotBlank String apellidos,
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String telefono
){}
