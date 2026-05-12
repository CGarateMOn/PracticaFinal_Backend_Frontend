package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.records.Disponibilidad;
import edu.comillas.icai.gitt.pat.spring.mvc.service.DisponibilidadService;
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
@CrossOrigin
@RequestMapping("/pistaPadel")
public class DisponibilidadController {

    @Autowired
    DisponibilidadService disponibilidadService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/availability")
    public ResponseEntity<?> getAvailabilityByDate(
            @RequestParam(name = "date", required = false) String date,
            @RequestParam(name = "courtId", required = false) String courtId
    ) {
        logger.info("GET /availability - date={} courtId={}", date, courtId);
        if (date == null || date.isBlank()) {
            logger.error("Falta parámetro obligatorio: date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El parámetro date es obligatorio");
        }

        try {
            LocalDate fecha = LocalDate.parse(date);

            if (courtId != null && !courtId.isBlank()) {
                Long idPista = Long.parseLong(courtId);
                logger.debug("Consultando disponibilidad de pista concreta. idPista={} fecha={}", idPista, fecha);

                Disponibilidad disponibilidad = disponibilidadService.obtenerDisponibilidadPista(idPista, fecha);
                logger.info("Disponibilidad de pista {} calculada correctamente para fecha {}", idPista, fecha);
                return ResponseEntity.ok(disponibilidad);
            }
            logger.debug("Consultando disponibilidad de todas las pistas para fecha={}", fecha);
            List<Disponibilidad> disponibilidades = disponibilidadService.obtenerDisponibilidadPorFecha(fecha);
            logger.info("Disponibilidad general calculada correctamente para fecha {}", fecha);
            return ResponseEntity.ok(disponibilidades);

        } catch (DateTimeParseException e) {
            logger.error("Formato de fecha inválido: {}", date);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de fecha inválido. Usa YYYY-MM-DD");
        } catch (NumberFormatException e) {
            logger.error("courtId inválido: {}", courtId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "courtId debe ser numérico");
        }
    }

    // GET /pistaPadel/courts/{courtId}/availability?date=YYYY-MM-DD
    @GetMapping("/courts/{courtId}/availability")
    public ResponseEntity<?> getCourtAvailability(
            @PathVariable String courtId,
            @RequestParam(name = "date", required = false) String date
    ) {
        logger.info("GET /courts/{}/availability - date={}", courtId, date);
        if (date == null || date.isBlank()) {
            logger.error("Falta parámetro obligatorio: date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El parámetro date es obligatorio");
        }
        try {
            LocalDate fecha = LocalDate.parse(date);
            Long idPista = Long.parseLong(courtId);

            logger.debug("Consultando disponibilidad de pista {} para fecha {}", idPista, fecha);
            Disponibilidad disponibilidad = disponibilidadService.obtenerDisponibilidadPista(idPista, fecha);
            logger.info("Disponibilidad de pista {} calculada correctamente para fecha {}", idPista, fecha);
            return ResponseEntity.ok(disponibilidad);

        } catch (DateTimeParseException e) {
            logger.error("Formato de fecha inválido: {}", date);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de fecha inválido. Usa YYYY-MM-DD");
        } catch (NumberFormatException e) {
            logger.error("courtId inválido: {}", courtId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "courtId debe ser numérico");
        }
    }
}