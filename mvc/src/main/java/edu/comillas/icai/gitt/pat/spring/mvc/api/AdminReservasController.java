package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Reserva;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.Rol;
import edu.comillas.icai.gitt.pat.spring.mvc.service.AuthService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.ReservaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/pistaPadel/admin")
public class AdminReservasController {

    @Autowired
    AuthService authService;

    @Autowired
    ReservaService reservaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/reservations")
    public ResponseEntity<List<Reserva>> listarReservasAdmin(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String courtId,
            @RequestParam(required = false) String userId,
            @CookieValue(value = "session", required = true) String session
    ) {
        logger.info("GET /pistaPadel/admin/reservations - date={} courtId={} userId={}", date, courtId, userId);

        Usuario admin = authService.authentication(session);
        if (admin == null) {
            logger.error("Sesión no válida en consulta admin de reservas");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        if (admin.getRol() != Rol.ADMIN) {
            logger.error("Usuario {} sin permisos de admin intenta consultar reservas", admin.getIdUsuario());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado");
        }

        try {
            LocalDate fecha = (date != null && !date.isBlank()) ? LocalDate.parse(date) : null;
            Long pistaId = (courtId != null && !courtId.isBlank()) ? Long.parseLong(courtId) : null;
            Long usuarioId = (userId != null && !userId.isBlank()) ? Long.parseLong(userId) : null;

            List<Reserva> reservas = reservaService.listarReservasAdmin(fecha, pistaId, usuarioId);

            logger.info("Admin {} ha consultado {} reservas", admin.getIdUsuario(), reservas.size());
            return ResponseEntity.ok(reservas);

        } catch (DateTimeParseException e) {
            logger.error("Formato de fecha inválido: {}", date);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de fecha inválido. Usa YYYY-MM-DD");
        } catch (NumberFormatException e) {
            logger.error("courtId o userId no numérico. courtId={} userId={}", courtId, userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "courtId y userId deben ser numéricos");
        }
    }
}