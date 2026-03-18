package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import edu.comillas.icai.gitt.pat.spring.mvc.records.UpdateUsuarioRequest;
import edu.comillas.icai.gitt.pat.spring.mvc.service.UsuarioService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import jakarta.servlet.http.Cookie;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Apagamos la autoconfiguración de seguridad para que no pida login, ya comprobaremos la seguridad en el E2E
@WebMvcTest(controllers = UsuarioController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
// Apagamos los filtros internos (como la protección CSRF) para que deje pasar los PATCH
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @Test
    void testListarUsuarios_Devuelve200YLista() throws Exception {
        Usuario usuario1 = new Usuario();
        usuario1.setEmail("test@test.com");

        String cookieFalsa = "sesionValida123";
        when(usuarioService.listarTodos(cookieFalsa)).thenReturn(List.of(usuario1));

        mockMvc.perform(get("/pistaPadel/users")
                        .cookie(new Cookie("session", cookieFalsa)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].email").value("test@test.com"));
    }

    @Test
    void testListarUsuarios_SinCookie_DevuelveError400() throws Exception {
        mockMvc.perform(get("/pistaPadel/users"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testActualizarUsuario_Devuelve200YUsuarioActualizado() throws Exception {
        Long userId = 1L;
        String cookieFalsa = "sesionValida123";

        UpdateUsuarioRequest request = new UpdateUsuarioRequest(
                "NuevoNombre",
                "NuevosApellidos",
                "nuevo@email.com",
                "600123456"
        );

        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setEmail("nuevo@email.com");

        when(usuarioService.actualizar(eq(userId), any(UpdateUsuarioRequest.class), eq(cookieFalsa)))
                .thenReturn(usuarioActualizado);

        mockMvc.perform(patch("/pistaPadel/users/{userId}", userId)
                        .cookie(new Cookie("session", cookieFalsa))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("nuevo@email.com"));
    }
}