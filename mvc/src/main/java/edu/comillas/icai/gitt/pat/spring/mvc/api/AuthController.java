package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Token;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.LoginRequest;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.ProfileResponse;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.RegisterRequest;
import edu.comillas.icai.gitt.pat.spring.mvc.service.AuthService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.PistaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.ReservaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/pistaPadel/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse register(
            @Valid @RequestBody RegisterRequest register){
        try{
            return authService.registrarUsuario(register);
        }catch (DataIntegrityViolationException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(),e);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Usuario> me(){
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@Valid @RequestBody LoginRequest loginRequest ){
        Token token = authService.login(loginRequest.email(),loginRequest.password());
        if(token == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        ResponseCookie session = ResponseCookie
                .from("session", token.id)
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .build();
        return  ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.SET_COOKIE, session.toString()).build();

    }



}
