package io.github.salepartido.api.features;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
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
import io.github.salepartido.api.domain.participation.model.Evento;
import io.github.salepartido.api.domain.participation.model.Usuario;
import io.github.salepartido.api.domain.participation.model.EstadoEvento;
import io.github.salepartido.api.domain.participation.model.Participacion;
import io.github.salepartido.api.domain.participation.model.EstadoParticipacion;
import io.github.salepartido.api.domain.participation.model.Rol;
import io.github.salepartido.api.domain.participation.repository.EventoRepository;
import io.github.salepartido.api.domain.participation.repository.UsuarioRepository;
import java.util.ArrayList;
import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class CalendarioDisponibilidadTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StringRedisTemplate redisTemplate;

    @Autowired
    private LocalRepository localRepository;

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Obtener disponibilidad con base de datos real - Genera turnos correctamente")
    void obtenerDisponibilidadLocal_ConBaseDeDatosReal_GeneraTurnosCorrectamente() throws Exception {
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
        evento.setTipo(io.github.salepartido.api.domain.participation.model.TipoEvento.CERRADO);
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

        mockMvc.perform(get("/locales/{uuid}/disponibilidad", savedLocal.getUuid())
                .param("fechaInicio", "2026-06-01")
                .param("fechaFin", "2026-06-01")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].canchaUuid").value(canchaUuid.toString()))
        .andExpect(jsonPath("$[0].canchaNombre").value("Cancha 1"))
        .andExpect(jsonPath("$[0].turnos").isArray())
        .andExpect(jsonPath("$[0].turnos", hasSize(2)))
        // primer turno: libre
        .andExpect(jsonPath("$[0].turnos[0].fecha").value("2026-06-01"))
        .andExpect(jsonPath("$[0].turnos[0].horaInicio").value("08:00:00"))
        .andExpect(jsonPath("$[0].turnos[0].horaFin").value("09:00:00"))
        .andExpect(jsonPath("$[0].turnos[0].estado").value("LIBRE"))
        .andExpect(jsonPath("$[0].turnos[0].turno").isEmpty())
        // segundo turno: ocupado
        .andExpect(jsonPath("$[0].turnos[1].fecha").value("2026-06-01"))
        .andExpect(jsonPath("$[0].turnos[1].horaInicio").value("09:00:00"))
        .andExpect(jsonPath("$[0].turnos[1].horaFin").value("10:00:00"))
        .andExpect(jsonPath("$[0].turnos[1].estado").value("OCUPADO"))
        .andExpect(jsonPath("$[0].turnos[1].turno.nombreOrganizador").value("Martín"))
        .andExpect(jsonPath("$[0].turnos[1].turno.deporte").value("Fútbol"))
        .andExpect(jsonPath("$[0].turnos[1].turno.cantidadParticipantesConfirmados").value(10))
        .andExpect(jsonPath("$[0].turnos[1].turno.estadoEvento").value("DISPONIBLE"));
    }

    @Test
    @DisplayName("Local sin canchas retorna vacio")
    void obtenerDisponibilidadLocal_LocalSinCanchas_RetornaVacio() throws Exception {
        Local local = new Local();
        local.setNombre("Local Vacío");
        setUbicacionHelper(local, "Av. Test 456");

        Local saved = localRepository.save(local);

        mockMvc.perform(get("/locales/{uuid}/disponibilidad", saved.getUuid())
                .param("fechaInicio", LocalDate.now().toString())
                .param("fechaFin", LocalDate.now().plusDays(1).toString())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Cancha sin configuracion retorna disponibilidad sin turnos")
    void obtenerDisponibilidadLocal_CanchaSinConfiguracion_RetornaDisponibilidadSinTurnos() throws Exception {
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

        mockMvc.perform(get("/locales/{uuid}/disponibilidad", saved.getUuid())
                .param("fechaInicio", LocalDate.now().toString())
                .param("fechaFin", LocalDate.now().plusDays(1).toString())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].canchaNombre").value("Cancha sin horario"))
        .andExpect(jsonPath("$[0].turnos").isArray())
        .andExpect(jsonPath("$[0].turnos", hasSize(0)));
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
