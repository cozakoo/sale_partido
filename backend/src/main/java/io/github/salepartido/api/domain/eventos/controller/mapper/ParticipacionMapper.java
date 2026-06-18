package io.github.salepartido.api.domain.eventos.controller.mapper;

import org.springframework.stereotype.Component;
import io.github.salepartido.api.domain.eventos.controller.dto.ParticipacionResponseDTO;
import io.github.salepartido.api.domain.eventos.model.Participacion;

@Component
public class ParticipacionMapper {

    public ParticipacionResponseDTO toResponseDTO(Participacion p) {
        if (p == null) return null;
        return new ParticipacionResponseDTO(
                p.getUuid(),
                p.getEstado() != null ? p.getEstado().name() : null,
                p.getEsInvitacion(),
                p.getFechaEstado());
    }
}
