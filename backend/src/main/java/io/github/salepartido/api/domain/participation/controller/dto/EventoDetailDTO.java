package io.github.salepartido.api.domain.participation.controller.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import io.github.salepartido.api.domain.locales.model.Local;
import io.github.salepartido.api.domain.locales.model.Turno;
import io.github.salepartido.api.domain.participation.model.EstadoParticipacion;
import io.github.salepartido.api.domain.participation.model.Evento;

public record EventoDetailDTO(
        UUID uuid,
        String nombre,
        String deporte,
        String descripcion,
        String tipo,
        String estado,
        Integer cupoMinimo,
        Integer cupoMaximo,
        Integer participantesConfirmados,
        List<ParticipanteDTO> participantes,
        NivelRequeridoDTO nivelRequerido,
        TurnoDetalleDTO turno) {

    public record ParticipanteDTO(UUID uuid, String nombre) {}

    public record NivelRequeridoDTO(UUID uuid, String nombre, Integer orden, String deporte) {}

    public record TurnoDetalleDTO(
            UUID uuid,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            CanchaDetalleDTO cancha,
            LocalDetalleDTO local) {}

    public record CanchaDetalleDTO(UUID uuid, String nombre) {}

    public record LocalDetalleDTO(UUID uuid, String nombre, String direccion) {}

    public static EventoDetailDTO from(Evento evento, Local local) {
        String deporteName = evento.getTurno() != null && evento.getTurno().getCancha() != null
                && evento.getTurno().getCancha().getDeporte() != null
                ? evento.getTurno().getCancha().getDeporte().getNombre()
                : null;

        int confirmados = (int) evento.getParticipaciones().stream()
                .filter(p -> p.getEstado() == EstadoParticipacion.CONFIRMADO)
                .count();

        List<ParticipanteDTO> participantesList = evento.getParticipaciones().stream()
                .filter(p -> p.getEstado() == EstadoParticipacion.CONFIRMADO)
                .map(p -> new ParticipanteDTO(p.getParticipante().getUuid(), p.getParticipante().getNombre()))
                .toList();

        NivelRequeridoDTO nivelDto = null;
        if (evento.getNivelRequerido() != null) {
            nivelDto = new NivelRequeridoDTO(
                    evento.getNivelRequerido().getUuid(),
                    evento.getNivelRequerido().getNombre(),
                    evento.getNivelRequerido().getOrden(),
                    evento.getNivelRequerido().getDeporte() != null ? evento.getNivelRequerido().getDeporte().getNombre() : null);
        }

        TurnoDetalleDTO turnoDto = null;
        if (evento.getTurno() != null) {
            Turno t = evento.getTurno();
            CanchaDetalleDTO canchaDto = null;
            if (t.getCancha() != null) {
                canchaDto = new CanchaDetalleDTO(t.getCancha().getUuid(), t.getCancha().getNombre());
            }

            LocalDetalleDTO localDto = null;
            if (local != null) {
                String direccionCompleta = "";
                if (local.getUbicacion() != null) {
                    direccionCompleta = local.getUbicacion().getDireccion();
                    if (local.getUbicacion().getLocalidad() != null) {
                        direccionCompleta += ", " + local.getUbicacion().getLocalidad().getNombre();
                    }
                }
                localDto = new LocalDetalleDTO(local.getUuid(), local.getNombre(), direccionCompleta);
            }

            turnoDto = new TurnoDetalleDTO(
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

    public static EventoDetailDTO from(Evento evento) {
        return from(evento, null);
    }
}
