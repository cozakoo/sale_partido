package io.github.salepartido.api.domain.eventos.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.salepartido.api.domain.eventos.controller.dto.InvitacionEstadoRequestDTO;
import io.github.salepartido.api.domain.eventos.controller.dto.ParticipacionResponseDTO;
import io.github.salepartido.api.domain.eventos.controller.mapper.ParticipacionMapper;
import io.github.salepartido.api.domain.eventos.model.Participacion;
import io.github.salepartido.api.domain.eventos.service.ParticipacionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/participaciones")
public class ParticipacionController {

    private final ParticipacionService participacionService;
    private final ParticipacionMapper participacionMapper;

    public ParticipacionController(ParticipacionService participacionService, ParticipacionMapper participacionMapper) {
        this.participacionService = participacionService;
        this.participacionMapper = participacionMapper;
    }

    @PatchMapping("/{uuid}")
    public ParticipacionResponseDTO responderInvitacion(@PathVariable UUID uuid, @Valid @RequestBody InvitacionEstadoRequestDTO request) {
        Participacion result = switch (request.estado()) {
            case CONFIRMADO -> participacionService.aceptarInvitacion(uuid);
            case RECHAZADO -> participacionService.rechazarInvitacion(uuid);
        };
        return participacionMapper.toResponseDTO(result);
    }
}
