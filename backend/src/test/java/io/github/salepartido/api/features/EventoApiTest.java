package io.github.salepartido.api.features;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import io.github.salepartido.api.domain.locales.model.Cancha;
import io.github.salepartido.api.domain.locales.model.Local;
import io.github.salepartido.api.domain.locales.model.Turno;
import io.github.salepartido.api.domain.locales.service.LocalService;
import io.github.salepartido.api.domain.participation.exception.CupoMaximoSuperaCapacidadException;
import io.github.salepartido.api.domain.participation.exception.CupoMinimoInvalidoException;
import io.github.salepartido.api.domain.participation.exception.CupoMinimoMayorMaximoException;
import io.github.salepartido.api.domain.participation.exception.TiempoCancelacionInvalidoException;
import io.github.salepartido.api.domain.participation.model.Evento;
import io.github.salepartido.api.domain.participation.model.TipoEvento;
import io.github.salepartido.api.domain.participation.service.EventoService;

@SpringBootTest
@DisplayName("API de Eventos — E3-H01")
class EventoApiTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @MockitoBean
    private EventoService eventoService;

    @MockitoBean
    private LocalService localService;

    private static final UUID TURNO_UUID = UUID.fromString("bbbbbbbb-0000-4000-a000-000000000010");
    private static final UUID ORGANIZADOR_UUID = UUID.fromString("bbbbbbbb-0000-4000-a000-000000000011");
    private static final UUID NIVEL_UUID = UUID.fromString("bbbbbbbb-0000-4000-a000-000000000012");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /eventos — crear evento
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /eventos — crear evento")
    class CrearEvento {

        @Test
        @DisplayName("Creación exitosa → 201 Created con body JSON")
        void crearEvento_exitoso_retorna201() throws Exception {
            Evento e = new Evento();
            e.setNombre("Partido de futbol 5");
            e.setTipo(TipoEvento.ABIERTO);
            Turno t = new Turno();
            Cancha c = new Cancha();
            c.setUuid(UUID.randomUUID());
            c.setNombre("Cancha A");
            t.setCancha(c);
            t.setUuid(TURNO_UUID);
            t.setFecha(LocalDate.now().plusDays(1));
            t.setHoraInicio(LocalTime.of(18,0));
            t.setHoraFin(LocalTime.of(19,0));
            e.setTurno(t);

            when(eventoService.crearEvento(org.mockito.ArgumentMatchers.any())).thenReturn(e);
            when(localService.buscarLocalPorCanchaUuid(org.mockito.ArgumentMatchers.any()))
                    .thenReturn(Optional.of(new Local()));

            String body = "{\"turno\":{\"fecha\":\"2026-06-02\",\"horaInicio\":\"18:00:00\",\"horaFin\":\"19:00:00\",\"canchaUuid\":\"" + TURNO_UUID + "\"},\"organizadorUuid\":\"" + ORGANIZADOR_UUID + "\",\"nombre\":\"Partido de futbol 5\",\"tipo\":\"ABIERTO\",\"cupoMinimo\":6,\"cupoMaximo\":10,\"limiteCancelacionParticipacion\":60,\"nivelRequeridoUuid\":null}";

            mockMvc.perform(post("/eventos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("Turno no encontrado → 404 Not Found")
        void turnoNoEncontrado_retorna404() throws Exception {
            when(eventoService.crearEvento(org.mockito.ArgumentMatchers.any()))
                    .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "El turno no existe"));

            String body = "{\"turno\":{\"fecha\":\"2026-06-02\",\"horaInicio\":\"18:00:00\",\"horaFin\":\"19:00:00\",\"canchaUuid\":\"" + TURNO_UUID + "\"},\"organizadorUuid\":\"" + ORGANIZADOR_UUID + "\",\"nombre\":\"Partido\",\"cupoMinimo\":6,\"cupoMaximo\":10}";

            mockMvc.perform(post("/eventos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Organizador no encontrado → 404 Not Found")
        void organizadorNoEncontrado_retorna404() throws Exception {
            when(eventoService.crearEvento(org.mockito.ArgumentMatchers.any()))
                    .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe"));

            String body = "{\"turno\":{\"fecha\":\"2026-06-02\",\"horaInicio\":\"18:00:00\",\"horaFin\":\"19:00:00\",\"canchaUuid\":\"" + TURNO_UUID + "\"},\"organizadorUuid\":\"" + ORGANIZADOR_UUID + "\",\"nombre\":\"Partido\",\"cupoMinimo\":6,\"cupoMaximo\":10}";

            mockMvc.perform(post("/eventos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Nivel requerido inexistente → 404 Not Found")
        void nivelNoEncontrado_retorna404() throws Exception {
            when(eventoService.crearEvento(org.mockito.ArgumentMatchers.any()))
                    .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Nivel de deporte no encontrado"));

            String body = "{\"turno\":{\"fecha\":\"2026-06-02\",\"horaInicio\":\"18:00:00\",\"horaFin\":\"19:00:00\",\"canchaUuid\":\"" + TURNO_UUID + "\"},\"organizadorUuid\":\"" + ORGANIZADOR_UUID + "\",\"nombre\":\"Partido\",\"cupoMinimo\":6,\"cupoMaximo\":10,\"nivelRequeridoUuid\":\"" + NIVEL_UUID + "\"}";

            mockMvc.perform(post("/eventos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Cupo mínimo inválido (<=0) → 400 Bad Request")
        void cupoMinimoInvalido_retorna400() throws Exception {
            when(eventoService.crearEvento(org.mockito.ArgumentMatchers.any()))
                    .thenThrow(new CupoMinimoInvalidoException());

            String body = "{\"turno\":{\"fecha\":\"2026-06-02\",\"horaInicio\":\"18:00:00\",\"horaFin\":\"19:00:00\",\"canchaUuid\":\"" + TURNO_UUID + "\"},\"organizadorUuid\":\"" + ORGANIZADOR_UUID + "\",\"nombre\":\"Partido\",\"cupoMinimo\":0,\"cupoMaximo\":10}";

            mockMvc.perform(post("/eventos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Cupo mínimo mayor que máximo → 400 Bad Request")
        void cupoMinMayorMax_retorna400() throws Exception {
            when(eventoService.crearEvento(org.mockito.ArgumentMatchers.any()))
                    .thenThrow(new CupoMinimoMayorMaximoException());

            String body = "{\"turno\":{\"fecha\":\"2026-06-02\",\"horaInicio\":\"18:00:00\",\"horaFin\":\"19:00:00\",\"canchaUuid\":\"" + TURNO_UUID + "\"},\"organizadorUuid\":\"" + ORGANIZADOR_UUID + "\",\"nombre\":\"Partido\",\"cupoMinimo\":12,\"cupoMaximo\":10}";

            mockMvc.perform(post("/eventos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Cupo máximo supera capacidad de la cancha → 409 Conflict")
        void cupoMaxSuperaCapacidad_retorna409() throws Exception {
            when(eventoService.crearEvento(org.mockito.ArgumentMatchers.any()))
                    .thenThrow(new CupoMaximoSuperaCapacidadException());

            String body = "{\"turno\":{\"fecha\":\"2026-06-02\",\"horaInicio\":\"18:00:00\",\"horaFin\":\"19:00:00\",\"canchaUuid\":\"" + TURNO_UUID + "\"},\"organizadorUuid\":\"" + ORGANIZADOR_UUID + "\",\"nombre\":\"Partido\",\"cupoMinimo\":6,\"cupoMaximo\":20}";

            mockMvc.perform(post("/eventos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Límite de cancelación inválido → 400 Bad Request")
        void limiteCancelacionInvalido_retorna400() throws Exception {
            when(eventoService.crearEvento(org.mockito.ArgumentMatchers.any()))
                    .thenThrow(new TiempoCancelacionInvalidoException());

            String body = "{\"turno\":{\"fecha\":\"2026-06-02\",\"horaInicio\":\"18:00:00\",\"horaFin\":\"19:00:00\",\"canchaUuid\":\"" + TURNO_UUID + "\"},\"organizadorUuid\":\"" + ORGANIZADOR_UUID + "\",\"nombre\":\"Partido\",\"cupoMinimo\":6,\"cupoMaximo\":10,\"limiteCancelacionParticipacion\":30}";

            mockMvc.perform(post("/eventos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Content-Type ausente → 415 Unsupported Media Type")
        void sinContentType_retorna415() throws Exception {
            String body = "{\"turno\":{\"fecha\":\"2026-06-02\",\"horaInicio\":\"18:00:00\",\"horaFin\":\"19:00:00\",\"canchaUuid\":\"" + TURNO_UUID + "\"},\"organizadorUuid\":\"" + ORGANIZADOR_UUID + "\",\"nombre\":\"Partido\",\"cupoMinimo\":6,\"cupoMaximo\":10}";

            mockMvc.perform(post("/eventos")
                            .content(body))
                    .andExpect(status().isUnsupportedMediaType());
        }

        @Test
        @DisplayName("Body sin campos requeridos → 400 Bad Request")
        void bodySinCampos_retorna400() throws Exception {
            mockMvc.perform(post("/eventos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

}
