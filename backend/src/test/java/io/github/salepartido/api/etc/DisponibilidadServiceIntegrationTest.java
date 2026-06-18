package io.github.salepartido.api.etc;

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
import io.github.salepartido.api.domain.locales.model.Localidad;
import io.github.salepartido.api.domain.locales.model.Ubicacion;
import io.github.salepartido.api.domain.locales.model.Turno;
import io.github.salepartido.api.domain.locales.repository.LocalRepository;
import io.github.salepartido.api.domain.locales.repository.TurnoRepository;
import io.github.salepartido.api.domain.locales.service.DisponibilidadService;
import io.github.salepartido.api.domain.eventos.model.EstadoEvento;
import io.github.salepartido.api.domain.eventos.model.EstadoParticipacion;
import io.github.salepartido.api.domain.eventos.model.Evento;
import io.github.salepartido.api.domain.eventos.model.Participacion;
import io.github.salepartido.api.domain.eventos.model.Rol;
import io.github.salepartido.api.domain.eventos.model.Usuario;
import io.github.salepartido.api.domain.eventos.repository.EventoRepository;
import io.github.salepartido.api.domain.eventos.repository.UsuarioRepository;
import io.github.salepartido.api.domain.locales.service.dto.CanchaDisponibilidad;
import io.github.salepartido.api.domain.locales.service.dto.TurnoSlot;

import java.util.ArrayList;
import java.time.LocalDateTime;

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
    private TurnoRepository turnoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void obtenerDisponibilidadLocal_ConBaseDeDatosReal_GeneraTurnosCorrectamente() {
        LocalDate fechaTest = LocalDate.of(2026, 6, 1);

        Local local = new Local();
        local.setNombre("Complejo Test");
        setUbicacionHelper(local, "Av. Test 123");

        var deporte = new Deporte();
        deporte.setNombre("Fútbol");

        Cancha cancha = new Cancha();
        cancha.setNombre("Cancha 1");
        cancha.setDeporte(deporte);
        cancha.setCapacidad(10);
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

        Turno turno = new Turno();
        turno.setCancha(savedCancha);
        turno.setFecha(fechaTest);
        turno.setHoraInicio(LocalTime.of(9, 0));
        turno.setHoraFin(LocalTime.of(10, 0));
        turno = turnoRepository.save(turno);

        Usuario organizador = new Usuario();
        organizador.setNombre("Martín");
        organizador.setRol(Rol.DEPORTISTA);
        organizador = usuarioRepository.save(organizador);

        Evento evento = new Evento();
        evento.setNombre("Partido de Fútbol");
        evento.setTipo(io.github.salepartido.api.domain.eventos.model.TipoEvento.CERRADO);
        evento.setCupoMinimo(2);
        evento.setCupoMaximo(10);
        evento.setEstado(EstadoEvento.DISPONIBLE);
        evento.setTurno(turno);
        evento.setOrganizador(organizador);

        List<Participacion> participaciones = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Participacion p = new Participacion();
            p.setEstado(EstadoParticipacion.CONFIRMADO);
            p.setEsInvitacion(false);
            p.setAsistio(false);
            p.setFechaEstado(LocalDateTime.now());
            p.setParticipante(organizador);
            participaciones.add(p);
        }
        evento.setParticipaciones(participaciones);
        eventoRepository.save(evento);

        List<CanchaDisponibilidad> resultado = disponibilidadService.obtenerDisponibilidadLocal(
                savedLocal.getUuid(), fechaTest, fechaTest);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        CanchaDisponibilidad canchaDisp = resultado.get(0);
        assertEquals(canchaUuid, canchaDisp.canchaUuid());
        assertEquals("Cancha 1", canchaDisp.canchaNombre());

        List<TurnoSlot> turnos = canchaDisp.slots();
        assertEquals(2, turnos.size());

        TurnoSlot t1 = turnos.get(0);
        assertEquals(fechaTest, t1.fecha());
        assertEquals(LocalTime.of(8, 0), t1.horaInicio());
        assertEquals(LocalTime.of(9, 0), t1.horaFin());
        assertEquals("Cancha 1", t1.canchaNombre());
        assertEquals("Fútbol", t1.deporte());
        assertEquals("LIBRE", t1.estado());
        assertNull(t1.turno());

        TurnoSlot t2 = turnos.get(1);
        assertEquals(fechaTest, t2.fecha());
        assertEquals(LocalTime.of(9, 0), t2.horaInicio());
        assertEquals(LocalTime.of(10, 0), t2.horaFin());
        assertEquals("Cancha 1", t2.canchaNombre());
        assertEquals("Fútbol", t2.deporte());
        assertEquals("OCUPADO", t2.estado());
        assertNotNull(t2.turno());
        assertEquals("Martín", t2.turno().organizadorNombre());
        assertEquals("Fútbol", t2.turno().deporte());
        assertEquals(10, t2.turno().cantidadConfirmados());
        assertEquals("DISPONIBLE", t2.turno().estadoEvento());
    }

    @Test
    void obtenerDisponibilidadLocal_LocalSinCanchas_RetornaVacio() {
        Local local = new Local();
        local.setNombre("Local Vacío");
        setUbicacionHelper(local, "Av. Test 456");

        Local saved = localRepository.save(local);

        List<CanchaDisponibilidad> resultado = disponibilidadService.obtenerDisponibilidadLocal(
                saved.getUuid(), LocalDate.now(), LocalDate.now().plusDays(1));

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerDisponibilidadLocal_CanchaSinConfiguracion_RetornaDisponibilidadSinTurnos() {
        Local local = new Local();
        local.setNombre("Local sin config");
        setUbicacionHelper(local, "Av. Test 789");

        var deporte = new Deporte();
        deporte.setNombre("Fútbol");

        Cancha cancha = new Cancha();
        cancha.setNombre("Cancha sin horario");
        cancha.setDeporte(deporte);
        cancha.setCapacidad(10);    
        local.setCanchas(List.of(cancha));
        Local saved = localRepository.save(local);

        List<CanchaDisponibilidad> resultado = disponibilidadService.obtenerDisponibilidadLocal(
                saved.getUuid(), LocalDate.now(), LocalDate.now().plusDays(1));

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).slots().isEmpty());
    }

    private void setUbicacionHelper(Local local, String direccion) {
        Localidad localidad = new Localidad();
        localidad.setNombre("Puerto Madryn");
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setLocalidad(localidad);
        ubicacion.setDireccion(direccion);
        local.setUbicacion(ubicacion);
    }
}
