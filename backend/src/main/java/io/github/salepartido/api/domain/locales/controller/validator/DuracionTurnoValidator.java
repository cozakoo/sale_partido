package io.github.salepartido.api.domain.locales.controller.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class DuracionTurnoValidator implements ConstraintValidator<DuracionTurnoValida, Long> {

    private static final List<Long> VALORES_PERMITIDOS = Arrays.asList(30L, 60L, 90L, 120L);

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return VALORES_PERMITIDOS.contains(value);
    }
}