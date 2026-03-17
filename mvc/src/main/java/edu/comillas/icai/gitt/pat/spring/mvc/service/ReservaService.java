package edu.comillas.icai.gitt.pat.spring.mvc.service;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.EstadoReserva;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Pista;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Reserva;
import edu.comillas.icai.gitt.pat.spring.mvc.records.Disponibilidad;
import edu.comillas.icai.gitt.pat.spring.mvc.records.TramosHorarios;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoPistas;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoReserva;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoUsuarios;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private RepoPistas pistaRepo;
    @Autowired
    private RepoReserva reservaRepo;

    private static final LocalTime HORA_APERTURA = LocalTime.of(9, 0);
    private static final LocalTime HORA_CIERRE = LocalTime.of(22, 0);

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public List<Disponibilidad> obtenerDisponibilidadPorFecha(LocalDate fecha) {
        logger.info("Calculando disponibilidad para todas las pistas en fecha {}", fecha);
        List<Pista> pistasActivas = pistaRepo.findByActiva(true);
        logger.debug("Número de pistas activas encontradas: {}", pistasActivas.size());
        List<Disponibilidad> resultado = new ArrayList<>();

        for (Pista pista : pistasActivas) {
            logger.debug("Procesando pista {}", pista.getIdPista());
            List<Reserva> reservasActivas = reservaRepo.findByPistaAndFechaReservaAndEstado(
                    pista, fecha, EstadoReserva.ACTIVA
            );

            logger.debug("Reservas activas encontradas para pista {}: {}", pista.getIdPista(), reservasActivas.size());
            List<TramosHorarios> huecos = calcularHuecosDisponibles(reservasActivas);

            resultado.add(new Disponibilidad(
                    pista.getIdPista(),
                    fecha,
                    huecos
            ));
        }
        logger.info("Disponibilidad general calculada correctamente para fecha {}", fecha);
        return resultado;
    }

    public Disponibilidad obtenerDisponibilidadPista(Long courtId, LocalDate fecha) {
        Pista pista = pistaRepo.findById(courtId)
                .orElseThrow(() -> {
                    logger.error("Pista no encontrada: {}", courtId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Pista no encontrada");
                });
        if (!Boolean.TRUE.equals(pista.getActiva())) {
            logger.error("Intento de consulta sobre pista inactiva: {}", courtId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La pista no está activa");
        }

        List<Reserva> reservasActivas = reservaRepo.findByPistaAndFechaReservaAndEstado(
                pista, fecha, EstadoReserva.ACTIVA
        );

        List<TramosHorarios> huecos = calcularHuecosDisponibles(reservasActivas);

        logger.info("Disponibilidad calculada correctamente para pista {} en fecha {}", courtId, fecha);
        return new Disponibilidad(
                pista.getIdPista(),
                fecha,
                huecos
        );
    }

    private List<TramosHorarios> calcularHuecosDisponibles(List<Reserva> reservas) {
        List<TramosHorarios> huecos = new ArrayList<>();

        reservas.sort(Comparator.comparing(Reserva::getHoraInicio));

        LocalTime cursor = HORA_APERTURA;

        for (Reserva reserva : reservas) {
            LocalTime inicioReserva = reserva.getHoraInicio();
            LocalTime finReserva = reserva.getHoraFin();

            // Si hay hueco entre el cursor y el inicio de la reserva, se añade
            if (cursor.isBefore(inicioReserva)) {
                huecos.add(new TramosHorarios(cursor, inicioReserva));
            }

            // Avanzamos el cursor al final de la reserva si está más adelante
            if (cursor.isBefore(finReserva)) {
                cursor = finReserva;
            }
        }

        // Hueco final hasta cierre
        if (cursor.isBefore(HORA_CIERRE)) {
            huecos.add(new TramosHorarios(cursor, HORA_CIERRE));
        }

        return huecos;
    }
}