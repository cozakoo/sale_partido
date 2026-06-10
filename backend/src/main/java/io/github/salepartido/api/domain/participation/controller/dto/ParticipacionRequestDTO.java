package io.github.salepartido.api.domain.participation.controller.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record ParticipacionRequestDTO(@NotNull UUID usuarioUuid) {}
