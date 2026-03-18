package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import edu.comillas.icai.gitt.pat.spring.mvc.service.PistaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.ReservaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/pistaPadel/users")
public class UsuarioController {

    @Autowired
    PistaService pistaService;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    ReservaService reservaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios(){
        //añadir logica
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable String userId) {
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Usuario> actualizarUsuario() {

    }
}