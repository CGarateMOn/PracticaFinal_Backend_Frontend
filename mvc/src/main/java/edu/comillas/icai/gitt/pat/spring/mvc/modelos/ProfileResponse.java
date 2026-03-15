package edu.comillas.icai.gitt.pat.spring.mvc.modelos;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;

public record ProfileResponse(
        String nombre,
        String email,
        Rol rol
) {
    public static ProfileResponse fromUsuario(Usuario usuario){
        return new ProfileResponse(usuario.getNombre(), usuario.getEmail(), usuario.getRol());
    }
}
