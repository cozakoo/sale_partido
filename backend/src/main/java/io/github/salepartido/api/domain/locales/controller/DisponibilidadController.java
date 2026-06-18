package io.github.salepartido.api.domain.locales.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;

import io.github.salepartido.api.domain.locales.controller.dto.DisponibilidadCanchaDTO;
import io.github.salepartido.api.domain.locales.controller.mapper.DisponibilidadMapper;
import io.github.salepartido.api.domain.locales.service.DisponibilidadService;
import io.github.salepartido.api.domain.locales.service.dto.CanchaDisponibilidad;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/locales")
@Tag(name = "Disponibilidad", description = "Consulta de disponibilidad de canchas")
public class DisponibilidadController {

    private final DisponibilidadService disponibilidadService;
    private final DisponibilidadMapper disponibilidadMapper;
    private final int maxDiasBusqueda;

    public DisponibilidadController(
            DisponibilidadService disponibilidadService,
            DisponibilidadMapper disponibilidadMapper,
            @Value("${app.limits.disponibilidad-max-dias:31}") int maxDiasBusqueda) {
        this.disponibilidadService = disponibilidadService;
        this.disponibilidadMapper = disponibilidadMapper;
        this.maxDiasBusqueda = maxDiasBusqueda;
    }

    @GetMapping("/{uuid}/disponibilidad")
    @Operation(summary = "Obtener disponibilidad de un local", description = "Retorna los turnos disponibles y ocupados de todas las canchas de un local en un rango de fechas (máx. 31 días).")
    @ApiResponse(responseCode = "200", description = "Lista de canchas con sus turnos", content = @Content(array = @ArraySchema(schema = @Schema(implementation = DisponibilidadCanchaDTO.class))))
    @ApiResponse(responseCode = "400", description = "Parámetros de fecha inválidos")
    @ApiResponse(responseCode = "404", description = "Local no encontrado")
    public List<DisponibilidadCanchaDTO> getDisponibilidad(
            @PathVariable @Parameter(description = "UUID del local") UUID uuid,
            @RequestParam(name = "fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "Fecha de inicio (ISO 8601)", example = "2026-06-01") LocalDate fechaInicio,
            @RequestParam(name = "fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "Fecha de fin (ISO 8601)", example = "2026-06-07") LocalDate fechaFin) {

        if (fechaInicio == null || fechaFin == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Las fechas de inicio y fin son obligatorias");
        }

        if (fechaInicio.isAfter(fechaFin)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        long daysBetween = ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        if (daysBetween > maxDiasBusqueda) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El rango de búsqueda de disponibilidad no puede exceder los " + maxDiasBusqueda + " días");
        }

        List<CanchaDisponibilidad> availability = disponibilidadService.obtenerDisponibilidadLocal(uuid, fechaInicio, fechaFin);
        return availability.stream()
                .map(disponibilidadMapper::toDTO)
                .collect(Collectors.toList());
    }
}
