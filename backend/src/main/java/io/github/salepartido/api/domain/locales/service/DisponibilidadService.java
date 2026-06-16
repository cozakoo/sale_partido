package io.github.salepartido.api.domain.locales.service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import io.github.salepartido.api.domain.locales.model.Cancha;
import io.github.salepartido.api.domain.locales.model.ConfiguracionDia;
import io.github.salepartido.api.domain.locales.model.ConfiguracionHorario;
import io.github.salepartido.api.domain.locales.model.Local;
import io.github.salepartido.api.domain.locales.model.Turno;
import io.github.salepartido.api.domain.locales.repository.LocalRepository;
import io.github.salepartido.api.domain.locales.repository.TurnoRepository;
import io.github.salepartido.api.domain.locales.controller.dto.DisponibilidadCanchaDTO;
import io.github.salepartido.api.domain.locales.controller.dto.TurnoDTO;
import io.github.salepartido.api.domain.locales.controller.dto.TurnoSlotDTO;
import io.github.salepartido.api.domain.participation.repository.EventoRepository;
import io.github.salepartido.api.domain.participation.model.Evento;
import io.github.salepartido.api.domain.participation.model.EstadoParticipacion;

@Service
public class DisponibilidadService {

    private final LocalRepository localRepository;
    private final TurnoRepository turnoRepository;
    private final EventoRepository eventoRepository;

    public DisponibilidadService(LocalRepository localRepository, TurnoRepository turnoRepository, EventoRepository eventoRepository) {
        this.localRepository = localRepository;
        this.turnoRepository = turnoRepository;
        this.eventoRepository = eventoRepository;
    }

