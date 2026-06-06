package io.github.salepartido.api.domain.locales.controller.dto;

import java.util.UUID;

import io.github.salepartido.api.domain.locales.model.HorarioAtencion;

import java.util.List;

public record LocalDetail(
    UUID uuid,
    String nombre,
    String ubicacion,
    List<String> deportes,
    String telefono,
    String descripcion,
    List<HorarioAtencion> horario,
    List<CanchaViewModel> canchas
) {}