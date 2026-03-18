package edu.comillas.icai.gitt.pat.spring.mvc.service;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.EstadoReserva;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Pista;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Reserva;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.Rol;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoPistas;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoReserva;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RepoPistas pistaRepo;
    @Autowired
    private RepoReserva reservaRepo;

    private static final LocalTime HORA_APERTURA = LocalTime.of(9, 0);
    private static final LocalTime HORA_CIERRE = LocalTime.of(22, 0);

    // Crear reserva nueva asociada al usuario autenticado
    public Reserva crearReserva(Usuario usuarioAutenticado, Reserva nuevaReserva) {
        logger.info("Creando nueva reserva para usuario {}", usuarioAutenticado.getIdUsuario());

        // Validar los campos básicos
        validarDatosReserva(nuevaReserva);

        // Sacamos el id de la pista del JSON
        Long pistaId = obtenerPistaId(nuevaReserva);

        // Comprobamos la existencia de la pista en la BD
        Pista pista = pistaRepo.findById(pistaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pista no encontrada"));

        // No se puede reservar una pista desactivada
        if (Boolean.FALSE.equals(pista.getActiva())) {
            logger.error("Intento de reserva sobre pista inactiva: {}", pistaId);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La pista no esta activa");
        }

        // Validamos que el horario este dentro de 9-22
        validarHorario(nuevaReserva.getHoraInicio(), nuevaReserva.getDuracionMinutos());

        // Validamos que la reserva sea futura
        validarFechaFutura(nuevaReserva.getFechaReserva(), nuevaReserva.getHoraInicio());

        validarNoSolapamiento(
                pista,
                nuevaReserva.getFechaReserva(),
                nuevaReserva.getHoraInicio(),
                nuevaReserva.getDuracionMinutos(),
                null
        );

        // Creamos una nueva entidad limpia
        Reserva reserva = new Reserva();
        reserva.setPista(pista);
        reserva.setUsuario(usuarioAutenticado);
        reserva.setFechaReserva(nuevaReserva.getFechaReserva());
        reserva.setHoraInicio(nuevaReserva.getHoraInicio());
        reserva.setDuracionMinutos(nuevaReserva.getDuracionMinutos());
        reserva.setEstado(EstadoReserva.ACTIVA);

        Reserva guardada = reservaRepo.save(reserva);
        logger.info("Reserva creada correctamente con id {}", guardada.getIdReserva());
        return guardada;
    }

    // Devuelve las reservas del usuario con/sin filtro de fechas
    public List<Reserva> listarMisReservas(Usuario usuarioAutenticado, LocalDate from, LocalDate to) {
        logger.info("Listando reservas del usuario {}", usuarioAutenticado.getIdUsuario());

        List<Reserva> reservas;

        if (from != null && to != null) {
            reservas = reservaRepo.findByUsuarioAndFechaReservaBetween(usuarioAutenticado, from, to);
        } else {
            reservas = reservaRepo.findByUsuario(usuarioAutenticado);
        }

        // Ordenamos por fecha y hora
        reservas.sort(Comparator
                .comparing(Reserva::getFechaReserva)
                .thenComparing(Reserva::getHoraInicio));

        logger.debug("Reservas encontradas para usuario {}: {}", usuarioAutenticado.getIdUsuario(), reservas.size());
        return reservas;
    }

    // Obtiene una reserva concreta si el usuario tiene permiso para verla
    public Reserva obtenerReserva(Long reservationId, Usuario usuarioAutenticado) {
        logger.info("Obteniendo reserva {} para usuario {}", reservationId, usuarioAutenticado.getIdUsuario());

        Reserva reserva = reservaRepo.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        validarAcceso(reserva, usuarioAutenticado);
        return reserva;
    }

    // Cancela una reserva cambiando su estado a cancelada
    @Transactional
    public void cancelarReserva(Long reservationId, Usuario usuarioAutenticado) {
        logger.info("Cancelando reserva {} para usuario {}", reservationId, usuarioAutenticado.getIdUsuario());

        Reserva reserva = reservaRepo.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        validarAcceso(reserva, usuarioAutenticado);

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            logger.error("La reserva {} ya estaba cancelada", reservationId);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La reserva ya esta cancelada");
        }

        // Solo permito cancelar reservas futuras
        validarFechaFutura(reserva.getFechaReserva(), reserva.getHoraInicio());
        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepo.save(reserva);

        logger.info("Reserva {} cancelada correctamente", reservationId);
    }

    // Modifica parcialmente una reserva ya existente
    @Transactional
    public Reserva modificarReserva(Long reservationId, Reserva datosActualizar, Usuario usuarioAutenticado) {
        logger.info("Modificando reserva {} para usuario {}", reservationId, usuarioAutenticado.getIdUsuario());

        Reserva reserva = reservaRepo.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        validarAcceso(reserva, usuarioAutenticado);

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            logger.error("Intento de modificar reserva cancelada {}", reservationId);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede modificar una reserva ya cancelada");
        }

        // Si con PATCH se manda otra pista la usamos, sino mantenemos la actual
        Pista pistaFinal = reserva.getPista();
        if (datosActualizar.getPista() != null && datosActualizar.getPista().getIdPista() != null) {
            pistaFinal = pistaRepo.findById(datosActualizar.getPista().getIdPista())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pista no encontrada"));
        }

        // Si no viene nada en el PATCH dejamos el valor que ya tenía
        LocalDate fechaFinal = datosActualizar.getFechaReserva() != null
                ? datosActualizar.getFechaReserva()
                : reserva.getFechaReserva();

        LocalTime horaInicioFinal = datosActualizar.getHoraInicio() != null
                ? datosActualizar.getHoraInicio()
                : reserva.getHoraInicio();

        Integer duracionFinal = datosActualizar.getDuracionMinutos() != null
                ? datosActualizar.getDuracionMinutos()
                : reserva.getDuracionMinutos();

        // Validamos el resultado final completo antes de guardar
        validarHorario(horaInicioFinal, duracionFinal);
        validarFechaFutura(fechaFinal, horaInicioFinal);
        validarNoSolapamiento(pistaFinal, fechaFinal, horaInicioFinal, duracionFinal, reserva.getIdReserva());

        reserva.setPista(pistaFinal);
        reserva.setFechaReserva(fechaFinal);
        reserva.setHoraInicio(horaInicioFinal);
        reserva.setDuracionMinutos(duracionFinal);

        Reserva actualizada = reservaRepo.save(reserva);
        logger.info("Reserva {} modificada correctamente", reservationId);
        return actualizada;
    }

    public List<Reserva> listarReservasAdmin(LocalDate fecha, Long pistaId, Long usuarioId) {
        logger.info("Listando reservas admin con filtros fecha={} pistaId={} usuarioId={}", fecha, pistaId, usuarioId);

        List<Reserva> reservas = new ArrayList<>();
        reservaRepo.findAll().forEach(reservas::add);

        if (fecha != null) {
            reservas.removeIf(reserva -> !reserva.getFechaReserva().equals(fecha));
        }

        if (pistaId != null) {
            reservas.removeIf(reserva ->
                    reserva.getPista() == null ||
                            reserva.getPista().getIdPista() == null ||
                            !reserva.getPista().getIdPista().equals(pistaId));
        }

        if (usuarioId != null) {
            reservas.removeIf(reserva ->
                    reserva.getUsuario() == null ||
                            reserva.getUsuario().getIdUsuario() == null ||
                            !reserva.getUsuario().getIdUsuario().equals(usuarioId));
        }

        reservas.sort(Comparator
                .comparing(Reserva::getFechaReserva)
                .thenComparing(Reserva::getHoraInicio));

        logger.debug("Reservas admin encontradas tras aplicar filtros: {}", reservas.size());
        return reservas;
    }


    // Comprobar que el JSON tenga los datos mínimos obligatorios
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

        if (reserva.getDuracionMinutos() % 30 != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La duración debe ser múltiplo de 30 minutos");
        }
    }

    // Extrae el id de la pista del objeto recibido
    private Long obtenerPistaId(Reserva reserva) {
        if (reserva.getPista() == null || reserva.getPista().getIdPista() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes indicar la pista");
        }
        return reserva.getPista().getIdPista();
    }

    // Valida que la hora esté dentro del horario del club
    private void validarHorario(LocalTime horaInicio, Integer duracionMinutos) {
        LocalTime horaFin = horaInicio.plusMinutes(duracionMinutos);

        if (horaInicio.isBefore(HORA_APERTURA) || horaFin.isAfter(HORA_CIERRE) || !horaFin.isAfter(horaInicio)) {
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
                pista, fecha, EstadoReserva.ACTIVA
        );

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
    // Este es el metodo que usa la Tarea Programada a las 2:00 AM
    @Transactional
    public void enviarRecordatoriosDiarios() {
        LocalDate hoy = LocalDate.now();

        // Buscamos SOLO las reservas de hoy que estén en estado ACTIVA
        List<Reserva> reservasActivasDeHoy = reservaRepo.findByFechaReservaAndEstado(hoy, EstadoReserva.ACTIVA);

        // Recorremos la lista de reservas
        for (Reserva reserva : reservasActivasDeHoy) {

            // Sacamos el usuario (funciona perfecto gracias al @Transactional)
            Usuario usuario = reserva.getUsuario();

            // 4. LÓGICA DE ENVIAR CORREOS
            String cuerpoMensaje = "Hola " + usuario.getNombre() + ",\n\n" +
                    "Te escribimos para recordarte que hoy tienes una reserva confirmada en nuestras pistas.\n" +
                    "Hora de inicio: " + reserva.getHoraInicio() + "\n\n" +
                    "¡Te esperamos!";

            // Configurar JavaMailSender, iría el código de envío real. Por ahora, logger
            logger.info("Enviando recordatorio PARA: {} - MENSAJE: \n{}", usuario.getEmail(), cuerpoMensaje);
        }
    }

    }

