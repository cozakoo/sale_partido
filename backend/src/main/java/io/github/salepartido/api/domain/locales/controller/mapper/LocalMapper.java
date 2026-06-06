package io.github.salepartido.api.domain.locales.controller.mapper;

import org.springframework.stereotype.Component;
import io.github.salepartido.api.domain.locales.model.Local;
import io.github.salepartido.api.domain.locales.controller.dto.LocalDetail;
import io.github.salepartido.api.domain.locales.controller.dto.LocalSummary;
import io.github.salepartido.api.domain.locales.controller.dto.LocalViewModel;
import io.github.salepartido.api.domain.locales.controller.dto.CanchaViewModel;

import java.util.stream.Collectors;
import java.util.List;
import java.util.Objects;

@Component
public class LocalMapper {
    private final CanchaMapper canchaMapper;

    public LocalMapper(CanchaMapper canchaMapper) {
        this.canchaMapper = canchaMapper;
    }

    public LocalSummary toSummary(Local local) {
        if (local == null) return null;
        return new LocalSummary(local.getUuid(), local.getNombre());
    }

    public LocalDetail toDetail(Local local) {
        if (local == null) return null;

        List<CanchaViewModel> canchas = local.getCanchas() != null ?
            local.getCanchas().stream().map(canchaMapper::toViewModel).collect(Collectors.toList()) : List.of();

        List<String> deportes = canchas.stream()
            .map(CanchaViewModel::deporte)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

        return new LocalDetail(
            local.getUuid(),
            local.getNombre(),
            getUbicacionString(local),
            deportes,
            local.getTelefono(),
            local.getDescripcion(),
            local.getHorariosAtencion(),
            canchas
        );
    }

    public LocalViewModel toViewModel(Local local) {
        if (local == null) return null;

        List<CanchaViewModel> canchas = local.getCanchas() != null ?
            local.getCanchas().stream().map(canchaMapper::toViewModel).collect(Collectors.toList()) : List.of();

        List<String> deportesDisponibles = canchas.stream()
            .map(CanchaViewModel::deporte)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

        return new LocalViewModel(
            local.getUuid(),
            local.getNombre(),
            getUbicacionString(local),
            deportesDisponibles,
            local.getTelefono(),
            local.getDescripcion(),
            local.getHorariosAtencion(),
            deportesDisponibles,
            canchas
        );
    }

    private String getUbicacionString(Local local) {
        if (local.getUbicacion() == null) {
            return null;
        }
        String direccion = local.getUbicacion().getDireccion();
        String localidad = local.getUbicacion().getLocalidad() != null ? local.getUbicacion().getLocalidad().getNombre() : null;
        if (direccion != null && localidad != null && !localidad.isBlank()) {
            return direccion + ", " + localidad;
        }
        return direccion;
    }
}