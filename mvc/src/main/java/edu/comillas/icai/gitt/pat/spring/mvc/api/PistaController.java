package edu.comillas.icai.gitt.pat.spring.mvc.api;

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
import edu.comillas.icai.gitt.pat.spring.mvc.data.AlmacenDatos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/pistaPadel/courts")
public class PistaController {
    @Autowired
    PistaService pistaService;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    ReservaService reservaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // 1. LISTAR PISTAS (GET /pistaPadel/courts)
    @GetMapping("")
    public ResponseEntity<List<Pista>> listarPistas(@RequestParam(required = false) Boolean active) {
    }

    // 2. DETALLE DE UNA PISTA (GET /pistaPadel/courts/{courtId})
    @GetMapping("/{courtId}")
    public ResponseEntity<Pista> obtenerDetalle(@PathVariable String courtId) {
        }

    // 3. CREAR PISTA (POST /pistaPadel/courts)
    @PostMapping
    public ResponseEntity<Pista> crearPista(@Valid @RequestBody Pista nuevaPista,
                                            BindingResult result){
    }

    // 4. MODIFICAR PISTA (PATCH /pistaPadel/courts/{courtId})
    @PatchMapping("/{courtId}")
    public ResponseEntity<Pista> modificarPista(
    }

    // 5. ELIMINAR PISTA (DELETE /pistaPadel/courts/{courtId})
    @DeleteMapping("/{courtId}")
    public ResponseEntity<Void> eliminarPista(@PathVariable String courtId) {
        logger.trace("se ha eliminado una pista");

    }
}
