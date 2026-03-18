package edu.comillas.icai.gitt.pat.spring.mvc.repositorios;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.Rol;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

// Levantamos solo la capa de base de datos de Spring
@DataJpaTest
class RepoUsuariosTest {

    // Inyectamos el repositorio real
    @Autowired
    private RepoUsuarios repoUsuarios;

    @Test
    void testGuardarYBuscarPorEmail_IntegracionReal() {
        // Creamos un usuario de verdad para meterlo en la base de datos
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail("integracion@test.com");
        nuevoUsuario.setPassword("clave123");
        nuevoUsuario.setNombre("Paco");
        nuevoUsuario.setApellidos("Pruebas");
        nuevoUsuario.setTelefono("600112233");
        nuevoUsuario.setRol(Rol.USER);
        nuevoUsuario.setFechaRegistro(LocalDateTime.now());

        // lo guardamos en la base de datos real en memoria
        repoUsuarios.save(nuevoUsuario);

        // Hacemos una consulta SELECT real a la base de datos usando tu metodo
        Usuario encontrado = repoUsuarios.findByEmail("integracion@test.com");

        // Comprobamos que la base de datos nos lo ha devuelto correctamente
        assertNotNull(encontrado, "El usuario debería haberse guardado y encontrado en la BD");
        assertEquals("Paco", encontrado.getNombre());
        assertEquals("Pruebas", encontrado.getApellidos());

        // Comprobamos que Hibernate le ha asignado un ID automáticamente (GenerationType.IDENTITY)
        assertNotNull(encontrado.getIdUsuario(), "La base de datos debería haberle asignado un ID");
    }

    @Test
    void testExistsByEmail_DevuelveTrueSiExiste() {
        // Guardamos un usuario en la BD
        Usuario usuario = new Usuario();
        usuario.setEmail("existe@correo.com");
        usuario.setPassword("1234");
        usuario.setNombre("Ana");
        usuario.setApellidos("García");
        usuario.setTelefono("111222333");
        usuario.setRol(Rol.USER);
        usuario.setFechaRegistro(LocalDateTime.now());

        repoUsuarios.save(usuario);

        // Usamos tu metodo existsByEmail
        boolean existe = repoUsuarios.existsByEmail("existe@correo.com");
        boolean noExiste = repoUsuarios.existsByEmail("fantasma@correo.com");

        // Assert
        assertTrue(existe, "Debería devolver true porque acabamos de insertar el email");
        assertFalse(noExiste, "Debería devolver false para un email que no está en la BD");
    }
}