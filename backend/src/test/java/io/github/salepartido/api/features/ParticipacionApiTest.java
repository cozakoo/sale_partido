package io.github.salepartido.api.features;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import io.github.salepartido.api.domain.participation.exception.EventoNoDisponibleException;
import io.github.salepartido.api.domain.participation.exception.EventoSinCuposException;
import io.github.salepartido.api.domain.participation.exception.InvitacionYaRespondidaException;
import io.github.salepartido.api.domain.participation.exception.NivelInsuficienteException;
import io.github.salepartido.api.domain.participation.exception.ParticipanteYaRegistradoException;
import io.github.salepartido.api.domain.participation.exception.TipoEventoInvalidoException;
import io.github.salepartido.api.domain.participation.model.EstadoEvento;
import io.github.salepartido.api.domain.participation.model.EstadoParticipacion;
import io.github.salepartido.api.domain.participation.model.Evento;
import io.github.salepartido.api.domain.participation.model.Participacion;
import io.github.salepartido.api.domain.participation.model.TipoEvento;
import io.github.salepartido.api.domain.participation.service.EventoService;
import io.github.salepartido.api.domain.participation.service.ParticipacionService;

/**
 * Tests de integración de la API — E2-H01: Participar de un evento.
 *
 * Verifican el contrato HTTP definido en doc/E2-H01-diseno-api.md
 * actuando como cliente: envían requests HTTP y verifican el status
 * y estructura de las respuestas, sin asumir nada sobre la implementación
 * interna.
 *
 * Estado actual: los endpoints aún no tienen controladores implementados.
 * Estos tests fallan con 404 hasta que se implemente el controlador de la API,
 * y con 500 en los casos de error de dominio (409) hasta que se configure
 * el mapeo de excepciones en GlobalExceptionHandler.
 * Ese es el comportamiento esperado en la fase de diseño (TDD rojo).
 */
