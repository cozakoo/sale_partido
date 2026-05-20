package io.github.salepartido.api.domain.locales.controller.mapper;

import org.springframework.stereotype.Component;
import io.github.salepartido.api.domain.locales.model.Local;
import io.github.salepartido.api.domain.locales.controller.dto.LocalDetail;
import io.github.salepartido.api.domain.locales.controller.dto.LocalSummary;

import java.util.stream.Collectors;
import java.util.List;

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
        return new LocalDetail(local.getUuid(), local.getNombre(), local.getCanchas() != null ? 
            local.getCanchas().stream().map(canchaMapper::toSummary).collect(Collectors.toList()) : List.of()
        );
    }
}