    @Transactional(readOnly = true)
    public List<DisponibilidadCanchaDTO> obtenerDisponibilidadLocal(UUID localUuid, LocalDate fechaInicio,
            LocalDate fechaFin) {
        Local local = localRepository.findById(localUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Local no encontrado"));

        List<Cancha> canchas = local.getCanchas();
        if (canchas == null || canchas.isEmpty()) {
            return new ArrayList<>();
        }

        List<UUID> canchaUuids = canchas.stream()
                .map(Cancha::getUuid)
                .collect(Collectors.toList());

        List<Turno> turnos = turnoRepository.findByCanchasAndDateRange(canchaUuids, fechaInicio, fechaFin);

        return canchas.stream()
                .map(cancha -> new DisponibilidadCanchaDTO(
                        cancha.getUuid(),
                        cancha.getNombre(),
                        cancha.getCapacidad(), // ← nuevo
                        buildTurnoSlotsForCancha(cancha, fechaInicio, fechaFin, turnos)))
                .collect(Collectors.toList());
    }

    private List<TurnoSlotDTO> buildTurnoSlotsForCancha(Cancha cancha, LocalDate fechaInicio, LocalDate fechaFin,
            List<Turno> turnos) {
        ConfiguracionHorario configHorario = findActiveConfiguracionHorario(cancha);
        if (configHorario == null) {
            return new ArrayList<>();
        }

        Duration duration = configHorario.getDuracionTurno();
        if (!isValidDuration(duration)) {
            return new ArrayList<>();
        }

        Map<DayOfWeek, ConfiguracionDia> configDias = buildConfigDiasMap(configHorario);
        return buildSlotList(cancha, fechaInicio, fechaFin, duration, configDias, turnos);
    }

    private ConfiguracionHorario findActiveConfiguracionHorario(Cancha cancha) {
        if (cancha.getConfiguracionesHorarios() == null) {
            return null;
        }
        return cancha.getConfiguracionesHorarios().stream()
                .filter(ConfiguracionHorario::isActivo)
                .findFirst()
                .orElse(null);
    }

    private boolean isValidDuration(Duration duration) {
        return duration != null && !duration.isZero() && !duration.isNegative();
    }

    private Map<DayOfWeek, ConfiguracionDia> buildConfigDiasMap(ConfiguracionHorario configHorario) {
        return configHorario.getConfiguracionesDias().stream()
                .collect(Collectors.toMap(ConfiguracionDia::getDiaSemana, d -> d));
    }

    private List<TurnoSlotDTO> buildSlotList(Cancha cancha, LocalDate fechaInicio, LocalDate fechaFin,
            Duration duration,
            Map<DayOfWeek, ConfiguracionDia> configDias, List<Turno> turnos) {
        List<TurnoSlotDTO> slotList = new ArrayList<>();

        for (LocalDate currentFecha = fechaInicio; !currentFecha.isAfter(fechaFin); currentFecha = currentFecha
                .plusDays(1)) {
            ConfiguracionDia configDia = configDias.get(currentFecha.getDayOfWeek());
            if (configDia == null) {
                continue;
            }
            slotList.addAll(buildSlotsForDay(cancha, currentFecha, duration, configDia, turnos));
        }

        return slotList;
    }

    private List<TurnoSlotDTO> buildSlotsForDay(Cancha cancha, LocalDate fecha, Duration duration,
            ConfiguracionDia configDia, List<Turno> turnos) {
        List<TurnoSlotDTO> slots = new ArrayList<>();

        LocalTime currentSlotStart = configDia.getHoraInicio();
        LocalTime end = configDia.getHoraFin();

        while (!currentSlotStart.plus(duration).isAfter(end)) {
            LocalTime currentSlotEnd = currentSlotStart.plus(duration);
            if (currentSlotEnd.isBefore(currentSlotStart)) {
                break;
            }

            Optional<Turno> overlappingTurno = findOverlappingTurno(cancha.getUuid(), fecha, currentSlotStart,
                    currentSlotEnd, turnos);
            slots.add(buildTurnoSlotDTO(cancha, fecha, currentSlotStart, currentSlotEnd, overlappingTurno));
            currentSlotStart = currentSlotEnd;
        }

        return slots;
    }

    private Optional<Turno> findOverlappingTurno(UUID canchaUuid, LocalDate fecha, LocalTime slotStart,
            LocalTime slotEnd, List<Turno> turnos) {
        return turnos.stream()
                .filter(t -> t.getCancha().getUuid().equals(canchaUuid))
                .filter(t -> t.getFecha().equals(fecha))
                .filter(t -> t.getHoraInicio().isBefore(slotEnd) && t.getHoraFin().isAfter(slotStart))
                .findFirst();
    }

    private TurnoSlotDTO buildTurnoSlotDTO(Cancha cancha, LocalDate fecha, LocalTime slotStart,
            LocalTime slotEnd, Optional<Turno> turnoOpt) {
        if (turnoOpt.isPresent()) {
            Turno t = turnoOpt.get();
            Optional<Evento> eventoOpt = eventoRepository.findByTurnoUuid(t.getUuid());

            String nombreOrganizador = "";
            String deporte = cancha.getDeporte() != null ? cancha.getDeporte().getNombre() : "";
            int cantidadParticipantesConfirmados = 0;
            String estadoEvento = "";

            if (eventoOpt.isPresent()) {
                Evento e = eventoOpt.get();
                if (e.getOrganizador() != null) {
                    nombreOrganizador = e.getOrganizador().getNombre();
                }
                if (e.getNivelRequerido() != null && e.getNivelRequerido().getDeporte() != null) {
                    deporte = e.getNivelRequerido().getDeporte().getNombre();
                } else if (cancha.getDeporte() != null) {
                    deporte = cancha.getDeporte().getNombre();
                }
                cantidadParticipantesConfirmados = (int) e.getParticipaciones().stream()
                        .filter(p -> p.getEstado() == EstadoParticipacion.CONFIRMADO)
                        .count();
                if (e.getEstado() != null) {
                    estadoEvento = e.getEstado().name();
                }
            }

            TurnoDTO turnoDTO = new TurnoDTO(
                    t.getUuid(),
                    nombreOrganizador,
                    deporte,
                    cancha.getCapacidad(),
                    cantidadParticipantesConfirmados,
                    estadoEvento);
            return new TurnoSlotDTO(fecha, slotStart, slotEnd, cancha.getNombre(), deporte, "OCUPADO", turnoDTO);
        }

        String deporteCancha = cancha.getDeporte() != null ? cancha.getDeporte().getNombre() : null;
        return new TurnoSlotDTO(fecha, slotStart, slotEnd, cancha.getNombre(), deporteCancha, "LIBRE", null);
    }

}
