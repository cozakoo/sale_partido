package io.github.salepartido.api.domain.locales.controller.mapper;

import org.springframework.stereotype.Component;
import io.github.salepartido.api.domain.locales.model.Local;
import io.github.salepartido.api.domain.locales.controller.dto.*;
import io.github.salepartido.api.domain.locales.service.dto.BuscarLocalesOperation;
import io.github.salepartido.api.domain.locales.service.dto.ActualizarConfiguracionesHorariosOperation;
import io.github.salepartido.api.domain.locales.exception.FechaInvalidaException;
import io.github.salepartido.api.domain.locales.exception.HorarioInvalidoException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Objects;

@Component
public class LocalMapper {
    private final CanchaMapper canchaMapper;

    public LocalMapper(CanchaMapper canchaMapper) {
        this.canchaMapper = canchaMapper;
    }

    public LocalSummary toSummary(Local local) {
        if (local == null) return null;
        return new LocalSummary(local.getUuid(), local.getNombre());
    }

    public LocalDetail toDetail(Local local) {
        if (local == null) return null;

        List<CanchaViewModel> canchas = local.getCanchas() != null ?
            local.getCanchas().stream().map(canchaMapper::toViewModel).collect(Collectors.toList()) : List.of();

        List<String> deportes = canchas.stream()
            .map(CanchaViewModel::deporte)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

        return new LocalDetail(
            local.getUuid(),
            local.getNombre(),
            getUbicacionString(local),
            deportes,
            local.getTelefono(),
            local.getDescripcion(),
            local.getHorariosAtencion(),
            canchas
        );
    }

    public LocalViewModel toViewModel(Local local) {
        if (local == null) return null;

        List<CanchaViewModel> canchas = local.getCanchas() != null ?
            local.getCanchas().stream().map(canchaMapper::toViewModel).collect(Collectors.toList()) : List.of();

        List<String> deportesDisponibles = canchas.stream()
            .map(CanchaViewModel::deporte)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

        return new LocalViewModel(
            local.getUuid(),
            local.getNombre(),
            getUbicacionString(local),
            deportesDisponibles,
            local.getTelefono(),
            local.getDescripcion(),
            local.getHorariosAtencion(),
            deportesDisponibles,
            canchas
        );
    }

    public BuscarLocalesOperation toOperation(FiltroViewModel filtro) {
        if (filtro == null) return null;
        LocalDate fechaFiltro = parseFecha(filtro.fecha());
        LocalTime desde = parseHora(filtro.horarioDisponible() != null ? filtro.horarioDisponible().desde() : null);
        LocalTime hasta = parseHora(filtro.horarioDisponible() != null ? filtro.horarioDisponible().hasta() : null);
        return new BuscarLocalesOperation(filtro.ubicacion(), filtro.tipoDeporte(), fechaFiltro, desde, hasta);
    }

    public ActualizarConfiguracionesHorariosOperation toOperation(SaveCanchasConfiguracionesHorariosRequest request) {
        if (request == null) return null;
        List<ActualizarConfiguracionesHorariosOperation.CanchaConfiguracionOperation> canchas = request.canchas().stream()
            .map(c -> new ActualizarConfiguracionesHorariosOperation.CanchaConfiguracionOperation(
                c.canchaUuid(),
                c.duracionTurno(),
                c.configuracionesDias().stream()
                    .map(d -> new ActualizarConfiguracionesHorariosOperation.ConfiguracionDiaOperation(
                        d.diaSemana(),
                        d.horaInicio(),
                        d.horaFin()
                    )).collect(Collectors.toList())
            )).collect(Collectors.toList());
        return new ActualizarConfiguracionesHorariosOperation(canchas);
    }

    private String getUbicacionString(Local local) {
        if (local.getUbicacion() == null) {
            return null;
        }
        String direccion = local.getUbicacion().getDireccion();
        String localidad = local.getUbicacion().getLocalidad() != null ? local.getUbicacion().getLocalidad().getNombre() : null;
        if (direccion != null && localidad != null && !localidad.isBlank()) {
            return direccion + ", " + localidad;
        }
        return direccion;
    }

    private LocalDate parseFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return null;
        }

        String normalized = fecha.trim().toLowerCase();
        if (FiltroViewModel.FECHA_HOY.equals(normalized)) {
            return LocalDate.now();
        }
        if (FiltroViewModel.FECHA_MANANA.equals(normalized)) {
            return LocalDate.now().plusDays(1);
        }

        try {
            return LocalDate.parse(normalized);
        } catch (Exception ex) {
            throw new FechaInvalidaException(fecha);
        }
    }

    private LocalTime parseHora(String hora) {
        if (hora == null || hora.isBlank()) {
            return null;
        }

        String normalized = hora.trim().replace("hs", "").replace("HS", "").trim();
        if (normalized.endsWith(".") || normalized.endsWith("h")) {
            normalized = normalized.substring(0, normalized.length() - 1).trim();
        }

        try {
            return LocalTime.parse(normalized);
        } catch (Exception ex) {
            throw new HorarioInvalidoException(hora);
        }
    }
}