package edu.comillas.icai.gitt.pat.spring.mvc.repositorios;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.EstadoReserva;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Pista;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Reserva;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface RepoReserva extends CrudRepository<Reserva, Long> {
    // GET /reservations → mis reservas (USER), con filtro opcional de fechas
    List<Reserva> findByUsuario(Usuario usuario);
    List<Reserva> findByUsuarioAndFechaReservaBetween(Usuario usuario, LocalDate from, LocalDate to);

    // GET /admin/reservations → todas las reservas con filtros (ADMIN)
    List<Reserva> findByFechaReservaAndPistaAndUsuario(LocalDate fecha, Pista pista, Usuario usuario);

    // GET /availability → pistas disponibles en una fecha
    List<Reserva> findByFechaReservaAndEstado(LocalDate fecha, EstadoReserva estado);

    // GET /courts/{courtId}/availability → disponibilidad de una pista concreta
    List<Reserva> findByPistaAndFechaReservaAndEstado(Pista pista, LocalDate fecha, EstadoReserva estado);

    // DELETE /courts/{courtId} → verificar si hay reservas futuras (409)
    boolean existsByPistaAndFechaReservaAfterAndEstado(
            Pista pista, LocalDate fecha, EstadoReserva estado);
}
