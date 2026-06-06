package io.github.salepartido.api.domain.locales.controller.dto;

public record FiltroViewModel(
    String ubicacion,
    String fecha,
    String tipoDeporte,
    HorarioDisponibleViewModel horarioDisponible
) {

    /**
     * El campo fecha puede recibir valores representativos como:
     * - "hoy"
     * - "mañana"
     * - una fecha ISO específica como "2026-06-15"
     */
    public static final String FECHA_HOY = "hoy";
    public static final String FECHA_MANANA = "mañana";
}