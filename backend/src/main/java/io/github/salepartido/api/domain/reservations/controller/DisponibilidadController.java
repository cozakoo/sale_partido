package io.github.salepartido.api.domain.reservations.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.github.salepartido.api.domain.reservations.controller.dto.DisponibilidadCanchaDTO;
import io.github.salepartido.api.domain.reservations.service.DisponibilidadService;

@RestController
@RequestMapping("/locales")
public class DisponibilidadController {

    private final DisponibilidadService disponibilidadService;

    public DisponibilidadController(DisponibilidadService disponibilidadService) {
        this.disponibilidadService = disponibilidadService;
    }

    @GetMapping("/{uuid}/disponibilidad")
    public List<DisponibilidadCanchaDTO> getDisponibilidad(
            @PathVariable UUID uuid,
            @RequestParam(name = "fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(name = "fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        if (fechaInicio == null || fechaFin == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Las fechas de inicio y fin son obligatorias");
        }

        if (fechaInicio.isAfter(fechaFin)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        long daysBetween = ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        if (daysBetween > 31) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rango de búsqueda de disponibilidad no puede exceder los 31 días");
        }

        return disponibilidadService.obtenerDisponibilidadLocal(uuid, fechaInicio, fechaFin);
    }
}