@SpringBootTest
@DisplayName("API de Participación — E2-H01")
class ParticipacionApiTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @MockitoBean
    private StringRedisTemplate redisTemplate;

    @MockitoBean
    private ParticipacionService participacionService;

    @MockitoBean
    private EventoService eventoService;

    private static final UUID EVENTO_UUID = UUID.fromString("aaaaaaaa-0000-4000-a000-000000000001");
    private static final UUID USUARIO_UUID = UUID.fromString("aaaaaaaa-0000-4000-a000-000000000002");
    private static final UUID PARTICIPACION_UUID = UUID.fromString("aaaaaaaa-0000-4000-a000-000000000003");

    private Evento eventoDisponible;
    private Participacion participacionConfirmada;
    private Participacion participacionPendiente;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        eventoDisponible = new Evento();
        eventoDisponible.setNombre("Partido de fútbol");
        eventoDisponible.setTipo(TipoEvento.ABIERTO);
        eventoDisponible.setEstado(EstadoEvento.DISPONIBLE);
        eventoDisponible.setCupoMinimo(6);
        eventoDisponible.setCupoMaximo(10);

        participacionConfirmada = new Participacion();
        participacionConfirmada.setEstado(EstadoParticipacion.CONFIRMADO);
        participacionConfirmada.setEsInvitacion(false);
        participacionConfirmada.setFechaEstado(LocalDateTime.now());
        participacionConfirmada.setAsistio(false);

        participacionPendiente = new Participacion();
        participacionPendiente.setEstado(EstadoParticipacion.PENDIENTE);
        participacionPendiente.setEsInvitacion(false);
        participacionPendiente.setFechaEstado(LocalDateTime.now());
        participacionPendiente.setAsistio(false);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /eventos/{uuid}
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /eventos/{uuid} — detalle de un evento")
    class GetEvento {

        @Test
        @DisplayName("Evento existente → 200 OK con body JSON")
        void eventoExistente_retorna200() throws Exception {
            when(eventoService.getEvento(EVENTO_UUID)).thenReturn(eventoDisponible);

            mockMvc.perform(get("/eventos/{uuid}", EVENTO_UUID))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("Evento no existente → 404 Not Found")
        void eventoNoExistente_retorna404() throws Exception {
            when(eventoService.getEvento(EVENTO_UUID))
                    .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));

            mockMvc.perform(get("/eventos/{uuid}", EVENTO_UUID))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("UUID inválido en ruta → 400 Bad Request")
        void uuidMalFormado_retorna400() throws Exception {
            mockMvc.perform(get("/eventos/no-es-uuid"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /eventos — listado de eventos
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /eventos — listado de eventos")
    class GetEventos {

        @Test
        @DisplayName("Obtener listado de eventos → 200 OK")
        void getEventos_retorna200() throws Exception {
            java.util.List<Evento> eventos = java.util.List.of(eventoDisponible);
            when(eventoService.getEventos()).thenReturn(eventos);

            mockMvc.perform(get("/eventos"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /eventos/{uuid}/participaciones/usuario/{usuarioUuid} — consulta de participación activa
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /eventos/{uuid}/participaciones/usuario/{usuarioUuid}")
    class GetParticipacionUsuario {

        @Test
        @DisplayName("Participación activa existente → 200 OK con body JSON")
        void participacionActivaExiste_retorna200() throws Exception {
            when(participacionService.getParticipacionActiva(EVENTO_UUID, USUARIO_UUID))
                    .thenReturn(participacionConfirmada);

            mockMvc.perform(get("/eventos/{uuid}/participaciones/usuario/{usuarioUuid}", EVENTO_UUID, USUARIO_UUID))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("Participación activa inexistente → 404 Not Found")
        void participacionActivaNoExiste_retorna404() throws Exception {
            when(participacionService.getParticipacionActiva(EVENTO_UUID, USUARIO_UUID))
                    .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Participación no encontrada"));

            mockMvc.perform(get("/eventos/{uuid}/participaciones/usuario/{usuarioUuid}", EVENTO_UUID, USUARIO_UUID))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("UUID de evento inválido → 400 Bad Request")
        void uuidEventoInvalido_retorna400() throws Exception {
            mockMvc.perform(get("/eventos/no-es-uuid/participaciones/usuario/{usuarioUuid}", USUARIO_UUID))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("UUID de usuario inválido → 400 Bad Request")
        void uuidUsuarioInvalido_retorna400() throws Exception {
            mockMvc.perform(get("/eventos/{uuid}/participaciones/usuario/no-es-uuid", EVENTO_UUID))
                    .andExpect(status().isBadRequest());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /eventos/{uuid}/participaciones — unirse a evento ABIERTO
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /eventos/{uuid}/participaciones — unirse a evento ABIERTO")
    class Unirse {

        @Test
        @DisplayName("Unión exitosa → 201 Created con estado CONFIRMADO")
        void unionExitosa_retorna201() throws Exception {
            when(participacionService.unirse(EVENTO_UUID, USUARIO_UUID))
                    .thenReturn(participacionConfirmada);

            mockMvc.perform(post("/eventos/{uuid}/participaciones", EVENTO_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyUsuario(USUARIO_UUID)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Evento no encontrado → 404 Not Found")
        void eventoNoExiste_retorna404() throws Exception {
            when(participacionService.unirse(EVENTO_UUID, USUARIO_UUID))
                    .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));

            mockMvc.perform(post("/eventos/{uuid}/participaciones", EVENTO_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyUsuario(USUARIO_UUID)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Usuario no encontrado → 404 Not Found")
        void usuarioNoExiste_retorna404() throws Exception {
            when(participacionService.unirse(EVENTO_UUID, USUARIO_UUID))
                    .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

            mockMvc.perform(post("/eventos/{uuid}/participaciones", EVENTO_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyUsuario(USUARIO_UUID)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Evento no disponible → 409 Conflict")
        void eventoNoDisponible_retorna409() throws Exception {
            when(participacionService.unirse(EVENTO_UUID, USUARIO_UUID))
                    .thenThrow(new EventoNoDisponibleException());

            mockMvc.perform(post("/eventos/{uuid}/participaciones", EVENTO_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyUsuario(USUARIO_UUID)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Tipo de evento inválido (no ABIERTO) → 409 Conflict")
        void tipoEventoInvalido_retorna409() throws Exception {
            when(participacionService.unirse(EVENTO_UUID, USUARIO_UUID))
                    .thenThrow(new TipoEventoInvalidoException("Solo eventos ABIERTOS"));

            mockMvc.perform(post("/eventos/{uuid}/participaciones", EVENTO_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyUsuario(USUARIO_UUID)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Evento sin cupos disponibles → 409 Conflict")
        void sinCuposDisponibles_retorna409() throws Exception {
            when(participacionService.unirse(EVENTO_UUID, USUARIO_UUID))
                    .thenThrow(new EventoSinCuposException());

            mockMvc.perform(post("/eventos/{uuid}/participaciones", EVENTO_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyUsuario(USUARIO_UUID)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Nivel de habilidad insuficiente → 409 Conflict")
        void nivelInsuficiente_retorna409() throws Exception {
            when(participacionService.unirse(EVENTO_UUID, USUARIO_UUID))
                    .thenThrow(new NivelInsuficienteException());

            mockMvc.perform(post("/eventos/{uuid}/participaciones", EVENTO_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyUsuario(USUARIO_UUID)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Participante ya registrado en el evento → 409 Conflict")
        void participanteYaRegistrado_retorna409() throws Exception {
            when(participacionService.unirse(EVENTO_UUID, USUARIO_UUID))
                    .thenThrow(new ParticipanteYaRegistradoException());

            mockMvc.perform(post("/eventos/{uuid}/participaciones", EVENTO_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyUsuario(USUARIO_UUID)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("UUID de evento inválido en ruta → 400 Bad Request")
        void uuidEventoMalFormado_retorna400() throws Exception {
            mockMvc.perform(post("/eventos/no-es-uuid/participaciones")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyUsuario(USUARIO_UUID)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Body sin usuarioUuid → 400 Bad Request")
        void bodySinUsuarioUuid_retorna400() throws Exception {
            mockMvc.perform(post("/eventos/{uuid}/participaciones", EVENTO_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("usuarioUuid con formato inválido → 400 Bad Request")
        void usuarioUuidMalFormado_retorna400() throws Exception {
            mockMvc.perform(post("/eventos/{uuid}/participaciones", EVENTO_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"usuarioUuid\":\"no-es-uuid\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Content-Type ausente → 415 Unsupported Media Type")
        void sinContentType_retorna415() throws Exception {
            mockMvc.perform(post("/eventos/{uuid}/participaciones", EVENTO_UUID)
                            .content(bodyUsuario(USUARIO_UUID)))
                    .andExpect(status().isUnsupportedMediaType());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /eventos/{uuid}/solicitudes — solicitar en evento CON_CONFIRMACION
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /eventos/{uuid}/solicitudes — solicitar en evento CON_CONFIRMACION")
    class SolicitarParticipacion {

        @Test
        @DisplayName("Solicitud exitosa → 201 Created con estado PENDIENTE")
        void solicitudExitosa_retorna201() throws Exception {
            when(participacionService.solicitarParticipacion(EVENTO_UUID, USUARIO_UUID))
                    .thenReturn(participacionPendiente);

            mockMvc.perform(post("/eventos/{uuid}/solicitudes", EVENTO_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyUsuario(USUARIO_UUID)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Evento no encontrado → 404 Not Found")
        void eventoNoExiste_retorna404() throws Exception {
            when(participacionService.solicitarParticipacion(EVENTO_UUID, USUARIO_UUID))
                    .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));

            mockMvc.perform(post("/eventos/{uuid}/solicitudes", EVENTO_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyUsuario(USUARIO_UUID)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Usuario no encontrado → 404 Not Found")
        void usuarioNoExiste_retorna404() throws Exception {
            when(participacionService.solicitarParticipacion(EVENTO_UUID, USUARIO_UUID))
                    .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

            mockMvc.perform(post("/eventos/{uuid}/solicitudes", EVENTO_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyUsuario(USUARIO_UUID)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Tipo de evento inválido (no CON_CONFIRMACION) → 409 Conflict")
        void tipoEventoInvalido_retorna409() throws Exception {
            when(participacionService.solicitarParticipacion(EVENTO_UUID, USUARIO_UUID))
                    .thenThrow(new TipoEventoInvalidoException("Solo eventos CON_CONFIRMACION"));

            mockMvc.perform(post("/eventos/{uuid}/solicitudes", EVENTO_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyUsuario(USUARIO_UUID)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Evento sin cupos → 409 Conflict")
        void sinCupos_retorna409() throws Exception {
            when(participacionService.solicitarParticipacion(EVENTO_UUID, USUARIO_UUID))
                    .thenThrow(new EventoSinCuposException());

            mockMvc.perform(post("/eventos/{uuid}/solicitudes", EVENTO_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyUsuario(USUARIO_UUID)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Nivel insuficiente → 409 Conflict")
        void nivelInsuficiente_retorna409() throws Exception {
            when(participacionService.solicitarParticipacion(EVENTO_UUID, USUARIO_UUID))
                    .thenThrow(new NivelInsuficienteException());

            mockMvc.perform(post("/eventos/{uuid}/solicitudes", EVENTO_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyUsuario(USUARIO_UUID)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Participante ya registrado → 409 Conflict")
        void participanteYaRegistrado_retorna409() throws Exception {
            when(participacionService.solicitarParticipacion(EVENTO_UUID, USUARIO_UUID))
                    .thenThrow(new ParticipanteYaRegistradoException());

            mockMvc.perform(post("/eventos/{uuid}/solicitudes", EVENTO_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyUsuario(USUARIO_UUID)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Body sin usuarioUuid → 400 Bad Request")
        void bodySinUsuarioUuid_retorna400() throws Exception {
            mockMvc.perform(post("/eventos/{uuid}/solicitudes", EVENTO_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PATCH /participaciones/{uuid} — responder invitación
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("PATCH /participaciones/{uuid} — responder invitación")
    class ResponderInvitacion {

        @Test
        @DisplayName("Aceptar invitación → 200 OK con estado CONFIRMADO")
        void aceptarInvitacion_retorna200ConConfirmado() throws Exception {
            Participacion confirmada = buildInvitacion(EstadoParticipacion.CONFIRMADO);
            when(participacionService.aceptarInvitacion(PARTICIPACION_UUID)).thenReturn(confirmada);

            mockMvc.perform(patch("/participaciones/{uuid}", PARTICIPACION_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"estado\":\"CONFIRMADO\"}"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Rechazar invitación → 200 OK con estado RECHAZADO")
        void rechazarInvitacion_retorna200ConRechazado() throws Exception {
            Participacion rechazada = buildInvitacion(EstadoParticipacion.RECHAZADO);
            when(participacionService.rechazarInvitacion(PARTICIPACION_UUID)).thenReturn(rechazada);

            mockMvc.perform(patch("/participaciones/{uuid}", PARTICIPACION_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"estado\":\"RECHAZADO\"}"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Participación no encontrada → 404 Not Found")
        void participacionNoExiste_retorna404() throws Exception {
            when(participacionService.aceptarInvitacion(PARTICIPACION_UUID))
                    .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Participación no encontrada"));

            mockMvc.perform(patch("/participaciones/{uuid}", PARTICIPACION_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"estado\":\"CONFIRMADO\"}"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("No es una invitación → 409 Conflict")
        void noEsInvitacion_retorna409() throws Exception {
            when(participacionService.aceptarInvitacion(PARTICIPACION_UUID))
                    .thenThrow(new TipoEventoInvalidoException("Solo aplica a invitaciones"));

            mockMvc.perform(patch("/participaciones/{uuid}", PARTICIPACION_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"estado\":\"CONFIRMADO\"}"))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Invitación ya respondida → 409 Conflict")
        void invitacionYaRespondida_retorna409() throws Exception {
            when(participacionService.aceptarInvitacion(PARTICIPACION_UUID))
                    .thenThrow(new InvitacionYaRespondidaException());

            mockMvc.perform(patch("/participaciones/{uuid}", PARTICIPACION_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"estado\":\"CONFIRMADO\"}"))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Estado inválido (ej: CANCELADO) → 400 Bad Request")
        void estadoInvalido_retorna400() throws Exception {
            mockMvc.perform(patch("/participaciones/{uuid}", PARTICIPACION_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"estado\":\"CANCELADO\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Body sin campo estado → 400 Bad Request")
        void bodySinEstado_retorna400() throws Exception {
            mockMvc.perform(patch("/participaciones/{uuid}", PARTICIPACION_UUID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("UUID de participación inválido → 400 Bad Request")
        void uuidMalFormado_retorna400() throws Exception {
            mockMvc.perform(patch("/participaciones/no-es-uuid")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"estado\":\"CONFIRMADO\"}"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private static String bodyUsuario(UUID usuarioUuid) {
        return "{\"usuarioUuid\":\"" + usuarioUuid + "\"}";
    }

    private static Participacion buildInvitacion(EstadoParticipacion estado) {
        Participacion p = new Participacion();
        p.setEstado(estado);
        p.setEsInvitacion(true);
        p.setFechaEstado(LocalDateTime.now());
        p.setAsistio(false);
        return p;
    }

}
