package io.github.salepartido.api.domain.participation.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

@Service
public class ParticipacionService {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ParticipacionRepository participacionRepository;

    public ParticipacionService(
            EventoRepository eventoRepository,
            UsuarioRepository usuarioRepository,
            ParticipacionRepository participacionRepository) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
        this.participacionRepository = participacionRepository;
    }

    /**
     * El participante se une directamente a un evento ABIERTO.
     * Pre: evento DISPONIBLE, tipo ABIERTO, cupos disponibles, nivel cumplido, no registrado.
     * Post: Participacion con estado CONFIRMADO.
     */
    @Transactional
    public Participacion unirse(UUID eventoId, UUID usuarioId) {
        Evento evento = buscarEvento(eventoId);
        Usuario usuario = buscarUsuario(usuarioId);

        validarEventoDisponible(evento);
        validarTipoAbierto(evento);
        validarCuposDisponibles(evento);
        validarNivel(usuario, evento);
        validarNoRegistrado(usuario, evento);

        return crearParticipacion(evento, usuario, EstadoParticipacion.CONFIRMADO);
    }

    /**
     * El participante solicita unirse a un evento CON_CONFIRMACION.
     * Pre: evento DISPONIBLE, tipo CON_CONFIRMACION, cupos disponibles, nivel cumplido, no registrado.
     * Post: Participacion con estado PENDIENTE (el organizador debe aprobar).
     */
    @Transactional
    public Participacion solicitarParticipacion(UUID eventoId, UUID usuarioId) {
        Evento evento = buscarEvento(eventoId);
        Usuario usuario = buscarUsuario(usuarioId);

        validarEventoDisponible(evento);
        validarTipoConConfirmacion(evento);
        validarCuposDisponibles(evento);
        validarNivel(usuario, evento);
        validarNoRegistrado(usuario, evento);

        return crearParticipacion(evento, usuario, EstadoParticipacion.PENDIENTE);
    }

    /**
     * El participante acepta una invitación recibida (esInvitacion=true, estado PENDIENTE).
     * Aplica a eventos CERRADO y CON_CONFIRMACION.
     * Post: Participacion con estado CONFIRMADO.
     */
    @Transactional
    public Participacion aceptarInvitacion(UUID participacionId) {
        Participacion participacion = buscarParticipacion(participacionId);
        validarEsInvitacion(participacion);
        validarInvitacionPendiente(participacion);

        participacion.setEstado(EstadoParticipacion.CONFIRMADO);
        participacion.setFechaEstado(LocalDateTime.now());
        return participacionRepository.save(participacion);
    }

    /**
     * El participante rechaza una invitación recibida (esInvitacion=true, estado PENDIENTE).
     * Aplica a eventos CERRADO y CON_CONFIRMACION.
     * Post: Participacion con estado RECHAZADO.
     */
    @Transactional
    public Participacion rechazarInvitacion(UUID participacionId) {
        Participacion participacion = buscarParticipacion(participacionId);
        validarEsInvitacion(participacion);
        validarInvitacionPendiente(participacion);

        participacion.setEstado(EstadoParticipacion.RECHAZADO);
        participacion.setFechaEstado(LocalDateTime.now());
        return participacionRepository.save(participacion);
    }

    @Transactional(readOnly = true)
    public Participacion getParticipacionActiva(UUID eventoId, UUID usuarioId) {
        Evento evento = buscarEvento(eventoId);
        return evento.getParticipaciones().stream()
                .filter(p -> p.getParticipante().getUuid().equals(usuarioId))
                .filter(p -> p.getEstado() == EstadoParticipacion.CONFIRMADO 
                          || p.getEstado() == EstadoParticipacion.PENDIENTE)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, 
                        "El usuario no participa ni tiene invitación activa para este evento"
                ));
    }

    /* --- Lookups --- */

    private Evento buscarEvento(UUID eventoId) {
        return eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));
    }

    private Usuario buscarUsuario(UUID usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private Participacion buscarParticipacion(UUID participacionId) {
        return participacionRepository.findById(participacionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participación no encontrada"));
    }

    /* --- Validaciones --- */

    private void validarEventoDisponible(Evento evento) {
        if (evento.getEstado() != EstadoEvento.DISPONIBLE) {
            throw new EventoNoDisponibleException();
        }
    }

    private void validarTipoAbierto(Evento evento) {
        if (evento.getTipo() != TipoEvento.ABIERTO) {
            throw new TipoEventoInvalidoException(
                    "Este evento no permite unirse directamente. Sólo los eventos ABIERTOS aceptan inscripción directa.");
        }
    }

    private void validarTipoConConfirmacion(Evento evento) {
        if (evento.getTipo() != TipoEvento.CON_CONFIRMACION) {
            throw new TipoEventoInvalidoException(
                    "Este evento no permite solicitudes de participación. Sólo los eventos CON_CONFIRMACION aceptan solicitudes.");
        }
    }

    /**
     * Cupo disponible: cantidad de participaciones CONFIRMADAS < cupoMaximo.
     * Las participaciones PENDIENTES no ocupan cupo hasta ser confirmadas.
     */
    private void validarCuposDisponibles(Evento evento) {
        long confirmados = evento.getParticipaciones().stream()
                .filter(p -> p.getEstado() == EstadoParticipacion.CONFIRMADO)
                .count();
        if (confirmados >= evento.getCupoMaximo()) {
            throw new EventoSinCuposException();
        }
    }

    /**
     * Si el evento no tiene nivelRequerido, cualquier participante puede unirse.
     * Si tiene nivelRequerido, el participante debe tener HabilidadJugador para ese deporte
     * con orden >= orden del nivel requerido.
     */
    private void validarNivel(Usuario usuario, Evento evento) {
        NivelDeporte nivelRequerido = evento.getNivelRequerido();
        if (nivelRequerido == null) {
            return;
        }

        Deporte deporteEvento = nivelRequerido.getDeporte();
        HabilidadJugador habilidad = usuario.getHabilidades().stream()
                .filter(h -> h.getDeporte().getUuid().equals(deporteEvento.getUuid()))
                .findFirst()
                .orElseThrow(NivelInsuficienteException::new);

        if (habilidad.getNivel().getOrden() < nivelRequerido.getOrden()) {
            throw new NivelInsuficienteException();
        }
    }

    /**
     * Participante registrado: ya tiene una participación CONFIRMADA o PENDIENTE en el evento.
     */
    private void validarNoRegistrado(Usuario usuario, Evento evento) {
        boolean yaRegistrado = evento.getParticipaciones().stream()
                .filter(p -> p.getParticipante().getUuid().equals(usuario.getUuid()))
                .anyMatch(p -> p.getEstado() == EstadoParticipacion.CONFIRMADO
                            || p.getEstado() == EstadoParticipacion.PENDIENTE);
        if (yaRegistrado) {
            throw new ParticipanteYaRegistradoException();
        }
    }

    private void validarEsInvitacion(Participacion participacion) {
        if (!Boolean.TRUE.equals(participacion.getEsInvitacion())) {
            throw new TipoEventoInvalidoException("Esta operación sólo aplica a invitaciones recibidas.");
        }
    }

    /**
     * Invitación respondida: ya fue aceptada o rechazada (estado != PENDIENTE).
     */
    private void validarInvitacionPendiente(Participacion participacion) {
        if (participacion.getEstado() != EstadoParticipacion.PENDIENTE) {
            throw new InvitacionYaRespondidaException();
        }
    }

    /* --- Creación --- */

    private Participacion crearParticipacion(Evento evento, Usuario usuario, EstadoParticipacion estado) {
        Participacion p = new Participacion();
        p.setEsInvitacion(false);
        p.setEstado(estado);
        p.setFechaEstado(LocalDateTime.now());
        p.setAsistio(false);
        p.setParticipante(usuario);

        evento.getParticipaciones().add(p);
        eventoRepository.save(evento);
        return p;
    }

}
