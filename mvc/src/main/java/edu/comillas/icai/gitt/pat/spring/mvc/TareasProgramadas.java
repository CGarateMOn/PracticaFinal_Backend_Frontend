package edu.comillas.icai.gitt.pat.spring.mvc;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import edu.comillas.icai.gitt.pat.spring.mvc.service.PistaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.ReservaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.UsuarioService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TareasProgramadas {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // Inyectamos los SERVICIOS en lugar de los Repositorios
    private final ReservaService reservaService;
    private final UsuarioService usuarioService;
    private final PistaService pistaService;

    public TareasProgramadas(ReservaService reservaService,
                             UsuarioService usuarioService,
                             PistaService pistaService) {
        this.reservaService = reservaService;
        this.usuarioService = usuarioService;
        this.pistaService = pistaService;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void mandarRecordatorioReservas() {
        logger.info("Activando reloj: Iniciando proceso de recordatorios de reservas...");

        // ¡La tarea programada solo da la orden! El servicio hace el trabajo sucio.
        reservaService.enviarRecordatoriosDiarios();
    }

        /* 💡 INCLUSO MEJOR: Podrías llevarte TODO este bloque "forEach" adentro de
        un método en ReservaService llamado "procesarRecordatoriosDiarios(hoy)",
        y que esta clase simplemente haga:

        reservaService.enviarRecordatoriosDiarios(hoy);
        */


    @Scheduled(cron = "0 0 9 1 * *")
    public void mandarInfoMensualPistas() {
        logger.info("Activando reloj: Iniciando envío mensual de disponibilidad de pistas...");

        // ¡Solo damos la orden al servicio!
        pistaService.enviarInfoMensualDisponibilidad();
    }
}