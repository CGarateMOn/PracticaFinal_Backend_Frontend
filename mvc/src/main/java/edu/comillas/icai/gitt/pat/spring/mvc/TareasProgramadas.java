package edu.comillas.icai.gitt.pat.spring.mvc;

import edu.comillas.icai.gitt.pat.spring.mvc.data.AlmacenDatos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service //@Component sirve para que Spring registre la clase, vea que tiene métodos programados y
// activa el reloj interno para ejecutarlos cuando corresponda. tambien sepuede poner @Service hace
// lo mismo pero además indica que la clase contiene lógica de negocio
public class TareasProgramadas {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    // Inyectamos el controlador para acceder a sus datos

    @Scheduled(cron = "0 0 2 * * *")
    public void mandarRecordatorioReservas() {
        LocalDate hoy = LocalDate.now();
        logger.info("Iniciando envío de recordatorios (2:00 AM) para el día: {}", hoy);
        // Recorremos el mapa de reservas
        AlmacenDatos.reservas.values().forEach(reserva -> {
            // Comprobamos si la reserva es para hoy
            if (reserva.fechaReserva().toLocalDate().equals(hoy)) {
                // Obtenemos el ID del usuario de esa reserva
                String idUsuario = reserva.idUsuario();
                // Buscamos al usuario en el mapa de UsuarioController para obtener el email
                Usuario usuario = AlmacenDatos.usuarios.get(idUsuario);
                // RELLENAR CON LÓGICA DE ENVIAR CORREOS Y NO SOLO UN MENSJE EN LA TERMINAL
            }
        });
    }
    // Información de pistas el día 1 de cada mes a las 09:00 AM, se puede cambiar la hora si se quiere.
    @Scheduled(cron = "0 0 9 1 * *")
    public void mandarInfoMensualPistas() {
        // Construimos un resumen de la disponibilidad actual del mapa
        StringBuilder resumenDisponibilidad = new StringBuilder();
        resumenDisponibilidad.append("Horarios destacados para hoy:\n");

        AlmacenDatos.disponibilidad.values().forEach(disp -> {
            resumenDisponibilidad.append("- Pista: ").append(disp.idPista()).append("\n");
            disp.tramosHorariosDisponibles().forEach(tramo -> {
                resumenDisponibilidad.append("  * ")
                        .append(tramo.inicio())
                        .append(" a ")
                        .append(tramo.fin())
                        .append("\n");
            });
        });
        // Enviamos a todos los usuarios registrados
        AlmacenDatos.usuarios.values().forEach(usuario -> {
            if (usuario.activo()) {
                String cuerpoMensaje = "Hola " + usuario.nombre() + ",\n\n" +
                        "Te enviamos la disponibilidad de pistas para iniciar el mes:\n\n" +
                        resumenDisponibilidad.toString() +
                        "\nReserva ya en nuestra App.";
                logger.info("PARA: {}", usuario);
            }
        });
    }
}
