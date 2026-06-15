package io.github.salepartido.api.etc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import io.github.salepartido.api.domain.locales.model.Cancha;
import io.github.salepartido.api.domain.locales.model.ConfiguracionDia;
import io.github.salepartido.api.domain.locales.model.ConfiguracionHorario;
import io.github.salepartido.api.domain.locales.model.Deporte;
import io.github.salepartido.api.domain.locales.model.Local;
import io.github.salepartido.api.domain.locales.model.Turno;
import io.github.salepartido.api.domain.locales.repository.LocalRepository;
import io.github.salepartido.api.domain.locales.repository.TurnoRepository;
import io.github.salepartido.api.domain.locales.service.DisponibilidadService;
import io.github.salepartido.api.domain.locales.controller.dto.DisponibilidadCanchaDTO;
import io.github.salepartido.api.domain.locales.controller.dto.TurnoSlotDTO;

import io.github.salepartido.api.domain.participation.repository.EventoRepository;
import io.github.salepartido.api.domain.participation.model.Evento;
import io.github.salepartido.api.domain.participation.model.EstadoEvento;
import io.github.salepartido.api.domain.participation.model.EstadoParticipacion;
import io.github.salepartido.api.domain.participation.model.Participacion;
import io.github.salepartido.api.domain.participation.model.Usuario;

@ExtendWith(MockitoExtension.class)
class DisponibilidadServiceTest {

    @Mock
    private LocalRepository localRepository;

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private EventoRepository eventoRepository;

    @InjectMocks
    private DisponibilidadService disponibilidadService;

    @Test
    void obtenerDisponibilidadLocal_CuandoLocalNoExiste_LanzaNotFound() {
        UUID localUuid = UUID.randomUUID();
        LocalDate inicio = LocalDate.now();
        LocalDate fin = inicio.plusDays(1);

        when(localRepository.findById(localUuid)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            disponibilidadService.obtenerDisponibilidadLocal(localUuid, inicio, fin);
        });

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Local no encontrado", ex.getReason());
    }

    @Test
    void obtenerDisponibilidadLocal_CuandoLocalNoTieneCanchas_RetornaVacio() {
        UUID localUuid = UUID.randomUUID();
        LocalDate inicio = LocalDate.now();
        LocalDate fin = inicio.plusDays(1);

        Local local = new Local();
        local.setUuid(localUuid);
        local.setCanchas(new ArrayList<>());

        when(localRepository.findById(localUuid)).thenReturn(Optional.of(local));

        List<DisponibilidadCanchaDTO> resultado = disponibilidadService.obtenerDisponibilidadLocal(localUuid, inicio, fin);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerDisponibilidadLocal_GeneraTurnosYDetectaReservasCorrectamente() {
        UUID localUuid = UUID.randomUUID();
        // Lunes 25 de Mayo de 2026
        LocalDate fechaTest = LocalDate.of(2026, 5, 25); 

        Local local = new Local();
        local.setUuid(localUuid);
        local.setNombre("Complejo Test");

        Deporte deporte = new Deporte();
        deporte.setNombre("Fútbol");

        Cancha cancha = new Cancha();
        cancha.setUuid(UUID.randomUUID());
        cancha.setNombre("Cancha 1");
        cancha.setDeporte(deporte);

        // Configuración horaria: Lunes de 08:00 a 10:00, duración de turno 60 mins
        ConfiguracionHorario configHorario = new ConfiguracionHorario();
        configHorario.setActivo(true);
        configHorario.setDuracionTurno(Duration.ofMinutes(60));

        ConfiguracionDia configDia = new ConfiguracionDia();
        configDia.setDiaSemana(DayOfWeek.MONDAY);
        configDia.setHoraInicio(LocalTime.of(8, 0));
        configDia.setHoraFin(LocalTime.of(10, 0));

        configHorario.setConfiguracionesDias(List.of(configDia));
        cancha.setConfiguracionesHorarios(List.of(configHorario));

        local.setCanchas(List.of(cancha));

        // Turno existente para esa cancha el lunes de 09:00 a 10:00
        Turno turno = new Turno();
        turno.setUuid(UUID.randomUUID());
        turno.setCancha(cancha);
        turno.setFecha(fechaTest);
        turno.setHoraInicio(LocalTime.of(9, 0));
        turno.setHoraFin(LocalTime.of(10, 0));

        Usuario organizador = new Usuario();
        organizador.setNombre("Martín");

        Evento e = new Evento();
        e.setOrganizador(organizador);
        e.setEstado(EstadoEvento.DISPONIBLE);
        List<Participacion> participaciones = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Participacion p = new Participacion();
            p.setEstado(EstadoParticipacion.CONFIRMADO);
            participaciones.add(p);
        }
        e.setParticipaciones(participaciones);

        when(localRepository.findById(localUuid)).thenReturn(Optional.of(local));
        when(turnoRepository.findByCanchasAndDateRange(List.of(cancha.getUuid()), fechaTest, fechaTest))
                .thenReturn(List.of(turno));
        when(eventoRepository.findByTurnoUuid(turno.getUuid())).thenReturn(Optional.of(e));

        List<DisponibilidadCanchaDTO> resultado = disponibilidadService.obtenerDisponibilidadLocal(localUuid, fechaTest, fechaTest);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        DisponibilidadCanchaDTO canchaDisp = resultado.get(0);
        assertEquals(cancha.getUuid(), canchaDisp.canchaUuid());
        assertEquals("Cancha 1", canchaDisp.canchaNombre());

        List<TurnoSlotDTO> turnos = canchaDisp.turnos();
        // Debería generar exactamente 2 turnos: 08:00-09:00 y 09:00-10:00
        assertEquals(2, turnos.size());

        // Primer turno: 08:00 - 09:00 (LIBRE)
        TurnoSlotDTO t1 = turnos.get(0);
        assertEquals(fechaTest, t1.fecha());
        assertEquals(LocalTime.of(8, 0), t1.horaInicio());
        assertEquals(LocalTime.of(9, 0), t1.horaFin());
        assertEquals("Cancha 1", t1.espacioNombre());
        assertEquals("Fútbol", t1.deporte());
        assertEquals("LIBRE", t1.estado());
        assertNull(t1.turno());

        // Segundo turno: 09:00 - 10:00 (OCUPADO)
        TurnoSlotDTO t2 = turnos.get(1);
        assertEquals(fechaTest, t2.fecha());
        assertEquals(LocalTime.of(9, 0), t2.horaInicio());
        assertEquals(LocalTime.of(10, 0), t2.horaFin());
        assertEquals("Cancha 1", t2.espacioNombre());
        assertEquals("Fútbol", t2.deporte());
        assertEquals("OCUPADO", t2.estado());
        assertNotNull(t2.turno());
        assertEquals("Martín", t2.turno().nombreOrganizador());
        assertEquals("Fútbol", t2.turno().deporte());
        assertEquals(10, t2.turno().cantidadParticipantesConfirmados());
        assertEquals("DISPONIBLE", t2.turno().estadoEvento());
    }
}
