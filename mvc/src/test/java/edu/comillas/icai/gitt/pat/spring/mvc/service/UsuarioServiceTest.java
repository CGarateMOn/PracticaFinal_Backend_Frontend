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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private RepoUsuarios usuarioRepo;
    // @Mock AuthService porque UsuarioService lo necesita para comprobar la sesión
    @Mock
    private AuthService authService;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void testListarTodos_ExitoSiEsAdmin() {
        // Arranque
        String sessionToken = "tokenAdminValido";

        Usuario admin = new Usuario();
        admin.setIdUsuario(1L);
        admin.setRol(Rol.ADMIN); // Es Admin

        Usuario usuarioNormal = new Usuario();
        usuarioNormal.setIdUsuario(2L);
        usuarioNormal.setRol(Rol.USER);

        // Simulamos que AuthService reconoce el token como válido y devuelve al Admin
        when(authService.authentication(sessionToken)).thenReturn(admin);

        // Simulamos que la base de datos devuelve una lista de usuarios
        when(usuarioRepo.findAll()).thenReturn(List.of(admin, usuarioNormal));

        List<Usuario> resultado = usuarioService.listarTodos(sessionToken);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(usuarioRepo, times(1)).findAll();
    }

    @Test
    void testListarTodos_FallaSiNoEsAdmin() {
        // Arranque
        String sessionToken = "tokenUserValido";

        Usuario usuarioNormal = new Usuario();
        usuarioNormal.setIdUsuario(2L);
        usuarioNormal.setRol(Rol.USER); // NO es Admin

        // Simulamos que AuthService devuelve a un usuario normal
        when(authService.authentication(sessionToken)).thenReturn(usuarioNormal);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            usuarioService.listarTodos(sessionToken);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Solo administradores", exception.getReason());

        // Verificamos que al fallar la seguridad, nunca llegó a pedir todos los usuarios a la BD
        verify(usuarioRepo, never()).findAll();
    }

    @Test
    void testBuscarPorId_ExitoSiEsElMismoUsuario() {
        // Arranque
        String sessionToken = "miToken";
        Long miId = 5L;

        Usuario yo = new Usuario();
        yo.setIdUsuario(miId);
        yo.setRol(Rol.USER);

        when(authService.authentication(sessionToken)).thenReturn(yo);
        when(usuarioRepo.findById(miId)).thenReturn(Optional.of(yo));

        Usuario resultado = usuarioService.buscarPorId(miId, sessionToken);

        assertNotNull(resultado);
        assertEquals(miId, resultado.getIdUsuario());
    }

    @Test
    void testBuscarPorId_FallaSiIntentaVerAOtroYNoEsAdmin() {
        // Arrange
        String sessionToken = "miToken";
        Long miId = 5L;
        Long idDeOtro = 10L; // Quiero ver los datos de este usuario

        Usuario yo = new Usuario();
        yo.setIdUsuario(miId);
        yo.setRol(Rol.USER); // No soy admin

        // AuthService dice que soy yo (id 5)
        when(authService.authentication(sessionToken)).thenReturn(yo);

        // Intento buscar al usuario 10
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            usuarioService.buscarPorId(idDeOtro, sessionToken);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Sin permisos", exception.getReason());
    }
}