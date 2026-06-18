package io.github.salepartido.api.domain.eventos.controller.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

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
}
