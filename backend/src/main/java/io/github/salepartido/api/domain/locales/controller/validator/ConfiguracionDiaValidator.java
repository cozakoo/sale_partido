package io.github.salepartido.api.domain.locales.controller.validator;

import io.github.salepartido.api.domain.locales.controller.dto.ConfiguracionDiaDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfiguracionDiaValidator implements ConstraintValidator<ConfiguracionDiaValida, ConfiguracionDiaDTO> {

    private final int minutoStep;

    public ConfiguracionDiaValidator(@Value("${app.limits.horario-atencion-minuto-step:30}") int minutoStep) {
        this.minutoStep = minutoStep;
    }

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

        if (dto.horaInicio().getMinute() % minutoStep != 0) {
            if (!defaultDisabled) {
                context.disableDefaultConstraintViolation();
                defaultDisabled = true;
            }
            context.buildConstraintViolationWithTemplate("Los horarios deben ser en intervalos de " + minutoStep + " minutos")
                   .addPropertyNode("horaInicio")
                   .addConstraintViolation();
            valid = false;
        }
        
        if (dto.horaFin().getMinute() % minutoStep != 0) {
            if (!defaultDisabled) {
                context.disableDefaultConstraintViolation();
                defaultDisabled = true;
            }
            context.buildConstraintViolationWithTemplate("Los horarios deben ser en intervalos de " + minutoStep + " minutos")
                   .addPropertyNode("horaFin")
                   .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}
