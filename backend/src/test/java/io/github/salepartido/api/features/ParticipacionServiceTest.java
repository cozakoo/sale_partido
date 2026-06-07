package io.github.salepartido.api.features;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
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

import io.github.salepartido.api.domain.locales.model.Deporte;
import io.github.salepartido.api.domain.participation.exception.EventoNoDisponibleException;
import io.github.salepartido.api.domain.participation.exception.EventoSinCuposException;
import io.github.salepartido.api.domain.participation.exception.InvitacionYaRespondidaException;
import io.github.salepartido.api.domain.participation.exception.NivelInsuficienteException;
import io.github.salepartido.api.domain.participation.exception.ParticipanteYaRegistradoException;
import io.github.salepartido.api.domain.participation.exception.TipoEventoInvalidoException;
import io.github.salepartido.api.domain.participation.model.EstadoEvento;
import io.github.salepartido.api.domain.participation.model.EstadoParticipacion;
import io.github.salepartido.api.domain.participation.model.Evento;
import io.github.salepartido.api.domain.participation.model.HabilidadJugador;
import io.github.salepartido.api.domain.participation.model.NivelDeporte;
import io.github.salepartido.api.domain.participation.model.Participacion;
import io.github.salepartido.api.domain.participation.model.TipoEvento;
import io.github.salepartido.api.domain.participation.model.Usuario;
import io.github.salepartido.api.domain.participation.repository.EventoRepository;
import io.github.salepartido.api.domain.participation.repository.ParticipacionRepository;
import io.github.salepartido.api.domain.participation.repository.UsuarioRepository;
import io.github.salepartido.api.domain.participation.service.ParticipacionService;

