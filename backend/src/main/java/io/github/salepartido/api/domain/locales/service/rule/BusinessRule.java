package io.github.salepartido.api.domain.locales.service.rule;

// Interfaz genérica de reglas de validación
public interface BusinessRule<T> {
    void validate(T operation);
}