package io.github.salepartido.api.etc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import io.github.salepartido.api.domain.locales.controller.DisponibilidadController;
import io.github.salepartido.api.domain.locales.controller.dto.DisponibilidadCanchaDTO;
import io.github.salepartido.api.domain.locales.controller.mapper.DisponibilidadMapper;
import io.github.salepartido.api.domain.locales.service.DisponibilidadService;
import io.github.salepartido.api.domain.locales.service.dto.CanchaDisponibilidad;

@ExtendWith(MockitoExtension.class)
class DisponibilidadControllerTest {

        private MockMvc mockMvc;

        @Mock
        private java.time.LocalDate dummyLocalDate; // just to make imports clean, unused

        @Mock
        private DisponibilidadService disponibilidadService;

        @Mock
        private DisponibilidadMapper disponibilidadMapper;

        private DisponibilidadController controller;

        private UUID localUuid;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;

        @BeforeEach
        void setup() {
                controller = new DisponibilidadController(disponibilidadService, disponibilidadMapper, 31);
                mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
                localUuid = UUID.randomUUID();
                fechaInicio = LocalDate.of(2026, 6, 1);
                fechaFin = LocalDate.of(2026, 6, 7);
        }

        @Test
        void getDisponibilidad_ConParametrosValidos_RetornaListaDeDisponibilidad() throws Exception {
                // Arrange
                List<CanchaDisponibilidad> disponibilidad = new ArrayList<>();
                UUID canchaUuid = UUID.randomUUID();
                disponibilidad.add(new CanchaDisponibilidad(
                                canchaUuid,
                                "Cancha 1",
                                null,
                                new ArrayList<>()));

                DisponibilidadCanchaDTO dto = new DisponibilidadCanchaDTO(canchaUuid, "Cancha 1", null, new ArrayList<>());

                when(disponibilidadService.obtenerDisponibilidadLocal(localUuid, fechaInicio, fechaFin))
                                .thenReturn(disponibilidad);
                when(disponibilidadMapper.toDTO(any(CanchaDisponibilidad.class)))
                                .thenReturn(dto);

                // Act & Assert
                mockMvc.perform(get("/locales/{uuid}/disponibilidad", localUuid)
                                .param("fechaInicio", "2026-06-01")
                                .param("fechaFin", "2026-06-07")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$", hasSize(1)));

                verify(disponibilidadService, times(1)).obtenerDisponibilidadLocal(localUuid, fechaInicio, fechaFin);
        }

        @Test
        void getDisponibilidad_ConCanchasSinTurnos_RetornaListaVacia() throws Exception {
                // Arrange
                List<CanchaDisponibilidad> disponibilidad = new ArrayList<>();

                when(disponibilidadService.obtenerDisponibilidadLocal(localUuid, fechaInicio, fechaFin))
                                .thenReturn(disponibilidad);

                // Act & Assert
                mockMvc.perform(get("/locales/{uuid}/disponibilidad", localUuid)
                                .param("fechaInicio", "2026-06-01")
                                .param("fechaFin", "2026-06-07")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$", hasSize(0)));

                verify(disponibilidadService, times(1)).obtenerDisponibilidadLocal(localUuid, fechaInicio, fechaFin);
        }

        @Test
        void getDisponibilidad_SinFechaInicio_RetornaBadRequest() throws Exception {
                // Act & Assert
                mockMvc.perform(get("/locales/{uuid}/disponibilidad", localUuid)
                                .param("fechaFin", "2026-06-07")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest());

                verify(disponibilidadService, never()).obtenerDisponibilidadLocal(any(), any(), any());
        }

        @Test
        void getDisponibilidad_SinFechaFin_RetornaBadRequest() throws Exception {
                // Act & Assert
                mockMvc.perform(get("/locales/{uuid}/disponibilidad", localUuid)
                                .param("fechaInicio", "2026-06-01")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest());

                verify(disponibilidadService, never()).obtenerDisponibilidadLocal(any(), any(), any());
        }

        @Test
        void getDisponibilidad_ConFechaInicioMayorQueFechaFin_RetornaBadRequest() throws Exception {
                // Act & Assert
                mockMvc.perform(get("/locales/{uuid}/disponibilidad", localUuid)
                                .param("fechaInicio", "2026-06-07")
                                .param("fechaFin", "2026-06-01")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest());

                verify(disponibilidadService, never()).obtenerDisponibilidadLocal(any(), any(), any());
        }

        @Test
        void getDisponibilidad_ConRangoMayorA31Dias_RetornaBadRequest() throws Exception {
                // Act & Assert
                mockMvc.perform(get("/locales/{uuid}/disponibilidad", localUuid)
                                .param("fechaInicio", "2026-06-01")
                                .param("fechaFin", "2026-07-05")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest());

                verify(disponibilidadService, never()).obtenerDisponibilidadLocal(any(), any(), any());
        }

        @Test
        void getDisponibilidad_ConLocalNoExistente_RetornaNotFound() throws Exception {
                // Arrange
                when(disponibilidadService.obtenerDisponibilidadLocal(localUuid, fechaInicio, fechaFin))
                                .thenThrow(new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Local no encontrado"));

                // Act & Assert
                mockMvc.perform(get("/locales/{uuid}/disponibilidad", localUuid)
                                .param("fechaInicio", "2026-06-01")
                                .param("fechaFin", "2026-06-07")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());

                verify(disponibilidadService, times(1)).obtenerDisponibilidadLocal(localUuid, fechaInicio, fechaFin);
        }

        @Test
        void getDisponibilidad_ConFechasEnFormatoInvalido_RetornaBadRequest() throws Exception {
                // Act & Assert
                mockMvc.perform(get("/locales/{uuid}/disponibilidad", localUuid)
                                .param("fechaInicio", "01-06-2026")
                                .param("fechaFin", "07-06-2026")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest());

                verify(disponibilidadService, never()).obtenerDisponibilidadLocal(any(), any(), any());
        }

        @Test
        void getDisponibilidad_ConRangoExactamente31Dias_RetornaOk() throws Exception {
                // Arrange
                LocalDate inicio31 = LocalDate.of(2026, 6, 1);
                LocalDate fin31 = LocalDate.of(2026, 7, 2); // Exactamente 31 días
                List<CanchaDisponibilidad> disponibilidad = new ArrayList<>();

                when(disponibilidadService.obtenerDisponibilidadLocal(localUuid, inicio31, fin31))
                                .thenReturn(disponibilidad);

                // Act & Assert
                mockMvc.perform(get("/locales/{uuid}/disponibilidad", localUuid)
                                .param("fechaInicio", "2026-06-01")
                                .param("fechaFin", "2026-07-02")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void getDisponibilidad_ConUUIDInvalido_RetornaBadRequest() throws Exception {
                // Act & Assert
                mockMvc.perform(get("/locales/uuid-invalido/disponibilidad")
                                .param("fechaInicio", "2026-06-01")
                                .param("fechaFin", "2026-06-07")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest());

                verify(disponibilidadService, never()).obtenerDisponibilidadLocal(any(), any(), any());
        }

        @Test
        void getDisponibilidad_ConMultiplesCanchas_RetornaTodasEnLaRespuesta() throws Exception {
                // Arrange
                List<CanchaDisponibilidad> disponibilidad = new ArrayList<>();
                disponibilidad.add(new CanchaDisponibilidad(UUID.randomUUID(), "Cancha 1", null, new ArrayList<>()));
                disponibilidad.add(new CanchaDisponibilidad(UUID.randomUUID(), "Cancha 2", null, new ArrayList<>()));
                disponibilidad.add(new CanchaDisponibilidad(UUID.randomUUID(), "Cancha 3", null, new ArrayList<>()));

                when(disponibilidadService.obtenerDisponibilidadLocal(localUuid, fechaInicio, fechaFin))
                                .thenReturn(disponibilidad);
                when(disponibilidadMapper.toDTO(any(CanchaDisponibilidad.class)))
                                .thenAnswer(inv -> {
                                    CanchaDisponibilidad dom = inv.getArgument(0);
                                    return new DisponibilidadCanchaDTO(dom.canchaUuid(), dom.canchaNombre(), dom.capacidad(), new ArrayList<>());
                                });

                // Act & Assert
                mockMvc.perform(get("/locales/{uuid}/disponibilidad", localUuid)
                                .param("fechaInicio", "2026-06-01")
                                .param("fechaFin", "2026-06-07")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$", hasSize(3)));

                verify(disponibilidadService, times(1)).obtenerDisponibilidadLocal(localUuid, fechaInicio, fechaFin);
        }
}
