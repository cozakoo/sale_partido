package io.github.salepartido.api.domain.locales.controller.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = ConfiguracionDiaValidator.class)
@Documented
public @interface ConfiguracionDiaValida {
    String message() default "La configuración del día es inválida";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}