package io.github.salepartido.api.domain.eventos.controller.mapper;

import org.springframework.stereotype.Component;
import io.github.salepartido.api.domain.eventos.controller.dto.CrearEventoRequestDTO;
import io.github.salepartido.api.domain.eventos.controller.dto.EventoDetailDTO;
import io.github.salepartido.api.domain.eventos.controller.dto.TurnoRequestDTO;
import io.github.salepartido.api.domain.eventos.model.Evento;
import io.github.salepartido.api.domain.eventos.model.EstadoParticipacion;
import io.github.salepartido.api.domain.locales.model.Local;
import io.github.salepartido.api.domain.locales.model.Turno;
import io.github.salepartido.api.domain.eventos.service.dto.CrearEventoOperation;
import io.github.salepartido.api.domain.eventos.service.dto.TurnoOperation;

import java.time.Duration;
import java.util.List;

@Component
public class EventoMapper {

    public TurnoOperation toOperation(TurnoRequestDTO dto) {
        if (dto == null) return null;
        return new TurnoOperation(dto.fecha(), dto.horaInicio(), dto.horaFin(), dto.canchaUuid());
    }

    public CrearEventoOperation toOperation(CrearEventoRequestDTO dto) {
        if (dto == null) return null;
        Duration limite = dto.limiteCancelacionParticipacion() != null
                ? Duration.ofMinutes(dto.limiteCancelacionParticipacion())
                : null;
        TurnoOperation turnoOp = toOperation(dto.turno());

        return new CrearEventoOperation(
                turnoOp,
                dto.organizadorUuid(),
                dto.nombre(),
                dto.cupoMinimo(),
                dto.cupoMaximo(),
                dto.tipo() != null ? io.github.salepartido.api.domain.eventos.model.TipoEvento.valueOf(dto.tipo()) : null,
                limite,
                dto.nivelRequeridoUuid()
        );
    }

    public EventoDetailDTO toDetailDTO(Evento evento, Local local) {
        if (evento == null) return null;

        String deporteName = evento.getTurno() != null && evento.getTurno().getCancha() != null
                && evento.getTurno().getCancha().getDeporte() != null
                ? evento.getTurno().getCancha().getDeporte().getNombre()
                : null;

        int confirmados = (int) evento.getParticipaciones().stream()
                .filter(p -> p.getEstado() == EstadoParticipacion.CONFIRMADO)
                .count();

        List<EventoDetailDTO.ParticipanteDTO> participantesList = evento.getParticipaciones().stream()
                .filter(p -> p.getEstado() == EstadoParticipacion.CONFIRMADO)
                .map(p -> new EventoDetailDTO.ParticipanteDTO(p.getParticipante().getUuid(), p.getParticipante().getNombre()))
                .toList();

        EventoDetailDTO.NivelRequeridoDTO nivelDto = null;
        if (evento.getNivelRequerido() != null) {
            nivelDto = new EventoDetailDTO.NivelRequeridoDTO(
                    evento.getNivelRequerido().getUuid(),
                    evento.getNivelRequerido().getNombre(),
                    evento.getNivelRequerido().getOrden(),
                    evento.getNivelRequerido().getDeporte() != null ? evento.getNivelRequerido().getDeporte().getNombre() : null);
        }

        EventoDetailDTO.TurnoDetalleDTO turnoDto = null;
        if (evento.getTurno() != null) {
            Turno t = evento.getTurno();
            EventoDetailDTO.CanchaDetalleDTO canchaDto = null;
            if (t.getCancha() != null) {
                canchaDto = new EventoDetailDTO.CanchaDetalleDTO(t.getCancha().getUuid(), t.getCancha().getNombre());
            }

            EventoDetailDTO.LocalDetalleDTO localDto = null;
            if (local != null) {
                String direccionCompleta = "";
                if (local.getUbicacion() != null) {
                    direccionCompleta = local.getUbicacion().getDireccion();
                    if (local.getUbicacion().getLocalidad() != null) {
                        direccionCompleta += ", " + local.getUbicacion().getLocalidad().getNombre();
                    }
                }
                localDto = new EventoDetailDTO.LocalDetalleDTO(local.getUuid(), local.getNombre(), direccionCompleta);
            }

            turnoDto = new EventoDetailDTO.TurnoDetalleDTO(
                    t.getUuid(),
                    t.getFecha(),
                    t.getHoraInicio(),
                    t.getHoraFin(),
                    canchaDto,
                    localDto);
        }

        return new EventoDetailDTO(
                evento.getUuid(),
                evento.getNombre(),
                deporteName,
                null, // descripción no modelada en la entidad Evento
                evento.getTipo() != null ? evento.getTipo().name() : null,
                evento.getEstado() != null ? evento.getEstado().name() : null,
                evento.getCupoMinimo(),
                evento.getCupoMaximo(),
                confirmados,
                participantesList,
                nivelDto,
                turnoDto);
    }

    public EventoDetailDTO toDetailDTO(Evento evento) {
        return toDetailDTO(evento, null);
    }
}
