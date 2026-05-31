package io.github.salepartido.api.domain.locales.controller.dto;

import java.util.List;
import java.util.UUID;

import io.github.salepartido.api.domain.locales.model.HorarioAtencion;

public record LocalViewModel(
    UUID uuid,
    String nombre,
    String ubicacion,
    List<String> deportes,
    String telefono,
    String descripcion,
    List<HorarioAtencion> horario,
    List<String> deportesDisponibles,
    List<CanchaViewModel> canchas
) {}