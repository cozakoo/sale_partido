package io.github.salepartido.api.domain.locales.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public record CanchaRequestDTO(
    @NotBlank(message = "El nombre de la cancha no puede estar vacío")
    String name,
    @NotNull(message = "La capacidad no puede ser nula")
    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    Integer capacidad
) {}