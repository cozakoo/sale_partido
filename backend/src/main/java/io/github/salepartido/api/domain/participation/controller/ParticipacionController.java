package io.github.salepartido.api.domain.participation.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.salepartido.api.domain.participation.controller.dto.InvitacionEstadoRequestDTO;
import io.github.salepartido.api.domain.participation.controller.dto.ParticipacionResponseDTO;
import io.github.salepartido.api.domain.participation.model.Participacion;
import io.github.salepartido.api.domain.participation.service.ParticipacionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/participaciones")
public class ParticipacionController {

    private final ParticipacionService participacionService;

    public ParticipacionController(ParticipacionService participacionService) {
        this.participacionService = participacionService;
    }

    @PatchMapping("/{uuid}")
    public ParticipacionResponseDTO responderInvitacion(@PathVariable UUID uuid, @Valid @RequestBody InvitacionEstadoRequestDTO request) {
        Participacion result = switch (request.estado()) {
            case CONFIRMADO -> participacionService.aceptarInvitacion(uuid);
            case RECHAZADO -> participacionService.rechazarInvitacion(uuid);
        };
        return ParticipacionResponseDTO.from(result);
    }
}
