package io.github.salepartido.api.domain.participation.controller.dto;

import jakarta.validation.constraints.NotNull;

public record InvitacionEstadoRequestDTO(@NotNull RespuestaInvitacion estado) {}
