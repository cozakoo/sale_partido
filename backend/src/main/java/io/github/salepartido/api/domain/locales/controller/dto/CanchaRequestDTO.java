package io.github.salepartido.api.domain.locales.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record CanchaRequestDTO(
    @NotBlank(message = "El nombre de la cancha no puede estar vacío")
    String name
) {}