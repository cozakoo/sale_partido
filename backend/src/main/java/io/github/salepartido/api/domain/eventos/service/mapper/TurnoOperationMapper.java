package io.github.salepartido.api.domain.eventos.service.mapper;

import org.springframework.stereotype.Component;
import io.github.salepartido.api.domain.eventos.service.dto.TurnoOperation;
import io.github.salepartido.api.domain.locales.model.Cancha;
import io.github.salepartido.api.domain.locales.model.Turno;

@Component
public class TurnoOperationMapper {

    public Turno toTurnoEntity(TurnoOperation operation, Cancha cancha) {
        if (operation == null) return null;
        Turno turno = new Turno();
        turno.setCancha(cancha);
        turno.setFecha(operation.fecha());
        turno.setHoraInicio(operation.horaInicio());
        turno.setHoraFin(operation.horaFin());
        return turno;
    }
}
