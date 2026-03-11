package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.records.Disponibilidad;
import edu.comillas.icai.gitt.pat.spring.mvc.service.PistaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.ReservaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/pistaPadel")
public class DisponibilidadController {

    @Autowired
    PistaService pistaService;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    ReservaService reservaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/availability")
    public ResponseEntity<?> getAvailabilityByDate(
            @RequestParam(name = "date", required = false) String date,
            @RequestParam(name = "courtId", required = false) String courtId
    ) {
    }

    // GET /pistaPadel/courts/{courtId}/availability?date=YYYY-MM-DD
    @GetMapping("/courts/{courtId}/availability")
    public ResponseEntity<?> getCourtAvailability(
            @PathVariable String courtId,
            @RequestParam(name = "date", required = false) String date
    ) {
    }

}
