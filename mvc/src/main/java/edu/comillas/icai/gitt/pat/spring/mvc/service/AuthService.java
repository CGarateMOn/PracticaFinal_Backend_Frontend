package edu.comillas.icai.gitt.pat.spring.mvc.service;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Token;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.ProfileResponse;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.RegisterRequest;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.Rol;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoToken;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    RepoUsuarios repoUsuario;
    @Autowired
    RepoToken repoToken;

    public ProfileResponse registrarUsuario(RegisterRequest register){
        Usuario usuario = new Usuario();
        usuario.setEmail(register.email());
        usuario.setPassword(register.password());
        usuario.setNombre(register.nombre());
        usuario.setApellidos(register.apellidos());
        usuario.setTelefono(register.telefono());

        Usuario usuarioGuardado = repoUsuario.save(usuario);
        return ProfileResponse.fromUsuario(usuarioGuardado);
    }

    public Token login(String email, String password){
        Usuario usuario = repoUsuario.findByEmail(email);
        if(usuario == null) return null;

        Token token = repoToken.findByUsuario(usuario);
        if(token != null) return token;

        token = new Token();
        token.usuario= usuario;
        return repoToken.save(token);
    }
}
