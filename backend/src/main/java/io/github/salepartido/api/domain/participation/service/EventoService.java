package io.github.salepartido.api.domain.participation.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
import io.github.salepartido.api.domain.participation.repository.EventoRepository;
import io.github.salepartido.api.domain.participation.repository.NivelDeporteRepository;
import io.github.salepartido.api.domain.participation.repository.UsuarioRepository;

@Service
public class EventoService {

    private static final Duration LIMITE_CANCELACION_DEFAULT = Duration.ofHours(1);
    private static final Duration LIMITE_CANCELACION_MINIMO = Duration.ofHours(1);
    private static final Duration LIMITE_CANCELACION_MAXIMO = Duration.ofHours(24);

    private final EventoRepository eventoRepository;
    private final TurnoRepository turnoRepository;
    private final UsuarioRepository usuarioRepository;
    private final NivelDeporteRepository nivelDeporteRepository;

    public EventoService(
            EventoRepository eventoRepository,
            TurnoRepository turnoRepository,
            UsuarioRepository usuarioRepository,
            NivelDeporteRepository nivelDeporteRepository) {
        this.eventoRepository = eventoRepository;
        this.turnoRepository = turnoRepository;
        this.usuarioRepository = usuarioRepository;
        this.nivelDeporteRepository = nivelDeporteRepository;
    }

    /**
     * Crea un nuevo evento a partir de un turno reservado previamente.
     * Pre: turno existente, cupos válidos, límite de cancelación entre 1h y 24h.
     * Post: Evento con estado DISPONIBLE.
     */
    @Transactional
    public Evento crearEvento(CrearEventoCommand command) {
        Turno turno = buscarTurno(command.turnoId());
        Usuario organizador = buscarOrganizador(command.organizadorId());
        NivelDeporte nivelRequerido = resolverNivelRequerido(command.nivelRequeridoId());

        TipoEvento tipo = command.tipo() != null ? command.tipo() : TipoEvento.CERRADO;
        Duration limiteCancelacion = resolverLimiteCancelacion(command.limiteCancelacionParticipacion());

        validarCupoMinimo(command.cupoMinimo());
        validarCuposRelacion(command.cupoMinimo(), command.cupoMaximo());
        validarCupoMaximoCapacidad(command.cupoMaximo(), turno.getCancha().getCapacidad());
        validarLimiteCancelacion(limiteCancelacion);

        Evento evento = new Evento();
        evento.setNombre(command.nombre());
        evento.setTipo(tipo);
        evento.setCupoMinimo(command.cupoMinimo());
        evento.setCupoMaximo(command.cupoMaximo());
        evento.setEstado(EstadoEvento.DISPONIBLE);
        evento.setLimiteCancelacionParticipacion(limiteCancelacion);
        evento.setTurno(turno);
        evento.setOrganizador(organizador);
        evento.setNivelRequerido(nivelRequerido);

        return eventoRepository.save(evento);
    }

    /* --- Lookups --- */

    private Turno buscarTurno(UUID turnoId) {
        if (turnoId == null) {
            throw new TurnoRequeridoException();
        }
        return turnoRepository.findById(turnoId)
                .orElseThrow(TurnoRequeridoException::new);
    }

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
        return limiteCancelacion != null ? limiteCancelacion : LIMITE_CANCELACION_DEFAULT;
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
        if (limiteCancelacion.compareTo(LIMITE_CANCELACION_MINIMO) < 0
                || limiteCancelacion.compareTo(LIMITE_CANCELACION_MAXIMO) > 0) {
            throw new TiempoCancelacionInvalidoException();
        }
    }

}
