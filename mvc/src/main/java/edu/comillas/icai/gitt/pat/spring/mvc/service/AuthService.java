package edu.comillas.icai.gitt.pat.spring.mvc.service;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Token;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.ProfileResponse;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.RegisterRequest;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.Rol;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoToken;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoUsuarios;
import edu.comillas.icai.gitt.pat.spring.mvc.util.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    RepoUsuarios repoUsuario;
    @Autowired
    RepoToken repoToken;
    @Autowired
    Hashing hashing;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Transactional
    public ProfileResponse registrarUsuario(RegisterRequest register){
        if(repoUsuario.existsByEmail(register.email())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setEmail(register.email());
        usuario.setPassword(hashing.hash(register.password()));
        usuario.setNombre(register.nombre());
        usuario.setApellidos(register.apellidos());
        usuario.setTelefono(register.telefono());

        usuario.setFechaRegistro(LocalDateTime.now());

        Usuario usuarioGuardado = repoUsuario.save(usuario);
        return ProfileResponse.fromUsuario(usuarioGuardado);
    }

    public Token login(String email, String password){
        Usuario usuario = repoUsuario.findByEmail(email);
        if(usuario == null || !hashing.compare(password, usuario.getPassword())){
            throw  new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Contraseña no valida");
        }

        Token token = repoToken.findByUsuario(usuario);
        if(token == null) {
            token = new Token();
            token.usuario = usuario;
            token = repoToken.save(token);
        }
        return token;
    }

    public Usuario authentication(String tokenId){
        logger.info("AuthService: autentificando al usuario, cuyo token es {}", tokenId);
        Optional<Token> token = repoToken.findById(tokenId);
        if(token.isEmpty()){
            logger.warn("AuthService: no se encuentra ninguna sesión iniciada");
            throw  new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no encontrado");
        }
        return token.get().usuario;
    }

    public ProfileResponse perfil(String tokenId){
       Optional<Token> token = repoToken.findById(tokenId);

       if(token.isEmpty()){
           logger.warn("No se ha inciado sesión o la sesión ha expirado");
           throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesion invalida");
       }

       Usuario usuario = token.get().usuario;
       logger.info("Se ha obtenido correctamente el usuario: {}", usuario);
       return ProfileResponse.fromUsuario(usuario);
    }

    @Transactional
    public void logout(String tokenId){
        Optional<Token> token = repoToken.findById(tokenId);

        if(token.isEmpty()){
            logger.warn("No se ha inciado sesión o la sesión ha expirado");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesion invalida");
        }

        logger.info("Se ha cerrado correctamente la sesión");
        repoToken.deleteById(tokenId);
    }

    public void actualizarUsuario(Usuario usuario) {
        repoUsuario.save(usuario);
    }
}
