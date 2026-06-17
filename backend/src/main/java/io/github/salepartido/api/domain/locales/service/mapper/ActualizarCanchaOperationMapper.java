package io.github.salepartido.api.domain.locales.service.mapper;

import org.springframework.stereotype.Component;
import io.github.salepartido.api.domain.locales.model.Cancha;
import io.github.salepartido.api.domain.locales.service.dto.ActualizarCanchaOperation;

@Component
public class ActualizarCanchaOperationMapper {

    public void updateEntity(ActualizarCanchaOperation operation, Cancha entity) {
        if (operation == null || entity == null) return;
        entity.setNombre(operation.name());
        entity.setCapacidad(operation.capacidad());
    }
}
