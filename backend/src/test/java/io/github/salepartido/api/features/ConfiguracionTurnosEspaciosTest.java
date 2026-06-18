package io.github.salepartido.api.features;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import io.github.salepartido.api.domain.locales.model.Cancha;
import io.github.salepartido.api.domain.locales.model.Deporte;
import io.github.salepartido.api.domain.locales.model.Local;
import io.github.salepartido.api.domain.locales.model.Localidad;
import io.github.salepartido.api.domain.locales.model.Ubicacion;
import io.github.salepartido.api.domain.locales.repository.LocalRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ConfiguracionTurnosEspaciosTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LocalRepository localRepository;

    private UUID localUuid;
    private UUID cancha1Uuid;
    private UUID cancha2Uuid;

    @BeforeEach
    void setup() {
        Deporte deporte = new Deporte();
        deporte.setNombre("Futbol");

        Local local = new Local();
        local.setNombre("Complejo El Diez");

        Localidad localidad = new Localidad();
        localidad.setNombre("Puerto Madryn");
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setLocalidad(localidad);
        ubicacion.setDireccion("Calle Falsa 123");
        local.setUbicacion(ubicacion);

        Cancha cancha1 = new Cancha();
        cancha1.setNombre("Cancha 1");
        cancha1.setCapacidad(10);
        cancha1.setDeporte(deporte);

        Cancha cancha2 = new Cancha();
        cancha2.setNombre("Cancha 2");
        cancha2.setCapacidad(10);
        cancha2.setDeporte(deporte);

        local.getCanchas().add(cancha1);
        local.getCanchas().add(cancha2);

        Local savedLocal = localRepository.save(local);
        
        this.localUuid = savedLocal.getUuid();
        this.cancha1Uuid = savedLocal.getCanchas().get(0).getUuid();
        this.cancha2Uuid = savedLocal.getCanchas().get(1).getUuid();
    }

    @Test
    @DisplayName("Aplicar configuración de horario a todas las canchas")
    void deberiaAplicarConfiguracionA_TodasLasCanchas() throws Exception {
        String body = """
        {
            "canchas": [
                {
                    "canchaUuid": "%s",
                    "duracionTurno": 60,
                    "configuracionesDias": [
                        {
                            "diaSemana": "MONDAY",
                            "horaInicio": "14:00",
                            "horaFin": "22:00"
                        }
                    ]
                },
                {
                    "canchaUuid": "%s",
                    "duracionTurno": 60,
                    "configuracionesDias": [
                        {
                            "diaSemana": "MONDAY",
                            "horaInicio": "14:00",
                            "horaFin": "22:00"
                        }
                    ]
                }
            ]
        }
        """.formatted(cancha1Uuid, cancha2Uuid);

        mockMvc.perform(
                post("/locales/{uuid}/configuraciones-horarios", localUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.canchas").isArray())
        .andExpect(jsonPath("$.canchas.length()").value(2));
    }

    @Test
    @DisplayName("Configuración de la duración del turno mediante lista predefinida (30, 60, 90, 120)")
    void deberiaRechazarDuracionDeTurnoNoPermitida() throws Exception {
        // Probamos con una duracion de 45, que no está permitida (debería ser 30, 60, 90, 120)
        String body = """
        {
            "canchas": [
                {
                    "canchaUuid": "%s",
                    "duracionTurno": 45,
                    "configuracionesDias": [
                        {
                            "diaSemana": "MONDAY",
                            "horaInicio": "14:00",
                            "horaFin": "22:00"
                        }
                    ]
                }
            ]
        }
        """.formatted(cancha1Uuid);

        mockMvc.perform(
                post("/locales/{uuid}/configuraciones-horarios", localUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Validación de hora de apertura menor a hora de cierre")
    void deberiaRechazarHoraInicioMayorAHoraFin() throws Exception {
        String body = """
        {
            "canchas": [
                {
                    "canchaUuid": "%s",
                    "duracionTurno": 60,
                    "configuracionesDias": [
                        {
                            "diaSemana": "MONDAY",
                            "horaInicio": "22:00",
                            "horaFin": "14:00"
                        }
                    ]
                }
            ]
        }
        """.formatted(cancha1Uuid);

        mockMvc.perform(
                post("/locales/{uuid}/configuraciones-horarios", localUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Incrementos de tiempo en saltos de 30 minutos")
    void deberiaRechazarHorariosQueNoSeanEnSaltosDe30Minutos() throws Exception {
        // horaInicio con 15 minutos (no está en salto de 30)
        String body = """
        {
            "canchas": [
                {
                    "canchaUuid": "%s",
                    "duracionTurno": 60,
                    "configuracionesDias": [
                        {
                            "diaSemana": "MONDAY",
                            "horaInicio": "14:15",
                            "horaFin": "22:00"
                        }
                    ]
                }
            ]
        }
        """.formatted(cancha1Uuid);

        mockMvc.perform(
                post("/locales/{uuid}/configuraciones-horarios", localUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        )
        .andExpect(status().isBadRequest());
    }

}
