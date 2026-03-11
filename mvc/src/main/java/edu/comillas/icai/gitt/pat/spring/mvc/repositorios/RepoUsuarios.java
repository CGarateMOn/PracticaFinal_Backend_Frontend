package edu.comillas.icai.gitt.pat.spring.mvc.repositorios;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import org.springframework.data.repository.CrudRepository;

public interface RepoUsuarios extends CrudRepository<Usuario, Integer> {
}
