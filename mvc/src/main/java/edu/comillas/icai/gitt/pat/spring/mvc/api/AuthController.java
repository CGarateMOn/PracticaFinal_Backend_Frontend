package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Token;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.LoginRequest;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.ProfileResponse;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.RegisterRequest;
import edu.comillas.icai.gitt.pat.spring.mvc.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/pistaPadel/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse register(@Valid @RequestBody RegisterRequest register) {
        return authService.registrarUsuario(register);
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public ProfileResponse me(@CookieValue(value = "session", required = true) String session){
        return  authService.perfil(session);
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@Valid @RequestBody LoginRequest loginRequest){
        Token token = authService.login(loginRequest.email(), loginRequest.password());
        if(token == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Usuario usuario = token.getUsuario();

        ResponseCookie session = ResponseCookie
                .from("session", token.id)
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, session.toString())
                .body(usuario);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> logout(@CookieValue(value = "session", required = true) String session){
        if(session == null){
            throw  new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        Usuario usuario = authService.authentication(session);
        if(usuario == null){
            throw  new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        authService.logout(session);
        ResponseCookie expireSession = ResponseCookie
                .from("session")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).header(HttpHeaders.SET_COOKIE, expireSession.toString()).build();
    }

    @PutMapping("/actualizar")
    @ResponseStatus(HttpStatus.OK)
    public void actualizarPerfil(@RequestBody Usuario datosRecibidos,
                                 @CookieValue(value = "session", required = true) String session) {

        // 1. Identificamos al usuario por su sesión
        Usuario usuarioActual = authService.authentication(session);

        if (usuarioActual == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión no válida");
        }

        // 2. Actualizamos los campos básicos si vienen en el JSON
        if (datosRecibidos.getNombre() != null) usuarioActual.setNombre(datosRecibidos.getNombre());
        if (datosRecibidos.getApellidos() != null) usuarioActual.setApellidos(datosRecibidos.getApellidos());
        if (datosRecibidos.getTelefono() != null) usuarioActual.setTelefono(datosRecibidos.getTelefono());

        // 3. Manejo especial del email (podrías validar si ya existe otro igual)
        if (datosRecibidos.getEmail() != null && !datosRecibidos.getEmail().equals(usuarioActual.getEmail())) {
            usuarioActual.setEmail(datosRecibidos.getEmail());
        }

        // 4. Si el usuario introdujo una contraseña nueva, la hasheamos y guardamos
        if (datosRecibidos.getPassword() != null && !datosRecibidos.getPassword().isEmpty()) {
            // Importante: aquí deberías usar tu clase Hashing para que no se guarde en texto plano
            // Suponiendo que tienes acceso a 'hashing' en este controller o servicio
            usuarioActual.setPassword(datosRecibidos.getPassword());
        }

        // 5. Guardamos todos los cambios
        authService.actualizarUsuario(usuarioActual);
    }

}
