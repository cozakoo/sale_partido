package io.github.salepartido.api.domain.eventos.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;

import io.github.salepartido.api.domain.eventos.exception.CupoMaximoSuperaCapacidadException;
import io.github.salepartido.api.domain.eventos.exception.CupoMinimoInvalidoException;
import io.github.salepartido.api.domain.eventos.exception.CupoMinimoMayorMaximoException;
import io.github.salepartido.api.domain.eventos.exception.TiempoCancelacionInvalidoException;
import io.github.salepartido.api.domain.eventos.exception.TurnoRequeridoException;
import io.github.salepartido.api.domain.eventos.model.Evento;
import io.github.salepartido.api.domain.eventos.model.NivelDeporte;
import io.github.salepartido.api.domain.eventos.model.TipoEvento;
import io.github.salepartido.api.domain.eventos.model.Usuario;
import io.github.salepartido.api.domain.eventos.repository.EventoRepository;
import io.github.salepartido.api.domain.eventos.repository.NivelDeporteRepository;
import io.github.salepartido.api.domain.eventos.repository.UsuarioRepository;
import io.github.salepartido.api.domain.eventos.service.dto.CrearEventoOperation;
import io.github.salepartido.api.domain.eventos.service.mapper.CrearEventoOperationMapper;
import io.github.salepartido.api.domain.eventos.service.mapper.TurnoOperationMapper;
import io.github.salepartido.api.domain.locales.model.Cancha;
import io.github.salepartido.api.domain.locales.model.Turno;
import io.github.salepartido.api.domain.locales.repository.CanchaRepository;
import io.github.salepartido.api.domain.locales.repository.TurnoRepository;

@Service
public class EventoService {

    private final Duration limiteCancelacionDefault;
    private final Duration limiteCancelacionMinimo;
    private final Duration limiteCancelacionMaximo;

    private final EventoRepository eventoRepository;
    private final TurnoRepository turnoRepository;
    private final CanchaRepository canchaRepository;
    private final UsuarioRepository usuarioRepository;
    private final NivelDeporteRepository nivelDeporteRepository;
    private final CrearEventoOperationMapper crearEventoOperationMapper;
    private final TurnoOperationMapper turnoOperationMapper;

    public EventoService(
            EventoRepository eventoRepository,
            TurnoRepository turnoRepository,
            CanchaRepository canchaRepository,
            UsuarioRepository usuarioRepository,
            NivelDeporteRepository nivelDeporteRepository,
            CrearEventoOperationMapper crearEventoOperationMapper,
            TurnoOperationMapper turnoOperationMapper,
            @Value("${app.limits.cancelacion-default-horas:1}") int cancelacionDefaultHoras,
            @Value("${app.limits.cancelacion-min-horas:1}") int cancelacionMinHoras,
            @Value("${app.limits.cancelacion-max-horas:24}") int cancelacionMaxHoras) {
        this.eventoRepository = eventoRepository;
        this.turnoRepository = turnoRepository;
        this.canchaRepository = canchaRepository;
        this.usuarioRepository = usuarioRepository;
        this.nivelDeporteRepository = nivelDeporteRepository;
        this.crearEventoOperationMapper = crearEventoOperationMapper;
        this.turnoOperationMapper = turnoOperationMapper;
        this.limiteCancelacionDefault = Duration.ofHours(cancelacionDefaultHoras);
        this.limiteCancelacionMinimo = Duration.ofHours(cancelacionMinHoras);
        this.limiteCancelacionMaximo = Duration.ofHours(cancelacionMaxHoras);
    }

    /**
     * Crea un nuevo evento a partir de un turno reservado previamente.
     * Pre: turno existente, cupos válidos, límite de cancelación entre 1h y 24h.
     * Post: Evento con estado DISPONIBLE.
     */
    @Transactional
    public Evento crearEvento(CrearEventoOperation command) {
        if (command.turno() == null) {
            throw new TurnoRequeridoException();
        }

        Cancha cancha = canchaRepository.findById(command.turno().canchaUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cancha no encontrada"));

        Turno turno = turnoOperationMapper.toTurnoEntity(command.turno(), cancha);
        turno = turnoRepository.save(turno);

        Usuario organizador = buscarOrganizador(command.organizadorId());
        NivelDeporte nivelRequerido = resolverNivelRequerido(command.nivelRequeridoId());

        TipoEvento tipo = command.tipo() != null ? command.tipo() : TipoEvento.CERRADO;
        Duration limiteCancelacion = resolverLimiteCancelacion(command.limiteCancelacionParticipacion());

        validarCupoMinimo(command.cupoMinimo());
        validarCuposRelacion(command.cupoMinimo(), command.cupoMaximo());
        validarCupoMaximoCapacidad(command.cupoMaximo(), cancha.getCapacidad());
        validarLimiteCancelacion(limiteCancelacion);

        Evento evento = crearEventoOperationMapper.toEventoEntity(command, turno, organizador, nivelRequerido, tipo, limiteCancelacion);

        return eventoRepository.save(evento);
    }

    public Evento getEvento(UUID eventoId) {
        return eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));
    }

    public java.util.List<Evento> getEventos() {
        return eventoRepository.findAll();
    }

    public java.util.List<Evento> getEventos(Boolean finalizados) {
        if (Boolean.FALSE.equals(finalizados)) {
            return eventoRepository.findByEstadoNot(io.github.salepartido.api.domain.eventos.model.EstadoEvento.FINALIZADO);
        }
        return getEventos();
    }

    /* --- Lookups --- */

    private Usuario buscarOrganizador(UUID organizadorId) {
        return usuarioRepository.findById(organizadorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private NivelDeporte resolverNivelRequerido(UUID nivelRequeridoId) {
        if (nivelRequeridoId == null) {
            return null;
        }
        return nivelDeporteRepository.findById(nivelRequeridoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nivel de deporte no encontrado"));
    }

    /* --- Defaults --- */

    private Duration resolverLimiteCancelacion(Duration limiteCancelacion) {
        return limiteCancelacion != null ? limiteCancelacion : limiteCancelacionDefault;
    }

    /* --- Validaciones --- */

    private void validarCupoMinimo(int cupoMinimo) {
        if (cupoMinimo <= 0) {
            throw new CupoMinimoInvalidoException();
        }
    }

    private void validarCuposRelacion(int cupoMinimo, int cupoMaximo) {
        if (cupoMinimo > cupoMaximo) {
            throw new CupoMinimoMayorMaximoException();
        }
    }

    private void validarCupoMaximoCapacidad(int cupoMaximo, int capacidadCancha) {
        if (cupoMaximo > capacidadCancha) {
            throw new CupoMaximoSuperaCapacidadException();
        }
    }

    private void validarLimiteCancelacion(Duration limiteCancelacion) {
        if (limiteCancelacion.compareTo(limiteCancelacionMinimo) < 0
                || limiteCancelacion.compareTo(limiteCancelacionMaximo) > 0) {
            throw new TiempoCancelacionInvalidoException();
        }
    }
}