@ExtendWith(MockitoExtension.class)
@DisplayName("ParticipacionService — E2-H01")
class ParticipacionServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ParticipacionRepository participacionRepository;

    @InjectMocks
    private ParticipacionService participacionService;

    // IDs comunes
    private final UUID eventoId = UUID.randomUUID();
    private final UUID usuarioId = UUID.randomUUID();
    private final UUID participacionId = UUID.randomUUID();

    // =========================================================
    // Helpers de construcción de fixtures
    // =========================================================

    private Deporte crearDeporte(String nombre) {
        Deporte d = new Deporte();
        d.setUuid(UUID.randomUUID());
        d.setNombre(nombre);
        return d;
    }

    private NivelDeporte crearNivel(Deporte deporte, int orden) {
        NivelDeporte n = new NivelDeporte();
        n.setUuid(UUID.randomUUID());
        n.setNombre("Nivel " + orden);
        n.setOrden(orden);
        n.setDeporte(deporte);
        return n;
    }

    private HabilidadJugador crearHabilidad(Deporte deporte, NivelDeporte nivel) {
        HabilidadJugador h = new HabilidadJugador();
        h.setUuid(UUID.randomUUID());
        h.setDeporte(deporte);
        h.setNivel(nivel);
        return h;
    }

    private Usuario crearUsuario() {
        Usuario u = new Usuario();
        u.setUuid(usuarioId);
        u.setNombre("Juan Pérez");
        u.setHabilidades(new ArrayList<>());
        return u;
    }

    private Evento crearEvento(TipoEvento tipo, EstadoEvento estado, int cupoMaximo) {
        Evento e = new Evento();
        e.setUuid(eventoId);
        e.setNombre("Partido de prueba");
        e.setTipo(tipo);
        e.setEstado(estado);
        e.setCupoMinimo(2);
        e.setCupoMaximo(cupoMaximo);
        e.setNivelRequerido(null);
        e.setParticipaciones(new ArrayList<>());
        return e;
    }

    private Participacion crearInvitacion(Usuario participante, EstadoParticipacion estado) {
        Participacion p = new Participacion();
        p.setUuid(participacionId);
        p.setEsInvitacion(true);
        p.setEstado(estado);
        p.setAsistio(false);
        p.setParticipante(participante);
        return p;
    }

    // =========================================================
    // unirse()
    // =========================================================

    @Nested
    @DisplayName("unirse()")
    class Unirse {

        @Test
        @DisplayName("Escenario: Unirse directamente a un evento abierto — estado CONFIRMADO")
        void unirse_EventoAbierto_SinRestricciones_CreaParticipacionConfirmada() {
            Evento evento = crearEvento(TipoEvento.ABIERTO, EstadoEvento.DISPONIBLE, 10);
            Usuario usuario = crearUsuario();

            when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));
            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

            Participacion resultado = participacionService.unirse(eventoId, usuarioId);

            assertEquals(EstadoParticipacion.CONFIRMADO, resultado.getEstado());
            assertFalse(resultado.getEsInvitacion());
            assertEquals(usuario, resultado.getParticipante());
            verify(eventoRepository).save(evento);
        }

        @Test
        @DisplayName("Escenario: Unirse a evento abierto sin nivel requerido — siempre permitido")
        void unirse_SinNivelRequerido_PermiteParticipacion() {
            Evento evento = crearEvento(TipoEvento.ABIERTO, EstadoEvento.DISPONIBLE, 10);
            evento.setNivelRequerido(null);
            Usuario usuario = crearUsuario(); // sin habilidades

            when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));
            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

            Participacion resultado = participacionService.unirse(eventoId, usuarioId);

            assertEquals(EstadoParticipacion.CONFIRMADO, resultado.getEstado());
        }

        @Test
        @DisplayName("Escenario: Intentar unirse a un evento con nivel de habilidad incompatible")
        void unirse_NivelInsuficiente_LanzaNivelInsuficienteException() {
            Deporte futbol = crearDeporte("Fútbol");
            NivelDeporte nivelRequerido = crearNivel(futbol, 3); // requiere orden 3
            NivelDeporte nivelJugador = crearNivel(futbol, 1);  // jugador tiene orden 1
            HabilidadJugador habilidad = crearHabilidad(futbol, nivelJugador);

            Evento evento = crearEvento(TipoEvento.ABIERTO, EstadoEvento.DISPONIBLE, 10);
            evento.setNivelRequerido(nivelRequerido);

            Usuario usuario = crearUsuario();
            usuario.setHabilidades(new ArrayList<>(List.of(habilidad)));

            when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));
            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

            assertThrows(NivelInsuficienteException.class,
                    () -> participacionService.unirse(eventoId, usuarioId));
        }

        @Test
        @DisplayName("Escenario: Intentar unirse a un evento con nivel incompatible — sin habilidad en ese deporte")
        void unirse_SinHabilidadEnDeporte_LanzaNivelInsuficienteException() {
            Deporte futbol = crearDeporte("Fútbol");
            NivelDeporte nivelRequerido = crearNivel(futbol, 2);

            Evento evento = crearEvento(TipoEvento.ABIERTO, EstadoEvento.DISPONIBLE, 10);
            evento.setNivelRequerido(nivelRequerido);

            Usuario usuario = crearUsuario(); // sin habilidades registradas

            when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));
            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

            assertThrows(NivelInsuficienteException.class,
                    () -> participacionService.unirse(eventoId, usuarioId));
        }

        @Test
        @DisplayName("Escenario: Intentar unirse a un evento sin cupos disponibles")
        void unirse_SinCupos_LanzaEventoSinCuposException() {
            Evento evento = crearEvento(TipoEvento.ABIERTO, EstadoEvento.DISPONIBLE, 1);

            // El único cupo ya está ocupado
            Participacion ocupado = new Participacion();
            ocupado.setEstado(EstadoParticipacion.CONFIRMADO);
            evento.getParticipaciones().add(ocupado);

            Usuario usuario = crearUsuario();

            when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));
            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

            assertThrows(EventoSinCuposException.class,
                    () -> participacionService.unirse(eventoId, usuarioId));
        }

        @Test
        @DisplayName("Escenario: Intentar unirse a un evento en el que ya participa (CONFIRMADO)")
        void unirse_YaRegistradoConfirmado_LanzaParticipanteYaRegistradoException() {
            Evento evento = crearEvento(TipoEvento.ABIERTO, EstadoEvento.DISPONIBLE, 10);
            Usuario usuario = crearUsuario();

            Participacion existente = new Participacion();
            existente.setEstado(EstadoParticipacion.CONFIRMADO);
            existente.setParticipante(usuario);
            evento.getParticipaciones().add(existente);

            when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));
            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

            assertThrows(ParticipanteYaRegistradoException.class,
                    () -> participacionService.unirse(eventoId, usuarioId));
        }

        @Test
        @DisplayName("Escenario: Intentar unirse a un evento en el que ya participa (PENDIENTE)")
        void unirse_YaRegistradoPendiente_LanzaParticipanteYaRegistradoException() {
            Evento evento = crearEvento(TipoEvento.ABIERTO, EstadoEvento.DISPONIBLE, 10);
            Usuario usuario = crearUsuario();

            Participacion existente = new Participacion();
            existente.setEstado(EstadoParticipacion.PENDIENTE);
            existente.setParticipante(usuario);
            evento.getParticipaciones().add(existente);

            when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));
            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

            assertThrows(ParticipanteYaRegistradoException.class,
                    () -> participacionService.unirse(eventoId, usuarioId));
        }

        @Test
        @DisplayName("Evento no disponible (CANCELADO) — lanza EventoNoDisponibleException")
        void unirse_EventoNoDisponible_LanzaEventoNoDisponibleException() {
            Evento evento = crearEvento(TipoEvento.ABIERTO, EstadoEvento.CANCELADO, 10);
            Usuario usuario = crearUsuario();

            when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));
            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

            assertThrows(EventoNoDisponibleException.class,
                    () -> participacionService.unirse(eventoId, usuarioId));
        }

        @Test
        @DisplayName("Evento CERRADO — no permite unirse directamente, lanza TipoEventoInvalidoException")
        void unirse_EventoCerrado_LanzaTipoEventoInvalidoException() {
            Evento evento = crearEvento(TipoEvento.CERRADO, EstadoEvento.DISPONIBLE, 10);
            Usuario usuario = crearUsuario();

            when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));
            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

            assertThrows(TipoEventoInvalidoException.class,
                    () -> participacionService.unirse(eventoId, usuarioId));
        }

        @Test
        @DisplayName("Nivel igual al requerido — debe permitir la participación")
        void unirse_NivelExactoAlRequerido_PermiteParticipacion() {
            Deporte futbol = crearDeporte("Fútbol");
            NivelDeporte nivel = crearNivel(futbol, 2);
            HabilidadJugador habilidad = crearHabilidad(futbol, nivel);

            Evento evento = crearEvento(TipoEvento.ABIERTO, EstadoEvento.DISPONIBLE, 10);
            evento.setNivelRequerido(nivel); // mismo nivel

            Usuario usuario = crearUsuario();
            usuario.setHabilidades(new ArrayList<>(List.of(habilidad)));

            when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));
            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

            Participacion resultado = participacionService.unirse(eventoId, usuarioId);

            assertEquals(EstadoParticipacion.CONFIRMADO, resultado.getEstado());
        }
    }

    // =========================================================
    // solicitarParticipacion()
    // =========================================================

    @Nested
    @DisplayName("solicitarParticipacion()")
    class SolicitarParticipacion {

        @Test
        @DisplayName("Escenario: Solicitar participación en un evento con confirmación — estado PENDIENTE")
        void solicitar_EventoConConfirmacion_CreaParticipacionPendiente() {
            Evento evento = crearEvento(TipoEvento.CON_CONFIRMACION, EstadoEvento.DISPONIBLE, 10);
            Usuario usuario = crearUsuario();

            when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));
            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

            Participacion resultado = participacionService.solicitarParticipacion(eventoId, usuarioId);

            assertEquals(EstadoParticipacion.PENDIENTE, resultado.getEstado());
            assertFalse(resultado.getEsInvitacion());
            assertEquals(usuario, resultado.getParticipante());
            verify(eventoRepository).save(evento);
        }

        @Test
        @DisplayName("Escenario: Solicitar en evento CON_CONFIRMACION con nivel incompatible")
        void solicitar_NivelInsuficiente_LanzaNivelInsuficienteException() {
            Deporte futbol = crearDeporte("Fútbol");
            NivelDeporte nivelRequerido = crearNivel(futbol, 3);
            NivelDeporte nivelJugador = crearNivel(futbol, 1);
            HabilidadJugador habilidad = crearHabilidad(futbol, nivelJugador);

            Evento evento = crearEvento(TipoEvento.CON_CONFIRMACION, EstadoEvento.DISPONIBLE, 10);
            evento.setNivelRequerido(nivelRequerido);

            Usuario usuario = crearUsuario();
            usuario.setHabilidades(new ArrayList<>(List.of(habilidad)));

            when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));
            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

            assertThrows(NivelInsuficienteException.class,
                    () -> participacionService.solicitarParticipacion(eventoId, usuarioId));
        }

        @Test
        @DisplayName("Evento ABIERTO — no permite solicitudes, lanza TipoEventoInvalidoException")
        void solicitar_EventoAbierto_LanzaTipoEventoInvalidoException() {
            Evento evento = crearEvento(TipoEvento.ABIERTO, EstadoEvento.DISPONIBLE, 10);
            Usuario usuario = crearUsuario();

            when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));
            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

            assertThrows(TipoEventoInvalidoException.class,
                    () -> participacionService.solicitarParticipacion(eventoId, usuarioId));
        }

        @Test
        @DisplayName("Evento sin cupos — lanza EventoSinCuposException")
        void solicitar_SinCupos_LanzaEventoSinCuposException() {
            Evento evento = crearEvento(TipoEvento.CON_CONFIRMACION, EstadoEvento.DISPONIBLE, 1);

            Participacion ocupado = new Participacion();
            ocupado.setEstado(EstadoParticipacion.CONFIRMADO);
            evento.getParticipaciones().add(ocupado);

            Usuario usuario = crearUsuario();

            when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));
            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

            assertThrows(EventoSinCuposException.class,
                    () -> participacionService.solicitarParticipacion(eventoId, usuarioId));
        }
    }

    // =========================================================
    // aceptarInvitacion()
    // =========================================================

    @Nested
    @DisplayName("aceptarInvitacion()")
    class AceptarInvitacion {

        @Test
        @DisplayName("Escenario: Aceptar invitación a evento CERRADO — estado CONFIRMADO")
        void aceptar_InvitacionEventoCerrado_CambiaEstadoAConfirmado() {
            Usuario usuario = crearUsuario();
            Participacion invitacion = crearInvitacion(usuario, EstadoParticipacion.PENDIENTE);
            when(participacionRepository.findById(participacionId)).thenReturn(Optional.of(invitacion));
            when(participacionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Participacion resultado = participacionService.aceptarInvitacion(participacionId);

            assertEquals(EstadoParticipacion.CONFIRMADO, resultado.getEstado());
            assertNotNull(resultado.getFechaEstado());
        }

        @Test
        @DisplayName("Escenario: Aceptar invitación a evento CON_CONFIRMACION — estado CONFIRMADO")
        void aceptar_InvitacionEventoConConfirmacion_CambiaEstadoAConfirmado() {
            Usuario usuario = crearUsuario();
            Participacion invitacion = crearInvitacion(usuario, EstadoParticipacion.PENDIENTE);
            when(participacionRepository.findById(participacionId)).thenReturn(Optional.of(invitacion));
            when(participacionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Participacion resultado = participacionService.aceptarInvitacion(participacionId);

            assertEquals(EstadoParticipacion.CONFIRMADO, resultado.getEstado());
        }

        @Test
        @DisplayName("Escenario: Intentar responder una invitación ya respondida (CONFIRMADO)")
        void aceptar_InvitacionYaConfirmada_LanzaInvitacionYaRespondidaException() {
            Usuario usuario = crearUsuario();
            Participacion invitacion = crearInvitacion(usuario, EstadoParticipacion.CONFIRMADO);
            when(participacionRepository.findById(participacionId)).thenReturn(Optional.of(invitacion));

            assertThrows(InvitacionYaRespondidaException.class,
                    () -> participacionService.aceptarInvitacion(participacionId));
        }

        @Test
        @DisplayName("Escenario: Intentar responder una invitación ya respondida (RECHAZADO)")
        void aceptar_InvitacionYaRechazada_LanzaInvitacionYaRespondidaException() {
            Usuario usuario = crearUsuario();
            Participacion invitacion = crearInvitacion(usuario, EstadoParticipacion.RECHAZADO);
            when(participacionRepository.findById(participacionId)).thenReturn(Optional.of(invitacion));

            assertThrows(InvitacionYaRespondidaException.class,
                    () -> participacionService.aceptarInvitacion(participacionId));
        }

        @Test
        @DisplayName("Participación propia (no invitación) — lanza TipoEventoInvalidoException")
        void aceptar_NoEsInvitacion_LanzaTipoEventoInvalidoException() {
            Usuario usuario = crearUsuario();
            Participacion participacion = new Participacion();
            participacion.setUuid(participacionId);
            participacion.setEsInvitacion(false);
            participacion.setEstado(EstadoParticipacion.PENDIENTE);
            participacion.setParticipante(usuario);
            when(participacionRepository.findById(participacionId)).thenReturn(Optional.of(participacion));

            assertThrows(TipoEventoInvalidoException.class,
                    () -> participacionService.aceptarInvitacion(participacionId));
        }
    }

    // =========================================================
    // rechazarInvitacion()
    // =========================================================

    @Nested
    @DisplayName("rechazarInvitacion()")
    class RechazarInvitacion {

        @Test
        @DisplayName("Escenario: Rechazar invitación a evento CERRADO — estado RECHAZADO")
        void rechazar_InvitacionEventoCerrado_CambiaEstadoARechazado() {
            Usuario usuario = crearUsuario();
            Participacion invitacion = crearInvitacion(usuario, EstadoParticipacion.PENDIENTE);
            when(participacionRepository.findById(participacionId)).thenReturn(Optional.of(invitacion));
            when(participacionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Participacion resultado = participacionService.rechazarInvitacion(participacionId);

            assertEquals(EstadoParticipacion.RECHAZADO, resultado.getEstado());
            assertNotNull(resultado.getFechaEstado());
        }

        @Test
        @DisplayName("Escenario: Rechazar invitación a evento CON_CONFIRMACION — estado RECHAZADO")
        void rechazar_InvitacionEventoConConfirmacion_CambiaEstadoARechazado() {
            Usuario usuario = crearUsuario();
            Participacion invitacion = crearInvitacion(usuario, EstadoParticipacion.PENDIENTE);
            when(participacionRepository.findById(participacionId)).thenReturn(Optional.of(invitacion));
            when(participacionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Participacion resultado = participacionService.rechazarInvitacion(participacionId);

            assertEquals(EstadoParticipacion.RECHAZADO, resultado.getEstado());
        }

        @Test
        @DisplayName("Escenario: Intentar responder una invitación ya respondida")
        void rechazar_InvitacionYaRespondida_LanzaInvitacionYaRespondidaException() {
            Usuario usuario = crearUsuario();
            Participacion invitacion = crearInvitacion(usuario, EstadoParticipacion.RECHAZADO);
            when(participacionRepository.findById(participacionId)).thenReturn(Optional.of(invitacion));

            assertThrows(InvitacionYaRespondidaException.class,
                    () -> participacionService.rechazarInvitacion(participacionId));
        }

        @Test
        @DisplayName("Participación propia (no invitación) — lanza TipoEventoInvalidoException")
        void rechazar_NoEsInvitacion_LanzaTipoEventoInvalidoException() {
            Usuario usuario = crearUsuario();
            Participacion participacion = new Participacion();
            participacion.setUuid(participacionId);
            participacion.setEsInvitacion(false);
            participacion.setEstado(EstadoParticipacion.PENDIENTE);
            participacion.setParticipante(usuario);
            when(participacionRepository.findById(participacionId)).thenReturn(Optional.of(participacion));

            assertThrows(TipoEventoInvalidoException.class,
                    () -> participacionService.rechazarInvitacion(participacionId));
        }
    }
}
