package io.github.salepartido.api.domain.locales.service.mapper;

import org.springframework.stereotype.Component;
import io.github.salepartido.api.domain.locales.model.Cancha;
import io.github.salepartido.api.domain.locales.service.dto.CrearCanchaOperation;

@Component
public class CrearCanchaOperationMapper {

    public Cancha toEntity(CrearCanchaOperation operation) {
        if (operation == null) return null;
        Cancha cancha = new Cancha();
        cancha.setNombre(operation.name());
        cancha.setCapacidad(operation.capacidad());
        return cancha;
    }
}
