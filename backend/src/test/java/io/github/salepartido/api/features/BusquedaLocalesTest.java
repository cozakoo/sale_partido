package io.github.salepartido.api.features;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import io.github.salepartido.api.domain.locales.model.Cancha;
import io.github.salepartido.api.domain.locales.model.ConfiguracionDia;
import io.github.salepartido.api.domain.locales.model.ConfiguracionHorario;
import io.github.salepartido.api.domain.locales.model.Deporte;
import io.github.salepartido.api.domain.locales.model.Local;
import io.github.salepartido.api.domain.locales.model.Localidad;
import io.github.salepartido.api.domain.locales.model.Ubicacion;
import io.github.salepartido.api.domain.locales.repository.LocalRepository;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BusquedaLocalesTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LocalRepository localRepository;

    @BeforeEach
    void setup() {
        // Deporte 1: Fútbol
        Deporte deporteFutbol = new Deporte();
        deporteFutbol.setNombre("Fútbol");

        // Deporte 2: Tenis
        Deporte deporteTenis = new Deporte();
        deporteTenis.setNombre("Tenis");

        // Local 1: Complejo El Diez (Fútbol, Puerto Madryn)
        Local local1 = new Local();
        local1.setNombre("Complejo El Diez");
        local1.setTelefono("+54 280 411-1001");
        local1.setDescripcion("Complejo deportivo con césped sintético");
        
        Localidad localidad1 = new Localidad();
        localidad1.setNombre("Puerto Madryn");
        Ubicacion ubicacion1 = new Ubicacion();
        ubicacion1.setLocalidad(localidad1);
        ubicacion1.setDireccion("Calle Roca 123");
        local1.setUbicacion(ubicacion1);

        Cancha cancha1 = new Cancha();
        cancha1.setNombre("Cancha F10");
        cancha1.setCapacidad(10);
        cancha1.setDeporte(deporteFutbol);

        ConfiguracionHorario configHorario1 = new ConfiguracionHorario();
        configHorario1.setActivo(true);
        configHorario1.setDuracionTurno(Duration.ofMinutes(60));
        ConfiguracionDia configDia1 = new ConfiguracionDia();
        configDia1.setDiaSemana(DayOfWeek.MONDAY);
        configDia1.setHoraInicio(LocalTime.of(8, 0));
        configDia1.setHoraFin(LocalTime.of(22, 0));
        configHorario1.setConfiguracionesDias(List.of(configDia1));
        cancha1.setConfiguracionesHorarios(List.of(configHorario1));

        local1.getCanchas().add(cancha1);

        // Local 2: Club Trelew (Tenis, Trelew)
        Local local2 = new Local();
        local2.setNombre("Club Trelew");
        local2.setTelefono("+54 280 422-2002");
        local2.setDescripcion("Club social y deportivo con canchas de polvo de ladrillo");

        Localidad localidad2 = new Localidad();
        localidad2.setNombre("Trelew");
        Ubicacion ubicacion2 = new Ubicacion();
        ubicacion2.setLocalidad(localidad2);
        ubicacion2.setDireccion("Av. San Martín 456");
        local2.setUbicacion(ubicacion2);

        Cancha cancha2 = new Cancha();
        cancha2.setNombre("Cancha de Tenis 1");
        cancha2.setCapacidad(4);
        cancha2.setDeporte(deporteTenis);

        ConfiguracionHorario configHorario2 = new ConfiguracionHorario();
        configHorario2.setActivo(true);
        configHorario2.setDuracionTurno(Duration.ofMinutes(90));
        ConfiguracionDia configDia2 = new ConfiguracionDia();
        configDia2.setDiaSemana(DayOfWeek.MONDAY);
        configDia2.setHoraInicio(LocalTime.of(9, 0));
        configDia2.setHoraFin(LocalTime.of(18, 0));
        configHorario2.setConfiguracionesDias(List.of(configDia2));
        cancha2.setConfiguracionesHorarios(List.of(configHorario2));

        local2.getCanchas().add(cancha2);

        // Guardar ambos locales en la base de datos
        localRepository.save(local1);
        localRepository.save(local2);
    }

    @Test
    @DisplayName("Visualizar listado de locales disponibles sin filtros")
    void deberiaVisualizarListadoDeLocalesDisponibles() throws Exception {
        mockMvc.perform(get("/locales")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].nombre").isNotEmpty())
            .andExpect(jsonPath("$[0].ubicacion").isNotEmpty())
            .andExpect(jsonPath("$[0].telefono").isNotEmpty())
            .andExpect(jsonPath("$[0].descripcion").isNotEmpty())
            .andExpect(jsonPath("$[0].deportes").isArray());
    }

    @Test
    @DisplayName("Filtrar locales por ubicación determinada")
    void deberiaFiltrarLocalesPorUbicacion() throws Exception {
        // Búsqueda en "Puerto Madryn"
        mockMvc.perform(get("/locales")
                .param("ubicacion", "Puerto")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].nombre").value("Complejo El Diez"))
            .andExpect(jsonPath("$[0].ubicacion").value("Calle Roca 123, Puerto Madryn"));
    }

    @Test
    @DisplayName("Filtrar locales por tipo de deporte")
    void deberiaFiltrarLocalesPorDeporte() throws Exception {
        // Búsqueda por deporte "Tenis"
        mockMvc.perform(get("/locales")
                .param("tipoDeporte", "Tenis")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].nombre").value("Club Trelew"));
    }

    @Test
    @DisplayName("Filtrar locales por fecha y horario de disponibilidad")
    void deberiaFiltrarLocalesPorFechaYHorario() throws Exception {
        // Lunes 1 de Junio de 2026, rango de 10:00 a 11:00.
        // Ambos locales abren el lunes en ese horario.
        mockMvc.perform(get("/locales")
                .param("fecha", "2026-06-01")
                .param("horarioDesde", "10:00")
                .param("horarioHasta", "11:00")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));

        // Probamos una hora donde Club Trelew está cerrado (ej: 20:00 hs), solo Complejo El Diez está abierto
        mockMvc.perform(get("/locales")
                .param("fecha", "2026-06-01")
                .param("horarioDesde", "20:00")
                .param("horarioHasta", "21:00")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].nombre").value("Complejo El Diez"));
    }

    @Test
    @DisplayName("Aplicar múltiples filtros combinando criterios")
    void deberiaFiltrarConMultiplesFiltros() throws Exception {
        // Combinando ubicación "Trelew" y deporte "Tenis"
        mockMvc.perform(get("/locales")
                .param("ubicacion", "Trelew")
                .param("tipoDeporte", "Tenis")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].nombre").value("Club Trelew"));
    }

    @Test
    @DisplayName("No se encuentran resultados al usar filtros incompatibles")
    void deberiaRetornarVacioCuandoNoCoincideNinguno() throws Exception {
        mockMvc.perform(get("/locales")
                .param("ubicacion", "Comodoro Rivadavia")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }
}
