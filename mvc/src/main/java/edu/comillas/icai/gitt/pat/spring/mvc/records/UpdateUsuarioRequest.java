package edu.comillas.icai.gitt.pat.spring.mvc.records;

public record UpdateUsuarioRequest(
        String nombre,
        String apellidos,
        String email,
        String telefono
) {}
