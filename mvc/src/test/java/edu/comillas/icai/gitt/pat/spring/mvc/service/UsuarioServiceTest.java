package edu.comillas.icai.gitt.pat.spring.mvc.service;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.Rol;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoUsuarios;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//un test unitario para comporbar la lógica de UsuarioService

// 1. Activamos Mockito para esta clase
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    // 2. Creamos un "doble" del repositorio. ¡No tocará la base de datos real!
    @Mock
    private RepoUsuarios usuarioRepo;

    // 3. Inyectamos ese doble en nuestro servicio real
    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void testAutentica_Exito() {
        // Arrange: Preparamos la mentira
        Usuario usuarioFalso = new Usuario();
        usuarioFalso.setPassword("claveSecreta");

        // Le decimos al mock: "Cuando te busquen por esta clave, devuelve este usuario"
        when(usuarioRepo.findByPassword("claveSecreta")).thenReturn(Optional.of(usuarioFalso));

        // Act: Llamamos al servicio real
        Usuario resultado = usuarioService.Autentica("claveSecreta");

        // Assert: Comprobamos que nos devuelve el usuario correcto
        assertNotNull(resultado);
        assertEquals("claveSecreta", resultado.getPassword());

        // Verificamos que el servicio efectivamente consultó al repositorio
        verify(usuarioRepo, times(1)).findByPassword("claveSecreta");
    }

    @Test
    void testAutentica_FallaPorCredencialesIncorrectas() {
        // Arrange: Mentimos diciendo que no hay nadie con esa clave (Optional.empty)
        when(usuarioRepo.findByPassword("claveErronea")).thenReturn(Optional.empty());

        // Act & Assert: Comprobamos que lanza la excepción exacta que tú programaste
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            usuarioService.Autentica("claveErronea");
        });

        // Verificamos que el código HTTP es 401 UNAUTHORIZED
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Credenciales no encontrados", exception.getReason());
    }

    @Test
    void testAutenticaAdmin_FallaPorNoTenerRolAdmin() {
        // Arrange: Creamos un usuario pero le ponemos un rol distinto a ADMIN
        Usuario usuarioNormal = new Usuario();
        usuarioNormal.setPassword("claveUser");
        // Nota: Asumo que tienes un Rol.USER u otro distinto a ADMIN en tu Enum
        usuarioNormal.setRol(Rol.USER);

        when(usuarioRepo.findByPassword("claveUser")).thenReturn(Optional.of(usuarioNormal));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            usuarioService.AutenticaAdmin("claveUser");
        });

        // Verificamos que el código HTTP es 403 FORBIDDEN
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("No es administrador", exception.getReason());
    }
}