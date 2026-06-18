package io.github.salepartido.api.domain.eventos.controller.dto;

import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CrearEventoRequestDTO(
        @NotNull @Valid TurnoRequestDTO turno,
        @NotNull UUID organizadorUuid,
        @NotBlank String nombre,
        @NotNull @Min(1) Integer cupoMinimo,
        @NotNull @Min(1) Integer cupoMaximo,
        String tipo,
        Integer limiteCancelacionParticipacion,
        UUID nivelRequeridoUuid) {

}
