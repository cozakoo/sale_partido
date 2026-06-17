package io.github.salepartido.api.domain.locales.controller.mapper;

import org.springframework.stereotype.Component;
import io.github.salepartido.api.domain.locales.model.Cancha;
import io.github.salepartido.api.domain.locales.model.ConfiguracionHorario;
import io.github.salepartido.api.domain.locales.model.ConfiguracionDia;
import io.github.salepartido.api.domain.locales.controller.dto.CanchaDetail;
import io.github.salepartido.api.domain.locales.controller.dto.CanchaSummary;
import io.github.salepartido.api.domain.locales.controller.dto.CanchaViewModel;
import io.github.salepartido.api.domain.locales.controller.dto.ConfiguracionDiaDTO;
import io.github.salepartido.api.domain.locales.controller.dto.ConfiguracionHorarioDTO;
import io.github.salepartido.api.domain.locales.controller.dto.CanchaRequestDTO;
import io.github.salepartido.api.domain.locales.service.dto.CrearCanchaOperation;
import io.github.salepartido.api.domain.locales.service.dto.ActualizarCanchaOperation;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CanchaMapper {

    public CrearCanchaOperation toOperation(CanchaRequestDTO request) {
        if (request == null) return null;
        return new CrearCanchaOperation(request.name(), request.capacidad());
    }

    public ActualizarCanchaOperation toActualizarOperation(CanchaRequestDTO request) {
        if (request == null) return null;
        return new ActualizarCanchaOperation(request.name(), request.capacidad());
    }

    public CanchaSummary toSummary(Cancha cancha) {
        if (cancha == null) return null;
        return new CanchaSummary(cancha.getUuid(), cancha.getNombre(), cancha.getCapacidad());
    }

    public CanchaViewModel toViewModel(Cancha cancha) {
        if (cancha == null) return null;
        return new CanchaViewModel(
            cancha.getUuid(),
            cancha.getNombre(),
            cancha.getDeporte() != null ? cancha.getDeporte().getNombre() : null,
            cancha.getCapacidad(),
            cancha.getConfiguracionesHorarios() != null ?
                cancha.getConfiguracionesHorarios().stream().map(this::toConfiguracionHorarioDTO).collect(Collectors.toList()) : List.of()
        );
    }

    public CanchaDetail toDetail(Cancha cancha) {
        if (cancha == null) return null;
        return new CanchaDetail(
            cancha.getUuid(),
            cancha.getNombre(),
            cancha.getCapacidad(),
            cancha.getConfiguracionesHorarios() != null ?
                cancha.getConfiguracionesHorarios().stream().map(this::toConfiguracionHorarioDTO).collect(Collectors.toList()) : List.of()
        );
    }

    public ConfiguracionHorarioDTO toConfiguracionHorarioDTO(ConfiguracionHorario configuracion) {
        if (configuracion == null) return null;
        return new ConfiguracionHorarioDTO(
            configuracion.isActivo(),
            configuracion.getDuracionTurno() != null ? configuracion.getDuracionTurno().toMinutes() : null,
            configuracion.getConfiguracionesDias() != null ?
                configuracion.getConfiguracionesDias().stream().map(this::toConfiguracionDiaDTO).collect(Collectors.toList()) : List.of()
        );
    }

    public ConfiguracionDiaDTO toConfiguracionDiaDTO(ConfiguracionDia dia) {
        if (dia == null) return null;
        return new ConfiguracionDiaDTO(dia.getDiaSemana(), dia.getHoraInicio(), dia.getHoraFin());
    }
}