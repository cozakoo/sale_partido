package io.github.salepartido.api.domain.participation.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.salepartido.api.domain.participation.controller.dto.EventoDetailDTO;
import io.github.salepartido.api.domain.participation.controller.dto.ParticipacionRequestDTO;
import io.github.salepartido.api.domain.participation.controller.dto.ParticipacionResponseDTO;
import io.github.salepartido.api.domain.participation.service.EventoService;
import io.github.salepartido.api.domain.participation.service.ParticipacionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService eventoService;
    private final ParticipacionService participacionService;

    public EventoController(EventoService eventoService, ParticipacionService participacionService) {
        this.eventoService = eventoService;
        this.participacionService = participacionService;
    }

    @GetMapping("/{uuid}")
    public EventoDetailDTO getEvento(@PathVariable UUID uuid) {
        return EventoDetailDTO.from(eventoService.getEvento(uuid));
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
