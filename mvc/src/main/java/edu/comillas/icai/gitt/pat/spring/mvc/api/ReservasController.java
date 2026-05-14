package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Reserva;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import edu.comillas.icai.gitt.pat.spring.mvc.service.PistaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.ReservaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.UsuarioService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;


@RestController
@RequestMapping("/pistaPadel/reservations")
@CrossOrigin(origins = "http://localhost:8080/reservas.html", allowCredentials = "true")
public class ReservasController {
    @Autowired
    PistaService pistaService;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    ReservaService reservaService;
    @Autowired
    AuthService authService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // Crear reserva para el usuario que tiene la sesion iniciada
    @PostMapping
    public ResponseEntity<Reserva> crearReserva(
            @Valid @RequestBody Reserva nuevaReserva,
            @CookieValue(value = "session", required = true) String session
    ) {
        Usuario usuario = obtenerUsuarioDesdeSesion(session);
        Reserva reservaCreada = reservaService.crearReserva(usuario, nuevaReserva);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaCreada);
    }

    // Lista las reservas del usuario autenticado
    // Se filtra entre dos fechas: from y to
    @GetMapping
    public ResponseEntity<List<Reserva>> listarMisReservas(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @CookieValue(value = "session", required = true) String session
    ) {
        Usuario usuario = obtenerUsuarioDesdeSesion(session);

        LocalDate fromDate = parseFecha(from, "from");
        LocalDate toDate = parseFecha(to, "to");

        //Si mandan solo una de las dos fechas, error
        if((fromDate == null) != (toDate == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes enviar ambas fechas");
        }

        // from no puede ser posterior a to
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fechas incoherentes");
        }

        return ResponseEntity.ok(reservaService.listarMisReservas(usuario, fromDate, toDate));
    }

    // Devuelve una reserva concreta si el usuario puede verla
    @GetMapping("/{reservationId}")
    public ResponseEntity<Reserva> obtenerReserva(
            @PathVariable Long reservationId,
            @CookieValue(value = "session", required = true) String session)
    {
        Usuario usuario = obtenerUsuarioDesdeSesion(session);
        return  ResponseEntity.ok(reservaService.obtenerReserva(reservationId, usuario));
    }

    // Cancelar una reserva
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancelarReserva(
            @PathVariable Long reservationId,
            @CookieValue(value="session", required = true) String session)
    {
        Usuario usuario = obtenerUsuarioDesdeSesion(session);
        reservaService.cancelarReserva(reservationId, usuario);
        return ResponseEntity.noContent().build();
    }

    // Modifica parcialmente una reserva ya creada
    @PatchMapping("/{reservationId}")
    public ResponseEntity<Reserva> modificarReserva(
            @PathVariable Long reservationId,
            @RequestBody Reserva datosActualizar,
            @CookieValue(value = "session", required = true) String session)
    {
        Usuario usuario = obtenerUsuarioDesdeSesion(session);
        Reserva reservaActualizada = reservaService.modificarReserva(reservationId, datosActualizar, usuario);
        return ResponseEntity.ok(reservaActualizada);
    }

    //A partir de la cookie de sesion recuperamos el usuario autenticado
    private Usuario obtenerUsuarioDesdeSesion(String session) {
        Usuario usuario = authService.authentication(session);
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesion no valida");
        }
        return usuario;
    }

    //Convierte el texto recibido en una fecha LocalDate
    //Si el formato esta mal devolvemos error 404
    private LocalDate parseFecha(String valor, String nombreParametro) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(valor);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Formato inválido para " + nombreParametro + ". Usa YYYY-MM-DD"
            );
        }
    }

}
