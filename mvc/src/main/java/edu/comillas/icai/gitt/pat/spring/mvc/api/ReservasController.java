package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Reserva;
import edu.comillas.icai.gitt.pat.spring.mvc.service.PistaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.ReservaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/reservations") // Simplificado para que coincida con tus métodos
public class ReservasController {
    @Autowired
    PistaService pistaService;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    ReservaService reservaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // Usamos los mapas de AlmacenDatos para que sea persistente entre controladores

    // 0. CREAR RESERVA
    @PostMapping
    public ResponseEntity<Reserva> crearReserva(
            @Valid @RequestBody Reserva nuevaReserva,
            Authentication authentication
    ) {
    }

    // 1. LISTAR RESERVAS
    @GetMapping
    public ResponseEntity<List<Reserva>> listarMisReservas(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            Authentication authentication
    ) {
    }

    // 2. OBTENER UNA RESERVA
    @GetMapping("/{reservationId}")
    public ResponseEntity<Reserva> obtenerReserva(@PathVariable String reservationId, Authentication authentication) {
    }

    // 3. CANCELAR RESERVA
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancelarReserva(@PathVariable String reservationId, Authentication authentication) {
    }

    // 4. MODIFICAR RESERVA
    @PatchMapping("/{reservationId}")
    public ResponseEntity<Reserva> modificarReserva(
            @PathVariable String reservationId,
            @RequestBody Reserva datosActualizar,
            Authentication authentication
    ) {
    }
}
