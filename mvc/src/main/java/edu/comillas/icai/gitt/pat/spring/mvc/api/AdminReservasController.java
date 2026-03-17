package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.service.PistaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.ReservaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/pistaPadel/admin")
public class AdminReservasController {

    @Autowired
    PistaService pistaService;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    ReservaService reservaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/reservations")
    public ResponseEntity<?> listarReservasAdmin(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String courtId,
            @RequestParam(required = false) String userId
    ) {return null;}
}
