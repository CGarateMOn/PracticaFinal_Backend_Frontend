package edu.comillas.icai.gitt.pat.spring.mvc.repositorios;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RepoUsuarios extends CrudRepository<Usuario, Long> {

    // POST /auth/register → verificar si el email ya existe (409)
    boolean existsByEmail(String email);

    // POST /auth/login → buscar usuario por email para autenticar
    Optional<Usuario> findByPassword(String password);

    // GET /users → listar todos
    List<Usuario> findAll();

    Usuario findByEmail(String email);

    // Devuelve directamente solo los usuarios donde activo = true
    List<Usuario> findByActivoTrue(); //usado en Tarea progarmada en logica de servico en PistService
}
