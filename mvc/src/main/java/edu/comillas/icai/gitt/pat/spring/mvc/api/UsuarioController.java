package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.Rol;
import edu.comillas.icai.gitt.pat.spring.mvc.records.UpdateUsuarioRequest;
import edu.comillas.icai.gitt.pat.spring.mvc.service.AuthService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.PistaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.ReservaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static edu.comillas.icai.gitt.pat.spring.mvc.data.AlmacenDatos.usuarios;

@RestController
@RequestMapping("/pistaPadel/users")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios(
            @CookieValue(value = "session", required = true) String session) {
        logger.info("Petición de listado de usuarios");
        return ResponseEntity.ok(usuarioService.listarTodos(session));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Usuario> obtenerUsuario(
            @PathVariable Long userId,
            @CookieValue(value = "session", required = true) String session) {
        logger.info("Petición de consulta de usuario {}", userId);
        return ResponseEntity.ok(usuarioService.buscarPorId(userId, session));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Usuario> actualizarUsuario(
            @PathVariable Long userId,
            @RequestBody UpdateUsuarioRequest request,
            @CookieValue(value = "session", required = true) String session) {
        logger.info("Petición de actualización de usuario {}", userId);
        return ResponseEntity.ok(usuarioService.actualizar(userId, request, session));
    }
}