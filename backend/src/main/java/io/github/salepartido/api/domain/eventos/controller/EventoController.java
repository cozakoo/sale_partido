package io.github.salepartido.api.domain.eventos.controller;

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

import io.github.salepartido.api.domain.eventos.controller.dto.CrearEventoRequestDTO;
import io.github.salepartido.api.domain.eventos.controller.dto.EventoDetailDTO;
import io.github.salepartido.api.domain.eventos.controller.dto.ParticipacionRequestDTO;
import io.github.salepartido.api.domain.eventos.controller.dto.ParticipacionResponseDTO;
import io.github.salepartido.api.domain.eventos.controller.mapper.EventoMapper;
import io.github.salepartido.api.domain.eventos.controller.mapper.ParticipacionMapper;
import io.github.salepartido.api.domain.eventos.model.Evento;
import io.github.salepartido.api.domain.eventos.model.Participacion;
import io.github.salepartido.api.domain.eventos.service.EventoService;
import io.github.salepartido.api.domain.eventos.service.ParticipacionService;
import io.github.salepartido.api.domain.eventos.service.dto.CrearEventoOperation;
import io.github.salepartido.api.domain.locales.model.Local;
import io.github.salepartido.api.domain.locales.service.LocalService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService eventoService;
    private final ParticipacionService participacionService;
    private final LocalService localService;
    private final EventoMapper eventoMapper;
    private final ParticipacionMapper participacionMapper;

    public EventoController(
            EventoService eventoService,
            ParticipacionService participacionService,
            LocalService localService,
            EventoMapper eventoMapper,
            ParticipacionMapper participacionMapper) {
        this.eventoService = eventoService;
        this.participacionService = participacionService;
        this.localService = localService;
        this.eventoMapper = eventoMapper;
        this.participacionMapper = participacionMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventoDetailDTO crearEvento(@Valid @RequestBody CrearEventoRequestDTO request) {
        CrearEventoOperation cmd = eventoMapper.toOperation(request);
        var evento = eventoService.crearEvento(cmd);
        var local = evento.getTurno() != null && evento.getTurno().getCancha() != null
                ? localService.buscarLocalPorCanchaUuid(evento.getTurno().getCancha().getUuid()).orElse(null)
                : null;
        return eventoMapper.toDetailDTO(evento, local);
    }

    @GetMapping
    public List<EventoDetailDTO> getEventos(@org.springframework.web.bind.annotation.RequestParam(value = "finalizados", required = false) Boolean finalizados) {
        return eventoService.getEventos(finalizados).stream()
                .map(evento -> {
                    Local local = null;
                    if (evento.getTurno() != null && evento.getTurno().getCancha() != null) {
                        local = localService.buscarLocalPorCanchaUuid(evento.getTurno().getCancha().getUuid()).orElse(null);
                    }
                    return eventoMapper.toDetailDTO(evento, local);
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
        return eventoMapper.toDetailDTO(evento, local);
    }

    @GetMapping("/{uuid}/participaciones/usuario/{usuarioUuid}")
    public ParticipacionResponseDTO getParticipacionUsuario(@PathVariable UUID uuid, @PathVariable UUID usuarioUuid) {
        Participacion p = participacionService.getParticipacionActiva(uuid, usuarioUuid);
        return participacionMapper.toResponseDTO(p);
    }

    @PostMapping("/{uuid}/participaciones")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipacionResponseDTO unirse(@PathVariable UUID uuid, @Valid @RequestBody ParticipacionRequestDTO request) {
        return participacionMapper.toResponseDTO(participacionService.unirse(uuid, request.usuarioUuid()));
    }

    @PostMapping("/{uuid}/solicitudes")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipacionResponseDTO solicitarParticipacion(@PathVariable UUID uuid, @Valid @RequestBody ParticipacionRequestDTO request) {
        return participacionMapper.toResponseDTO(participacionService.solicitarParticipacion(uuid, request.usuarioUuid()));
    }
}
