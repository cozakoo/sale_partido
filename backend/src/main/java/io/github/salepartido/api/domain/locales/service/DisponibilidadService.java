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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.salepartido.api.domain.eventos.model.EstadoParticipacion;
import io.github.salepartido.api.domain.eventos.model.Evento;
import io.github.salepartido.api.domain.eventos.repository.EventoRepository;
import io.github.salepartido.api.domain.locales.exception.LocalNoEncontradoException;
import io.github.salepartido.api.domain.locales.model.Cancha;
import io.github.salepartido.api.domain.locales.model.ConfiguracionDia;
import io.github.salepartido.api.domain.locales.model.ConfiguracionHorario;
import io.github.salepartido.api.domain.locales.model.Local;
import io.github.salepartido.api.domain.locales.model.Turno;
import io.github.salepartido.api.domain.locales.repository.LocalRepository;
import io.github.salepartido.api.domain.locales.repository.TurnoRepository;
import io.github.salepartido.api.domain.locales.service.dto.CanchaDisponibilidad;
import io.github.salepartido.api.domain.locales.service.dto.TurnoInfo;
import io.github.salepartido.api.domain.locales.service.dto.TurnoSlot;

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
    public List<CanchaDisponibilidad> obtenerDisponibilidadLocal(UUID localUuid, LocalDate fechaInicio,
            LocalDate fechaFin) {
        Local local = localRepository.findById(localUuid)
                .orElseThrow(LocalNoEncontradoException::new);

        List<Cancha> canchas = local.getCanchas();
        if (canchas == null || canchas.isEmpty()) {
            return new ArrayList<>();
        }

        List<UUID> canchaUuids = canchas.stream()
                .map(Cancha::getUuid)
                .collect(Collectors.toList());

        List<Turno> turnos = turnoRepository.findByCanchasAndDateRange(canchaUuids, fechaInicio, fechaFin);

        return canchas.stream()
                .map(cancha -> new CanchaDisponibilidad(
                        cancha.getUuid(),
                        cancha.getNombre(),
                        cancha.getCapacidad(),
                        buildTurnoSlotsForCancha(cancha, fechaInicio, fechaFin, turnos)))
                .collect(Collectors.toList());
    }

    private List<TurnoSlot> buildTurnoSlotsForCancha(Cancha cancha, LocalDate fechaInicio, LocalDate fechaFin,
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

    private List<TurnoSlot> buildSlotList(Cancha cancha, LocalDate fechaInicio, LocalDate fechaFin,
            Duration duration,
            Map<DayOfWeek, ConfiguracionDia> configDias, List<Turno> turnos) {
        List<TurnoSlot> slotList = new ArrayList<>();

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

    private List<TurnoSlot> buildSlotsForDay(Cancha cancha, LocalDate fecha, Duration duration,
            ConfiguracionDia configDia, List<Turno> turnos) {
        List<TurnoSlot> slots = new ArrayList<>();

        LocalTime currentSlotStart = configDia.getHoraInicio();
        LocalTime end = configDia.getHoraFin();

        while (!currentSlotStart.plus(duration).isAfter(end)) {
            LocalTime currentSlotEnd = currentSlotStart.plus(duration);
            if (currentSlotEnd.isBefore(currentSlotStart)) {
                break;
            }

            Turno overlappingTurno = findOverlappingTurno(cancha.getUuid(), fecha, currentSlotStart,
                    currentSlotEnd, turnos);
            slots.add(buildTurnoSlot(cancha, fecha, currentSlotStart, currentSlotEnd, overlappingTurno));
            currentSlotStart = currentSlotEnd;
        }

        return slots;
    }

    private Turno findOverlappingTurno(UUID canchaUuid, LocalDate fecha, LocalTime slotStart,
            LocalTime slotEnd, List<Turno> turnos) {
        Optional<Turno> turnoOpt = turnos.stream()
                .filter(t -> t.getCancha().getUuid().equals(canchaUuid))
                .filter(t -> t.getFecha().equals(fecha))
                .filter(t -> t.getHoraInicio().isBefore(slotEnd) && t.getHoraFin().isAfter(slotStart))
                .findFirst();
        return turnoOpt.orElse(null);
    }

    private TurnoSlot buildTurnoSlot(Cancha cancha, LocalDate fecha, LocalTime slotStart,
            LocalTime slotEnd, Turno t) {
        Evento e = (t == null) ? null : eventoRepository.findByTurnoUuid(t.getUuid()).orElse(null); // Sólo consulta si el turno existe
        return new TurnoSlot(
            fecha,
            slotStart,
            slotEnd,
            cancha.getNombre(),
            logicaObtencionDeporte(cancha, e),
            (t == null) ? "LIBRE" : "OCUPADO",
            (t == null) ? null : buildTurnoInfo(t, cancha, e)
        );
    }

    private TurnoInfo buildTurnoInfo(Turno t, Cancha cancha, Evento e){
        int cantidadConfirmados = (e == null)
            ? 0
            : (int) e.getParticipaciones()
                .stream()
                .filter(p -> p.getEstado() == EstadoParticipacion.CONFIRMADO)
                .count();
        return new TurnoInfo(
            t.getUuid(),
            (e != null && e.getOrganizador() != null) ? e.getOrganizador().getNombre() : "", // Nombre del organizador
            logicaObtencionDeporte(cancha, e), // Nombre del deporte
            cancha.getCapacidad(),
            cantidadConfirmados,
            e != null && e.getEstado() != null ? e.getEstado().name() : "" // Estado del evento
        );
    }

    private String logicaObtencionDeporte(Cancha cancha, Evento e){
        // Si el turno, el evento, el nivel requerido y el deporte del mismo existen, retorna el nombre de dicho deporte
        if (e != null && e.getNivelRequerido() != null && e.getNivelRequerido().getDeporte() != null) {
            return e.getNivelRequerido().getDeporte().getNombre();
        }
        // Sino, retorna el nombre del deporte de la cancha
        if (cancha.getDeporte() != null){
            return cancha.getDeporte().getNombre();
        }
        return ""; // O nada.
    }
}
