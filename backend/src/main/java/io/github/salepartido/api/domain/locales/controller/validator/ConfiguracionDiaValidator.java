package io.github.salepartido.api.domain.locales.controller.validator;

import io.github.salepartido.api.domain.locales.controller.dto.ConfiguracionDiaDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ConfiguracionDiaValidator implements ConstraintValidator<ConfiguracionDiaValida, ConfiguracionDiaDTO> {

    @Override
    public boolean isValid(ConfiguracionDiaDTO dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        if (dto.horaInicio() == null || dto.horaFin() == null) {
            return true;
        }

        boolean valid = true;
        boolean defaultDisabled = false;

        if (!dto.horaInicio().isBefore(dto.horaFin())) {
            context.disableDefaultConstraintViolation();
            defaultDisabled = true;
            context.buildConstraintViolationWithTemplate("La hora de inicio debe ser anterior a la hora de fin")
                   .addPropertyNode("horaInicio")
                   .addConstraintViolation();
            valid = false;
        }

        if (dto.horaInicio().getMinute() % 30 != 0) {
            if (!defaultDisabled) {
                context.disableDefaultConstraintViolation();
                defaultDisabled = true;
            }
            context.buildConstraintViolationWithTemplate("Los horarios deben ser en intervalos de 30 minutos")
                   .addPropertyNode("horaInicio")
                   .addConstraintViolation();
            valid = false;
        }
        
        if (dto.horaFin().getMinute() % 30 != 0) {
            if (!defaultDisabled) {
                context.disableDefaultConstraintViolation();
                defaultDisabled = true;
            }
            context.buildConstraintViolationWithTemplate("Los horarios deben ser en intervalos de 30 minutos")
                   .addPropertyNode("horaFin")
                   .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}
