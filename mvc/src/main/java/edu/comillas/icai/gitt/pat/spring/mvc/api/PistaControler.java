package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Pista;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import edu.comillas.icai.gitt.pat.spring.mvc.records.PistaPatchForm;
import edu.comillas.icai.gitt.pat.spring.mvc.service.PistaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.ReservaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pistaPadel")
public class PistaControler {

    @Autowired
    PistaService pistaService;
    @Autowired
    UsuarioService usuarioService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping("/courts")
    @ResponseStatus(HttpStatus.CREATED)
    public Pista crearPista(@RequestBody Pista pista,
                            @CookieValue(value = "session", required = true) String session) {
        usuarioService.autenticarAdmin(session);
        return pistaService.crearPista(pista);
    }

    @GetMapping("/courts")
    public List<Pista> obtenerPistas(@RequestParam(required = false) Boolean activa,
                                     @CookieValue(value = "session", required = true) String session) {
        usuarioService.autenticar(session);
        if (activa != null) {
            return pistaService.getActivas(activa);
        }
        return pistaService.getTodas();
    }

    @GetMapping("/courts/{courtId}")
    public Pista obtenerPista(@PathVariable("courtId") Long courtId,
                              @CookieValue(value = "session", required = true) String session) {
        usuarioService.autenticar(session);
        return pistaService.getById(courtId);
    }

    @PatchMapping("/courts/{courtId}")
    public Pista modificarPista(@PathVariable("courtId") Long courtId,
                                @RequestBody PistaPatchForm pistaForm,
                                @CookieValue(value = "session", required = true) String session) {
        usuarioService.autenticarAdmin(session);
        return pistaService.modificarPista(courtId, pistaForm);
    }

    @DeleteMapping("/courts/{courtId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarPista(@PathVariable("courtId") Long courtId,
                              @CookieValue(value = "session", required = true) String session) {
        usuarioService.autenticarAdmin(session);
        pistaService.eliminaPista(courtId);
    }
}