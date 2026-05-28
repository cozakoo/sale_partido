package io.github.salepartido.api.integration.domain.reservations.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import io.github.salepartido.api.domain.locales.model.Cancha;
import io.github.salepartido.api.domain.locales.model.ConfiguracionDia;
import io.github.salepartido.api.domain.locales.model.ConfiguracionHorario;
import io.github.salepartido.api.domain.locales.model.Deporte;
import io.github.salepartido.api.domain.locales.model.Local;
import io.github.salepartido.api.domain.locales.repository.LocalRepository;
import io.github.salepartido.api.domain.reservations.controller.dto.DisponibilidadCanchaDTO;
import io.github.salepartido.api.domain.reservations.controller.dto.TurnoDTO;
import io.github.salepartido.api.domain.reservations.model.Reserva;
import io.github.salepartido.api.domain.reservations.repository.ReservaRepository;
import io.github.salepartido.api.domain.reservations.service.DisponibilidadService;

@SpringBootTest
@Transactional
class DisponibilidadServiceIntegrationTest {

    @MockitoBean
    StringRedisTemplate redisTemplate;

    @Autowired
    private DisponibilidadService disponibilidadService;

    @Autowired
    private LocalRepository localRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Test
    void obtenerDisponibilidadLocal_ConBaseDeDatosReal_GeneraTurnosCorrectamente() {
        LocalDate fechaTest = LocalDate.of(2026, 6, 1);

        Local local = new Local();
        local.setNombre("Complejo Test");
        local.setDireccion("Av. Test 123");

        var deporte = new Deporte();
        deporte.setNombre("Fútbol");

        Cancha cancha = new Cancha();
        cancha.setNombre("Cancha 1");
        cancha.setDeporte(deporte);

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

        Local savedLocal = localRepository.save(local);

        UUID canchaUuid = savedLocal.getCanchas().get(0).getUuid();
        Cancha savedCancha = savedLocal.getCanchas().get(0);

        Reserva reserva = new Reserva();
        reserva.setCancha(savedCancha);
        reserva.setFecha(fechaTest);
        reserva.setHoraInicio(LocalTime.of(9, 0));
        reserva.setHoraFin(LocalTime.of(10, 0));
        reserva.setNombreOrganizador("Martín");
        reserva.setDeporte("Fútbol");
        reserva.setCantidadParticipantesConfirmados(10);
        reserva.setEstadoEvento("CONFIRMADO");

        reservaRepository.save(reserva);

        List<DisponibilidadCanchaDTO> resultado = disponibilidadService.obtenerDisponibilidadLocal(
                savedLocal.getUuid(), fechaTest, fechaTest);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        DisponibilidadCanchaDTO canchaDisp = resultado.get(0);
        assertEquals(canchaUuid, canchaDisp.canchaUuid());
        assertEquals("Cancha 1", canchaDisp.canchaNombre());

        List<TurnoDTO> turnos = canchaDisp.turnos();
        assertEquals(2, turnos.size());

        TurnoDTO t1 = turnos.get(0);
        assertEquals(fechaTest, t1.fecha());
        assertEquals(LocalTime.of(8, 0), t1.horaInicio());
        assertEquals(LocalTime.of(9, 0), t1.horaFin());
        assertEquals("Cancha 1", t1.espacioNombre());
        assertNull(t1.deporte());
        assertEquals("LIBRE", t1.estado());
        assertNull(t1.reserva());

        TurnoDTO t2 = turnos.get(1);
        assertEquals(fechaTest, t2.fecha());
        assertEquals(LocalTime.of(9, 0), t2.horaInicio());
        assertEquals(LocalTime.of(10, 0), t2.horaFin());
        assertEquals("Cancha 1", t2.espacioNombre());
        assertEquals("Fútbol", t2.deporte());
        assertEquals("OCUPADO", t2.estado());
        assertNotNull(t2.reserva());
        assertEquals("Martín", t2.reserva().nombreOrganizador());
        assertEquals("Fútbol", t2.reserva().deporte());
        assertEquals(10, t2.reserva().cantidadParticipantesConfirmados());
        assertEquals("CONFIRMADO", t2.reserva().estadoEvento());
    }

    @Test
    void obtenerDisponibilidadLocal_LocalSinCanchas_RetornaVacio() {
        Local local = new Local();
        local.setNombre("Local Vacío");
        local.setDireccion("Av. Test 456");

        Local saved = localRepository.save(local);

        List<DisponibilidadCanchaDTO> resultado = disponibilidadService.obtenerDisponibilidadLocal(
                saved.getUuid(), LocalDate.now(), LocalDate.now().plusDays(1));

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerDisponibilidadLocal_CanchaSinConfiguracion_RetornaDisponibilidadSinTurnos() {
        Local local = new Local();
        local.setNombre("Local sin config");
        local.setDireccion("Av. Test 789");

        var deporte = new Deporte();
        deporte.setNombre("Fútbol");

        Cancha cancha = new Cancha();
        cancha.setNombre("Cancha sin horario");
        cancha.setDeporte(deporte);

        local.setCanchas(List.of(cancha));
        Local saved = localRepository.save(local);

        List<DisponibilidadCanchaDTO> resultado = disponibilidadService.obtenerDisponibilidadLocal(
                saved.getUuid(), LocalDate.now(), LocalDate.now().plusDays(1));

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).turnos().isEmpty());
    }
}
