package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Token;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.Rol;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoToken;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoUsuarios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import jakarta.servlet.http.Cookie;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc // <-- Fíjate: Ya NO apagamos los filtros de seguridad. ¡Están 100% encendidos!
class UsuarioE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RepoUsuarios repoUsuarios;

    @Autowired
    private RepoToken repoToken;

    private String tokenValido;

    @BeforeEach
    void prepararBaseDeDatosReal() {
        repoToken.deleteAll();
        repoUsuarios.deleteAll();

        Usuario admin = new Usuario();
        admin.setEmail("jefaza@padel.com");
        admin.setPassword("supersecreta");
        admin.setNombre("Martina");
        admin.setApellidos("García");
        admin.setTelefono("600123456");
        admin.setRol(Rol.ADMIN);
        admin.setFechaRegistro(LocalDateTime.now());

        repoUsuarios.save(admin);

        Token token = new Token();
        token.usuario = admin;

        Token tokenGuardado = repoToken.save(token);
        tokenValido = tokenGuardado.id;
    }

    // MAGIA APLICADA: Esta etiqueta le dice al filtro de Spring Security que deje pasar la petición
    @Test
    @WithMockUser(username = "jefaza@padel.com", roles = {"ADMIN"})
    void testFlujoCompleto_ListarUsuarios_PasaSeguridadYConsultaBD() throws Exception {

        // El test superará a Spring Security gracias a @WithMockUser
        // y luego superará a tu AuthService gracias a la Cookie real.
        mockMvc.perform(get("/pistaPadel/users")
                        .cookie(new Cookie("session", tokenValido)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("jefaza@padel.com"));
    }
}