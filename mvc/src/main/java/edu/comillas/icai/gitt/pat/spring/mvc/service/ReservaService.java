package edu.comillas.icai.gitt.pat.spring.mvc.service;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.EstadoReserva;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Pista;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Reserva;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.Rol;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoPistas;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoReserva;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoUsuarios;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Autowired
    private RepoUsuarios usuarioRepo;

    //Creas reserva nueva asociada al usuario autenticado
    public Reserva crearReserva(Usuario usuarioAutenticado, Reserva nuevaReserva) {
        //Validas los campos basicos
        validarDatosReserva(nuevaReserva);

        // Sacamos el id de la pista del JSON
        Long pistaId = obtenerPistaId(nuevaReserva);

        //Comprobamos la existencia de la pista en la BD
        Pista pista = pistaRepo.findById(pistaId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pista no encontrada"));

        //No se puede reservar una pista desactivada
        if (Boolean.FALSE.equals(pista.getActiva())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La pista no esta activa");
        }

        //Validamos que el horario este dentro de 9-22
        validarHorario(nuevaReserva.getHoraInicio(),nuevaReserva.getDuracionMinutos());

        //Validamos que la reserva sea futura
        validarFechaFutura(nuevaReserva.getFechaReserva(), nuevaReserva.getHoraInicio());

        validarNoSolapamiento(
                pista,
                nuevaReserva.getFechaReserva(),
                nuevaReserva.getHoraInicio(),
                nuevaReserva.getDuracionMinutos(),
                null
        );

        //Creamos una nueva entidad limpia
        Reserva reserva = new Reserva();
        reserva.setPista(pista);
        reserva.setUsuario(usuarioAutenticado);
        reserva.setFechaReserva(nuevaReserva.getFechaReserva());
        reserva.setHoraInicio(nuevaReserva.getHoraInicio());
        reserva.setDuracionMinutos(nuevaReserva.getDuracionMinutos());
        reserva.setEstado(EstadoReserva.ACTIVA);

        //Guardamos en la base de datos
        return  reservaRepo.save(reserva);
    }

    //Devuelve las reservas del usuario con/sin filtro de fechas
    public List<Reserva> listarMisReservas(Usuario usuarioAutenticado, LocalDate from, LocalDate to) {
        List<Reserva> reservas;

        if (from != null && to != null) {
            reservas=reservaRepo.findByUsuarioAndFechaReservaBetween(usuarioAutenticado, from, to);
        }
        else
        {
            reservas = reservaRepo.findByUsuario(usuarioAutenticado);
        }

        //Ordenamos por fecha y hora
        reservas.sort(Comparator
                .comparing(Reserva::getFechaReserva)
                .thenComparing(Reserva::getHoraInicio));
        return reservas;

    }

    //Obtiene una reserva concreta si el usuario tiene permiso para verla
    public Reserva obtenerReserva(Long reservationId, Usuario usuarioAutenticado) {
        Reserva reserva = reservaRepo.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        validarAcceso(reserva, usuarioAutenticado);
        return reserva;

    }

    //Cancela una reserva cambiando su estado a cancelada
    @Transactional
    public void cancelarReserva(Long reservationId, Usuario usuarioAutenticado) {
        Reserva reserva = reservaRepo.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        validarAcceso(reserva, usuarioAutenticado);

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La reserva ya esta cancelada");
        }

        //Solo permito cancelar reservas futuras
        validarFechaFutura(reserva.getFechaReserva(), reserva.getHoraInicio());
        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepo.save(reserva);
    }

    //Modifica parcialmente una reserva ya existente
    @Transactional
    public Reserva modificarReserva(Long reservationId, Reserva datosActualizar, Usuario usuarioAutenticado) {
        Reserva reserva = reservaRepo.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        validarAcceso(reserva, usuarioAutenticado);

        if(reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede modificar una reserva ya cancelada");

        }
            //Si con PATCH se manda otra pista la usamos, sino mantenemos la actual
            Pista pistaFinal = reserva.getPista();
            if (datosActualizar.getPista() != null && datosActualizar.getPista().getIdPista() != null) {
                pistaFinal = pistaRepo.findById(datosActualizar.getPista().getIdPista())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pista no encontrada"));
            }

            //Si no viene nada en el PATCH dejamos el valor que ya tenia
            LocalDate fechaFinal = datosActualizar.getFechaReserva() != null
                    ? datosActualizar.getFechaReserva()
                    : reserva.getFechaReserva();

            LocalTime horaInicioFinal = datosActualizar.getHoraInicio() != null
                    ? datosActualizar.getHoraInicio()
                    : reserva.getHoraInicio();

            Integer duracionFinal = datosActualizar.getDuracionMinutos() != null
                    ? datosActualizar.getDuracionMinutos()
                    : reserva.getDuracionMinutos();

            //Validamos el resultado final completo antes de guardar
            validarHorario(horaInicioFinal, duracionFinal);
            validarFechaFutura(fechaFinal, horaInicioFinal);
            validarNoSolapamiento(pistaFinal, fechaFinal, horaInicioFinal, duracionFinal, reserva.getIdReserva());

            reserva.setPista(pistaFinal);
            reserva.setFechaReserva(fechaFinal);
            reserva.setHoraInicio(horaInicioFinal);
            reserva.setDuracionMinutos(duracionFinal);

            return reservaRepo.save(reserva);
        }

        //Comprobar que el JSON tenga los datos minimos obligatorios
        private void validarDatosReserva(Reserva reserva) {
            if (reserva.getFechaReserva() == null || reserva.getHoraInicio() == null || reserva.getDuracionMinutos() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan datos obligatorios");
            }

            if (reserva.getPista() == null || reserva.getPista().getIdPista() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes indicar la pista");

            }

            if (reserva.getDuracionMinutos() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La duracion debe de ser mayor a 0");
            }

        }

        //Extrae el id de la pista del objeto recibido
        private Long obtenerPistaId(Reserva reserva) {
            if ( reserva.getPista() == null || reserva.getPista().getIdPista() == null ) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes indicar la pista");
            }
            return reserva.getPista().getIdPista();
        }

        // Valida que la hora esté dentro del horario del club
        private void validarHorario(LocalTime horaInicio, Integer duracionMinutos) {
            LocalTime apertura = LocalTime.of(9, 0);
            LocalTime cierre = LocalTime.of(22, 0);
            LocalTime horaFin = horaInicio.plusMinutes(duracionMinutos);

            if (horaInicio.isBefore(apertura) || horaFin.isAfter(cierre) || !horaFin.isAfter(horaInicio)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La reserva está fuera del horario permitido");
            }
        }

        // Valida que la reserva sea posterior al momento actual
        private void validarFechaFutura(LocalDate fecha, LocalTime horaInicio) {
            LocalDateTime inicioReserva = LocalDateTime.of(fecha, horaInicio);
            if (!inicioReserva.isAfter(LocalDateTime.now())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "La reserva debe ser futura");
            }
        }

        // Comprueba si la nueva reserva se cruza con otra ya existente
        private void validarNoSolapamiento(Pista pista, LocalDate fecha, LocalTime horaInicio,
                Integer duracionMinutos, Long reservaExcluirId) {
            LocalTime nuevaHoraFin = horaInicio.plusMinutes(duracionMinutos);

            List<Reserva> reservasActivas = reservaRepo.findByPistaAndFechaReservaAndEstado(
                    pista, fecha, EstadoReserva.ACTIVA);

            for (Reserva existente : reservasActivas) {
                // En PATCH ignoramos la propia reserva que estamos modificando
                if (reservaExcluirId != null && existente.getIdReserva().equals(reservaExcluirId)) {
                    continue;
                }

                LocalTime inicioExistente = existente.getHoraInicio();
                LocalTime finExistente = existente.getHoraFin();

                // Dos reservas solapan si la nueva empieza antes de que termine la otra
                // y termina después de que empiece la otra
                boolean solapa = horaInicio.isBefore(finExistente) && nuevaHoraFin.isAfter(inicioExistente);

                if (solapa) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "La pista ya está reservada en ese horario");
                }
            }
        }

        // Permite acceso si el usuario es dueño de la reserva o es admin
        private void validarAcceso(Reserva reserva, Usuario usuarioAutenticado) {
            boolean esPropietario = reserva.getUsuario().getIdUsuario().equals(usuarioAutenticado.getIdUsuario());
            boolean esAdmin = usuarioAutenticado.getRol() == Rol.ADMIN;

            if (!esPropietario && !esAdmin) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes acceso a esta reserva");
            }
        }

    }

