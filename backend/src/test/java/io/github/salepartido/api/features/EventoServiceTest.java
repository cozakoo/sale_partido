package io.github.salepartido.api.features;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import io.github.salepartido.api.domain.locales.model.Cancha;
import io.github.salepartido.api.domain.locales.model.Turno;
import io.github.salepartido.api.domain.locales.repository.TurnoRepository;
import io.github.salepartido.api.domain.participation.exception.CupoMaximoSuperaCapacidadException;
import io.github.salepartido.api.domain.participation.exception.CupoMinimoInvalidoException;
import io.github.salepartido.api.domain.participation.exception.CupoMinimoMayorMaximoException;
import io.github.salepartido.api.domain.participation.exception.TiempoCancelacionInvalidoException;
import io.github.salepartido.api.domain.participation.exception.TurnoRequeridoException;
import io.github.salepartido.api.domain.participation.model.EstadoEvento;
import io.github.salepartido.api.domain.participation.model.Evento;
import io.github.salepartido.api.domain.participation.model.NivelDeporte;
import io.github.salepartido.api.domain.participation.model.TipoEvento;
import io.github.salepartido.api.domain.participation.model.Usuario;
import io.github.salepartido.api.domain.locales.repository.CanchaRepository;
import io.github.salepartido.api.domain.participation.repository.EventoRepository;
import io.github.salepartido.api.domain.participation.repository.NivelDeporteRepository;
import io.github.salepartido.api.domain.participation.repository.UsuarioRepository;
import io.github.salepartido.api.domain.participation.service.CrearEventoCommand;
import io.github.salepartido.api.domain.participation.service.EventoService;
import io.github.salepartido.api.domain.participation.service.TurnoCommand;
import java.time.LocalDate;
import java.time.LocalTime;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventoService — E3-H01")
class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;
    @Mock
    private TurnoRepository turnoRepository;
    @Mock
    private CanchaRepository canchaRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private NivelDeporteRepository nivelDeporteRepository;

    @InjectMocks
    private EventoService eventoService;

    private final UUID turnoId = UUID.randomUUID();
    private final UUID organizadorId = UUID.randomUUID();
    private final UUID nivelId = UUID.randomUUID();

    private Turno turno;
    private Cancha cancha;
    private Usuario organizador;

    @BeforeEach
    void setUp() {
        cancha = new Cancha();
        cancha.setCapacidad(10);
        cancha.setUuid(UUID.randomUUID());

        turno = new Turno();
        turno.setCancha(cancha);

        organizador = new Usuario();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private CrearEventoCommand comandoBase() {
        TurnoCommand tc = new TurnoCommand(
                LocalDate.now().plusDays(1),
                LocalTime.of(18, 0),
                LocalTime.of(19, 0),
                cancha.getUuid()
        );
        return new CrearEventoCommand(
                tc, organizadorId, "Partido de fútbol",
                8, 10, TipoEvento.CERRADO, Duration.ofHours(1), null);
    }

    private CrearEventoCommand crearComandoConParametros(Integer cupoMin, Integer cupoMax, TipoEvento tipo, Duration limite, UUID nivel) {
        TurnoCommand tc = new TurnoCommand(
                LocalDate.now().plusDays(1),
                LocalTime.of(18, 0),
                LocalTime.of(19, 0),
                cancha.getUuid()
        );
        return new CrearEventoCommand(
                tc, organizadorId, "Partido",
                cupoMin, cupoMax, tipo, limite, nivel);
    }

    private void mockTurnoYOrganizador() {
        when(canchaRepository.findById(any(UUID.class))).thenReturn(Optional.of(cancha));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioRepository.findById(organizadorId)).thenReturn(Optional.of(organizador));
        when(eventoRepository.save(any(Evento.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private void mockCanchaYOrganizador() {
        when(canchaRepository.findById(any(UUID.class))).thenReturn(Optional.of(cancha));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioRepository.findById(organizadorId)).thenReturn(Optional.of(organizador));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Creación exitosa
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("crearEvento() — casos exitosos")
    class CreacionExitosa {

        @Test
        @DisplayName("Crea evento con tipo Cerrado y límite de cancelación 1 hora")
        void crearEvento_tipoCerrado_limiteUnaHora() {
            mockTurnoYOrganizador();

            Evento resultado = eventoService.crearEvento(comandoBase());

            assertEquals(TipoEvento.CERRADO, resultado.getTipo());
            assertEquals(Duration.ofHours(1), resultado.getLimiteCancelacionParticipacion());
            assertEquals(EstadoEvento.DISPONIBLE, resultado.getEstado());
            assertNull(resultado.getNivelRequerido());
        }

        @Test
        @DisplayName("Crea evento con tipo Abierto y límite de cancelación 24 horas")
        void crearEvento_tipoAbierto_limite24Horas() {
            mockTurnoYOrganizador();
            CrearEventoCommand command = crearComandoConParametros(8, 10, TipoEvento.ABIERTO, Duration.ofHours(24), null);

            Evento resultado = eventoService.crearEvento(command);

            assertEquals(TipoEvento.ABIERTO, resultado.getTipo());
            assertEquals(Duration.ofHours(24), resultado.getLimiteCancelacionParticipacion());
            assertEquals(EstadoEvento.DISPONIBLE, resultado.getEstado());
        }

        @Test
        @DisplayName("El tipo por defecto es CERRADO cuando no se especifica")
        void crearEvento_sinTipo_defaultCerrado() {
            mockTurnoYOrganizador();
            CrearEventoCommand command = crearComandoConParametros(5, 10, null, Duration.ofHours(2), null);

            Evento resultado = eventoService.crearEvento(command);

            assertEquals(TipoEvento.CERRADO, resultado.getTipo());
        }

        @Test
        @DisplayName("El límite de cancelación por defecto es 1 hora cuando no se especifica")
        void crearEvento_sinLimite_default1Hora() {
            mockTurnoYOrganizador();
            CrearEventoCommand command = crearComandoConParametros(5, 10, TipoEvento.CERRADO, null, null);

            Evento resultado = eventoService.crearEvento(command);

            assertEquals(Duration.ofHours(1), resultado.getLimiteCancelacionParticipacion());
        }

        @Test
        @DisplayName("El estado inicial del evento es DISPONIBLE")
        void crearEvento_estadoInicial_esDisponible() {
            mockTurnoYOrganizador();

            Evento resultado = eventoService.crearEvento(comandoBase());

            assertEquals(EstadoEvento.DISPONIBLE, resultado.getEstado());
        }

        @Test
        @DisplayName("Crea evento con nivel de habilidad requerido")
        void crearEvento_conNivelRequerido_seAsigna() {
            mockTurnoYOrganizador();
            NivelDeporte nivel = new NivelDeporte();
            when(nivelDeporteRepository.findById(nivelId)).thenReturn(Optional.of(nivel));
            CrearEventoCommand command = crearComandoConParametros(5, 10, TipoEvento.CERRADO, Duration.ofHours(1), nivelId);

            Evento resultado = eventoService.crearEvento(command);

            assertEquals(nivel, resultado.getNivelRequerido());
        }

        @Test
        @DisplayName("Cupo mínimo igual al máximo es válido")
        void crearEvento_cupoMinIgualMax_esValido() {
            mockTurnoYOrganizador();
            CrearEventoCommand command = crearComandoConParametros(10, 10, TipoEvento.CERRADO, Duration.ofHours(1), null);

            assertDoesNotThrow(() -> eventoService.crearEvento(command));
        }

        @Test
        @DisplayName("Límite de cancelación exactamente en el límite superior (24h) es válido")
        void crearEvento_limite24Horas_esValido() {
            mockTurnoYOrganizador();
            CrearEventoCommand command = crearComandoConParametros(5, 10, TipoEvento.CERRADO, Duration.ofHours(24), null);

            assertDoesNotThrow(() -> eventoService.crearEvento(command));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Validación de cupos
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("crearEvento() — validaciones de cupo")
    class ValidacionesCupo {

        @Test
        @DisplayName("Lanza excepción cuando cupo mínimo es cero")
        void crearEvento_cupoMinimoEsCero_lanzaExcepcion() {
            mockCanchaYOrganizador();
            CrearEventoCommand command = crearComandoConParametros(0, 10, TipoEvento.CERRADO, Duration.ofHours(1), null);

            assertThrows(CupoMinimoInvalidoException.class, () -> eventoService.crearEvento(command));
        }

        @Test
        @DisplayName("Lanza excepción cuando cupo mínimo es negativo")
        void crearEvento_cupoMinimoNegativo_lanzaExcepcion() {
            mockCanchaYOrganizador();
            CrearEventoCommand command = crearComandoConParametros(-1, 10, TipoEvento.CERRADO, Duration.ofHours(1), null);

            assertThrows(CupoMinimoInvalidoException.class, () -> eventoService.crearEvento(command));
        }

        @Test
        @DisplayName("Lanza excepción cuando cupo mínimo supera al máximo")
        void crearEvento_cupoMinimoMayorQueMaximo_lanzaExcepcion() {
            mockCanchaYOrganizador();
            CrearEventoCommand command = crearComandoConParametros(12, 10, TipoEvento.CERRADO, Duration.ofHours(1), null);

            assertThrows(CupoMinimoMayorMaximoException.class, () -> eventoService.crearEvento(command));
        }

        @Test
        @DisplayName("Lanza excepción cuando cupo máximo supera la capacidad de la cancha")
        void crearEvento_cupoMaximoSuperaCapacidad_lanzaExcepcion() {
            mockCanchaYOrganizador();
            CrearEventoCommand command = crearComandoConParametros(10, 15, TipoEvento.CERRADO, Duration.ofHours(1), null);

            assertThrows(CupoMaximoSuperaCapacidadException.class, () -> eventoService.crearEvento(command));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Validación del límite de cancelación
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("crearEvento() — validaciones de límite de cancelación")
    class ValidacionesLimiteCancelacion {

        @Test
        @DisplayName("Lanza excepción cuando el límite supera las 24 horas")
        void crearEvento_limite48Horas_lanzaExcepcion() {
            mockCanchaYOrganizador();
            CrearEventoCommand command = crearComandoConParametros(5, 10, TipoEvento.CERRADO, Duration.ofHours(48), null);

            assertThrows(TiempoCancelacionInvalidoException.class, () -> eventoService.crearEvento(command));
        }

        @Test
        @DisplayName("Lanza excepción cuando el límite es menor a 1 hora")
        void crearEvento_limite30Minutos_lanzaExcepcion() {
            mockCanchaYOrganizador();
            CrearEventoCommand command = crearComandoConParametros(5, 10, TipoEvento.CERRADO, Duration.ofMinutes(30), null);

            assertThrows(TiempoCancelacionInvalidoException.class, () -> eventoService.crearEvento(command));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Validación del turno
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("crearEvento() — validaciones de turno")
    class ValidacionesTurno {

        @Test
        @DisplayName("Lanza excepción cuando turno es null")
        void crearEvento_turnoIdNull_lanzaExcepcion() {
            CrearEventoCommand command = new CrearEventoCommand(
                    null, organizadorId, "Partido", 5, 10, TipoEvento.CERRADO, Duration.ofHours(1), null);

            assertThrows(TurnoRequeridoException.class, () -> eventoService.crearEvento(command));
        }

        @Test
        @DisplayName("Lanza excepción cuando la cancha no existe")
        void crearEvento_canchaNoExiste_lanzaExcepcion() {
            when(canchaRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            assertThrows(ResponseStatusException.class, () -> eventoService.crearEvento(comandoBase()));
        }
    }

}
