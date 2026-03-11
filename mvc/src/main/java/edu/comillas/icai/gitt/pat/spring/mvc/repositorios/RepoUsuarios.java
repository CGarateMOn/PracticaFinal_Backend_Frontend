package edu.comillas.icai.gitt.pat.spring.mvc.repositorios;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RepoUsuarios extends CrudRepository<Usuario, Integer> {

    // POST /auth/register → verificar si el email ya existe (409)
    boolean existsByEmail(String email);

    // POST /auth/login → buscar usuario por email para autenticar
    Optional<Usuario> findByEmail(String email);

    // GET /users → listar todos
    List<Usuario> findAll();
}
