package io.github.salepartido.api.domain.locales.controller.mapper;

import org.springframework.stereotype.Component;
import io.github.salepartido.api.domain.locales.controller.dto.DisponibilidadCanchaDTO;
import io.github.salepartido.api.domain.locales.controller.dto.TurnoSlotDTO;
import io.github.salepartido.api.domain.locales.controller.dto.TurnoDTO;
import io.github.salepartido.api.domain.locales.service.dto.CanchaDisponibilidad;
import io.github.salepartido.api.domain.locales.service.dto.TurnoSlot;
import io.github.salepartido.api.domain.locales.service.dto.TurnoInfo;

import java.util.stream.Collectors;

@Component
public class DisponibilidadMapper {

    public DisponibilidadCanchaDTO toDTO(CanchaDisponibilidad dom) {
        if (dom == null) return null;
        return new DisponibilidadCanchaDTO(
            dom.canchaUuid(),
            dom.canchaNombre(),
            dom.capacidad(),
            dom.slots().stream().map(this::toDTO).collect(Collectors.toList())
        );
    }

    public TurnoSlotDTO toDTO(TurnoSlot dom) {
        if (dom == null) return null;
        return new TurnoSlotDTO(
            dom.fecha(),
            dom.horaInicio(),
            dom.horaFin(),
            dom.canchaNombre(),
            dom.deporte(),
            dom.estado(),
            toDTO(dom.turno())
        );
    }

    public TurnoDTO toDTO(TurnoInfo dom) {
        if (dom == null) return null;
        return new TurnoDTO(
            dom.uuid(),
            dom.organizadorNombre(),
            dom.deporte(),
            dom.capacidad(),
            dom.cantidadConfirmados(),
            dom.estadoEvento()
        );
    }
}
