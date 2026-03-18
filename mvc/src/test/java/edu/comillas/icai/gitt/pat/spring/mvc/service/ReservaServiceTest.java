package edu.comillas.icai.gitt.pat.spring.mvc.service;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.EstadoReserva;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Pista;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Reserva;
import edu.comillas.icai.gitt.pat.spring.mvc.records.Disponibilidad;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoPistas;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoReserva;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private RepoPistas pistaRepo;

    @Mock
    private RepoReserva reservaRepo;

    @InjectMocks
    private ReservaService reservaService;

    @Test
    void testObtenerDisponibilidadPista_TodoElDiaLibre() {
        // Arrange
        Long idPista = 1L;
        LocalDate fecha = LocalDate.now();

        Pista pistaMock = new Pista();
        pistaMock.setIdPista(idPista);
        pistaMock.setActiva(true);

        when(pistaRepo.findById(idPista)).thenReturn(Optional.of(pistaMock));
        when(reservaRepo.findByPistaAndFechaReservaAndEstado(pistaMock, fecha, EstadoReserva.ACTIVA))
                .thenReturn(new ArrayList<>());

        // Act
        Disponibilidad resultado = reservaService.obtenerDisponibilidadPista(idPista, fecha);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.tramosHorariosDisponibles().size(), "Debería haber solo 1 hueco gigante");

        assertEquals(LocalTime.of(9, 0), resultado.tramosHorariosDisponibles().get(0).inicio());
        assertEquals(LocalTime.of(22, 0), resultado.tramosHorariosDisponibles().get(0).fin());
    }

    @Test
    void testObtenerDisponibilidadPista_ConUnaReservaEnMedio() {
        // Arrange
        Long idPista = 1L;
        LocalDate fecha = LocalDate.now();

        Pista pistaMock = new Pista();
        pistaMock.setIdPista(idPista);
        pistaMock.setActiva(true);

        Reserva reserva = new Reserva();
        reserva.setHoraInicio(LocalTime.of(10, 0));
        reserva.setDuracionMinutos(90); // 90 minutos = 1 hora y media (hasta las 11:30)

        when(pistaRepo.findById(idPista)).thenReturn(Optional.of(pistaMock));
        when(reservaRepo.findByPistaAndFechaReservaAndEstado(pistaMock, fecha, EstadoReserva.ACTIVA))
                .thenReturn(List.of(reserva));

        // Act
        Disponibilidad resultado = reservaService.obtenerDisponibilidadPista(idPista, fecha);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.tramosHorariosDisponibles().size(), "Debería haber 2 huecos libres separados por la reserva");

        // Hueco 1
        assertEquals(LocalTime.of(9, 0), resultado.tramosHorariosDisponibles().get(0).inicio());
        assertEquals(LocalTime.of(10, 0), resultado.tramosHorariosDisponibles().get(0).fin());

        // Hueco 2
        assertEquals(LocalTime.of(11, 30), resultado.tramosHorariosDisponibles().get(1).inicio());
        assertEquals(LocalTime.of(22, 0), resultado.tramosHorariosDisponibles().get(1).fin());
    }

    @Test
    void testObtenerDisponibilidadPista_FallaSiPistaInactiva() {
        // Arrange
        Long idPista = 1L;
        LocalDate fecha = LocalDate.now();

        Pista pistaInactiva = new Pista();
        pistaInactiva.setIdPista(idPista);
        pistaInactiva.setActiva(false);

        when(pistaRepo.findById(idPista)).thenReturn(Optional.of(pistaInactiva));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            reservaService.obtenerDisponibilidadPista(idPista, fecha);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("La pista no está activa", exception.getReason());

        verify(reservaRepo, never()).findByPistaAndFechaReservaAndEstado(any(), any(), any());
    }
}