package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.service.PistaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.ReservaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static edu.comillas.icai.gitt.pat.spring.mvc.data.AlmacenDatos.usuarios;

@RestController
@RequestMapping("/pistaPadel/auth")
public class AuthController {

    @Autowired
    PistaService pistaService;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    ReservaService reservaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    //Registrar a un usuario con rol user por defecto
    @PostMapping("/register")
    public ResponseEntity<Object> register(
            @Valid @RequestBody Usuario usuario, //Comprobamos si falta algún campo
            BindingResult result
    ){

    @GetMapping("/me")
    public ResponseEntity<Usuario> me(){
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(){

    }



}
