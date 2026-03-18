package edu.comillas.icai.gitt.pat.spring.mvc.service;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Token;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.Rol;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import edu.comillas.icai.gitt.pat.spring.mvc.records.UpdateUsuarioRequest;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoPistas;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoReserva;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoToken;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    private RepoPistas pistaRepo;
    @Autowired
    private RepoReserva reservaRepo;
    @Autowired
    private RepoUsuarios usuarioRepo;
    @Autowired
    private RepoToken tokenRepo;
    @Autowired
    private AuthService authService;

    public List<Usuario> listarTodos(String session) {
        Usuario admin = autenticarAdmin(session);
        return usuarioRepo.findAll();
    }

    public Usuario buscarPorId(Long id, String session) {
        Usuario autenticado = autenticarConAcceso(session, id);
        return usuarioRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    public Usuario actualizar(Long id, UpdateUsuarioRequest request, String session) {
        Usuario autenticado = autenticarConAcceso(session, id);
        Usuario usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (request.nombre() != null) usuario.setNombre(request.nombre());
        if (request.apellidos() != null) usuario.setApellidos(request.apellidos());
        if (request.telefono() != null) usuario.setTelefono(request.telefono());
        if (request.email() != null && !request.email().equals(usuario.getEmail())) {
            if (usuarioRepo.existsByEmail(request.email())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ya existe");
            }
            usuario.setEmail(request.email());
        }

        return usuarioRepo.save(usuario);
    }

    private Usuario autenticar(String session) {
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        Usuario usuario = authService.authentication(session);
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no válido");
        }
        return usuario;
    }

    private Usuario autenticarAdmin(String session) {
        Usuario usuario = autenticar(session);
        if (!usuario.getRol().equals(Rol.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo administradores");
        }
        return usuario;
    }

    private Usuario autenticarConAcceso(String session, Long userId) {
        Usuario usuario = autenticar(session);
        if (!usuario.getRol().equals(Rol.ADMIN) && !usuario.getIdUsuario().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sin permisos");
        }
        return usuario;
    }
}
