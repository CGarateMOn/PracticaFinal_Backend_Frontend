package edu.comillas.icai.gitt.pat.spring.mvc.repositorios;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Reserva;
import org.springframework.data.repository.CrudRepository;

public interface RepoReserva extends CrudRepository<Reserva, Integer> {
}
