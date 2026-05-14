package io.github.salepartido.api.domain.locales.controller.mapper;

import org.springframework.stereotype.Component;
import io.github.salepartido.api.domain.locales.model.Cancha;
import io.github.salepartido.api.domain.locales.model.ConfiguracionHorario;
import io.github.salepartido.api.domain.locales.model.ConfiguracionDia;
import io.github.salepartido.api.domain.locales.controller.dto.CanchaDetail;
import io.github.salepartido.api.domain.locales.controller.dto.CanchaSummary;
import io.github.salepartido.api.domain.locales.controller.dto.ConfiguracionDiaDTO;
import io.github.salepartido.api.domain.locales.controller.dto.ConfiguracionHorarioDTO;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CanchaMapper {

    public CanchaSummary toSummary(Cancha cancha) {
        if (cancha == null) return null;
        return new CanchaSummary(cancha.getUuid(), cancha.getNombre());
    }

    public CanchaDetail toDetail(Cancha cancha) {
        if (cancha == null) return null;
        return new CanchaDetail(
            cancha.getUuid(),
            cancha.getNombre(),
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
        // En caso que desde backend no manejes estado 'activo' por cada día suelto, pero frontend lo requiera:
        return new ConfiguracionDiaDTO(dia.getDiaSemana(), dia.getHoraInicio(), dia.getHoraFin(), true);
    }
}