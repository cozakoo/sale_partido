package io.github.salepartido.api.domain.participation.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.salepartido.api.domain.locales.model.Local;
import io.github.salepartido.api.domain.locales.service.LocalService;
import io.github.salepartido.api.domain.participation.controller.dto.EventoDetailDTO;
import io.github.salepartido.api.domain.participation.controller.dto.CrearEventoRequestDTO;
import io.github.salepartido.api.domain.participation.controller.dto.ParticipacionRequestDTO;
import io.github.salepartido.api.domain.participation.controller.dto.ParticipacionResponseDTO;
import io.github.salepartido.api.domain.participation.model.Evento;
import io.github.salepartido.api.domain.participation.model.Participacion;
import io.github.salepartido.api.domain.participation.service.EventoService;
import io.github.salepartido.api.domain.participation.service.ParticipacionService;
import jakarta.validation.Valid;
import io.github.salepartido.api.domain.participation.service.CrearEventoCommand;
import io.github.salepartido.api.domain.participation.service.TurnoCommand;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService eventoService;
    private final ParticipacionService participacionService;
    private final LocalService localService;

    public EventoController(
            EventoService eventoService,
            ParticipacionService participacionService,
            LocalService localService) {
        this.eventoService = eventoService;
        this.participacionService = participacionService;
        this.localService = localService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventoDetailDTO crearEvento(@Valid @RequestBody CrearEventoRequestDTO request) {
    java.time.Duration limite = request.limiteCancelacionParticipacion() != null
        ? java.time.Duration.ofMinutes(request.limiteCancelacionParticipacion())
        : null;

    TurnoCommand turnoCmd = null;
    if (request.turno() != null) {
        turnoCmd = new TurnoCommand(
            request.turno().fecha(),
            request.turno().horaInicio(),
            request.turno().horaFin(),
            request.turno().canchaUuid()
        );
    }

    CrearEventoCommand cmd =
        new CrearEventoCommand(
            turnoCmd,
            request.organizadorUuid(),
            request.nombre(),
            request.cupoMinimo(),
            request.cupoMaximo(),
            request.tipo() != null ? io.github.salepartido.api.domain.participation.model.TipoEvento.valueOf(request.tipo()) : null,
            limite,
            request.nivelRequeridoUuid());

    var evento = eventoService.crearEvento(cmd);
    var local = evento.getTurno() != null && evento.getTurno().getCancha() != null
        ? localService.buscarLocalPorCanchaUuid(evento.getTurno().getCancha().getUuid()).orElse(null)
        : null;
    return EventoDetailDTO.from(evento, local);
    }

    @GetMapping
    public List<EventoDetailDTO> getEventos() {
        return eventoService.getEventos().stream()
                .map(evento -> {
                    Local local = null;
                    if (evento.getTurno() != null && evento.getTurno().getCancha() != null) {
                        local = localService.buscarLocalPorCanchaUuid(evento.getTurno().getCancha().getUuid()).orElse(null);
                    }
                    return EventoDetailDTO.from(evento, local);
                })
                .toList();
    }

    @GetMapping("/{uuid}")
    public EventoDetailDTO getEvento(@PathVariable UUID uuid) {
        Evento evento = eventoService.getEvento(uuid);
        Local local = null;
        if (evento.getTurno() != null && evento.getTurno().getCancha() != null) {
            local = localService.buscarLocalPorCanchaUuid(evento.getTurno().getCancha().getUuid()).orElse(null);
        }
        return EventoDetailDTO.from(evento, local);
    }

    @GetMapping("/{uuid}/participaciones/usuario/{usuarioUuid}")
    public ParticipacionResponseDTO getParticipacionUsuario(@PathVariable UUID uuid, @PathVariable UUID usuarioUuid) {
        Participacion p = participacionService.getParticipacionActiva(uuid, usuarioUuid);
        return ParticipacionResponseDTO.from(p);
    }

    @PostMapping("/{uuid}/participaciones")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipacionResponseDTO unirse(@PathVariable UUID uuid, @Valid @RequestBody ParticipacionRequestDTO request) {
        return ParticipacionResponseDTO.from(participacionService.unirse(uuid, request.usuarioUuid()));
    }

    @PostMapping("/{uuid}/solicitudes")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipacionResponseDTO solicitarParticipacion(@PathVariable UUID uuid, @Valid @RequestBody ParticipacionRequestDTO request) {
        return ParticipacionResponseDTO.from(participacionService.solicitarParticipacion(uuid, request.usuarioUuid()));
    }
}
