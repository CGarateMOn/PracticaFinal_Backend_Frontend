package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import edu.comillas.icai.gitt.pat.spring.mvc.records.UpdateUsuarioRequest;
import edu.comillas.icai.gitt.pat.spring.mvc.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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