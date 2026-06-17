package io.github.salepartido.api.domain.locales.controller.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DuracionTurnoValidator implements ConstraintValidator<DuracionTurnoValida, Long> {

    private final List<Long> valoresPermitidos;

    public DuracionTurnoValidator(@Value("${app.limits.duracion-turno-permitida:30,60,90,120}") List<Long> valoresPermitidos) {
        this.valoresPermitidos = valoresPermitidos;
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return valoresPermitidos.contains(value);
    }
}