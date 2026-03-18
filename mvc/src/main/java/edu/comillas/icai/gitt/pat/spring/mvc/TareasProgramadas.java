package edu.comillas.icai.gitt.pat.spring.mvc;

import edu.comillas.icai.gitt.pat.spring.mvc.service.PistaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.ReservaService;
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
    private final PistaService pistaService;

    public TareasProgramadas(ReservaService reservaService,
                             PistaService pistaService) {
        this.reservaService = reservaService;
        this.pistaService = pistaService;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void mandarRecordatorioReservas() {
        logger.info("Activando reloj: Iniciando proceso de recordatorios de reservas...");

        // La tarea programada da la orden. El servicio ejecuta la lógica
        reservaService.enviarRecordatoriosDiarios();
    }

    @Scheduled(cron = "0 0 9 1 * *")
    public void mandarInfoMensualPistas() {
        logger.info("Activando reloj: Iniciando envío mensual de disponibilidad de pistas...");

        // Damos la orden al servicio
        pistaService.enviarInfoMensualDisponibilidad();
    }
}