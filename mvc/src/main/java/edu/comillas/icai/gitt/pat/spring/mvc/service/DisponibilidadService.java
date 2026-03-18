package edu.comillas.icai.gitt.pat.spring.mvc.service;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.EstadoReserva;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Pista;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Reserva;
import edu.comillas.icai.gitt.pat.spring.mvc.records.Disponibilidad;
import edu.comillas.icai.gitt.pat.spring.mvc.records.TramosHorarios;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoPistas;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoReserva;
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
public class DisponibilidadService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RepoPistas pistaRepo;

    @Autowired
    private RepoReserva reservaRepo;

    private static final LocalTime HORA_APERTURA = LocalTime.of(9, 0);
    private static final LocalTime HORA_CIERRE = LocalTime.of(22, 0);

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
        logger.info("Calculando disponibilidad para pista {} en fecha {}", courtId, fecha);

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

        logger.debug("Reservas activas encontradas para pista {}: {}", courtId, reservasActivas.size());

        List<TramosHorarios> huecos = calcularHuecosDisponibles(reservasActivas);

        logger.info("Disponibilidad calculada correctamente para pista {} en fecha {}", courtId, fecha);

        return new Disponibilidad(
                pista.getIdPista(),
                fecha,
                huecos
        );
    }

    private List<TramosHorarios> calcularHuecosDisponibles(List<Reserva> reservas) {
        logger.debug("Calculando huecos disponibles. Número de reservas: {}", reservas.size());

        List<TramosHorarios> huecos = new ArrayList<>();

        reservas.sort(Comparator.comparing(Reserva::getHoraInicio));

        LocalTime cursor = HORA_APERTURA;

        for (Reserva reserva : reservas) {
            LocalTime inicioReserva = reserva.getHoraInicio();
            LocalTime finReserva = reserva.getHoraFin();

            logger.debug("Procesando reserva: inicio={} fin={}", inicioReserva, finReserva);

            if (cursor.isBefore(inicioReserva)) {
                logger.debug("Hueco encontrado: {} - {}", cursor, inicioReserva);
                huecos.add(new TramosHorarios(cursor, inicioReserva));
            }

            if (cursor.isBefore(finReserva)) {
                cursor = finReserva;
            }
        }

        if (cursor.isBefore(HORA_CIERRE)) {
            logger.debug("Hueco final encontrado: {} - {}", cursor, HORA_CIERRE);
            huecos.add(new TramosHorarios(cursor, HORA_CIERRE));
        }

        logger.debug("Número total de huecos calculados: {}", huecos.size());
        return huecos;
    }
}