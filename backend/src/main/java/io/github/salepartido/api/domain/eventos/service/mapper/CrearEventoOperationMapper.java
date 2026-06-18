package io.github.salepartido.api.domain.eventos.service.mapper;

import org.springframework.stereotype.Component;
import io.github.salepartido.api.domain.eventos.service.dto.CrearEventoOperation;
import io.github.salepartido.api.domain.eventos.model.Evento;
import io.github.salepartido.api.domain.eventos.model.EstadoEvento;
import io.github.salepartido.api.domain.eventos.model.TipoEvento;
import io.github.salepartido.api.domain.eventos.model.Usuario;
import io.github.salepartido.api.domain.eventos.model.NivelDeporte;
import io.github.salepartido.api.domain.locales.model.Turno;
import java.time.Duration;

@Component
public class CrearEventoOperationMapper {

    public Evento toEventoEntity(CrearEventoOperation operation, Turno turno, Usuario organizador, NivelDeporte nivelRequerido, TipoEvento tipo, Duration limiteCancelacion) {
        if (operation == null) return null;
        Evento evento = new Evento();
        evento.setNombre(operation.nombre());
        evento.setTipo(tipo);
        evento.setCupoMinimo(operation.cupoMinimo());
        evento.setCupoMaximo(operation.cupoMaximo());
        evento.setEstado(EstadoEvento.DISPONIBLE);
        evento.setLimiteCancelacionParticipacion(limiteCancelacion);
        evento.setTurno(turno);
        evento.setOrganizador(organizador);
        evento.setNivelRequerido(nivelRequerido);
        return evento;
    }
}
