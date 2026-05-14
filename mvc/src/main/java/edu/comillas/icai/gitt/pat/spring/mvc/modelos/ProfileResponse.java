package edu.comillas.icai.gitt.pat.spring.mvc.modelos;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;

public record ProfileResponse(
        String nombre,
        String apellidos,
        String email,
        Rol rol,
        String telefono
) {
    public static ProfileResponse fromUsuario(Usuario usuario){
        return new ProfileResponse(usuario.getNombre(), usuario.getApellidos(), usuario.getEmail(), usuario.getRol(), usuario.getTelefono());
    }
}
