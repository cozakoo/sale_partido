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

@Service
public class DisponibilidadService {

    private final LocalRepository localRepository;
    private final TurnoRepository turnoRepository;

    public DisponibilidadService(LocalRepository localRepository, TurnoRepository turnoRepository) {
        this.localRepository = localRepository;
        this.turnoRepository = turnoRepository;
    }

    @Transactional(readOnly = true)
    public List<DisponibilidadCanchaDTO> obtenerDisponibilidadLocal(UUID localUuid, LocalDate fechaInicio, LocalDate fechaFin) {
        // 1. Obtener Local y validar su existencia
        Local local = localRepository.findById(localUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Local no encontrado"));

        List<Cancha> canchas = local.getCanchas();
        if (canchas == null || canchas.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. Obtener los UUIDs de las canchas del local
        List<UUID> canchaUuids = canchas.stream()
                .map(Cancha::getUuid)
                .collect(Collectors.toList());

        // 3. Buscar todos los turnos correspondientes en el rango de fechas
        List<Turno> turnos = turnoRepository.findByCanchasAndDateRange(canchaUuids, fechaInicio, fechaFin);

        List<DisponibilidadCanchaDTO> disponibilidadCanchas = new ArrayList<>();

        // 4. Calcular la disponibilidad para cada cancha
        for (Cancha cancha : canchas) {
            List<TurnoSlotDTO> turnosList = new ArrayList<>();

            // Buscar configuración horaria activa
            ConfiguracionHorario configHorario = null;
            if (cancha.getConfiguracionesHorarios() != null) {
                configHorario = cancha.getConfiguracionesHorarios().stream()
                        .filter(ConfiguracionHorario::isActivo)
                        .findFirst()
                        .orElse(null);
            }

            if (configHorario != null) {
                Duration duration = configHorario.getDuracionTurno();
                
                if (duration != null && !duration.isZero() && !duration.isNegative()) {
                    // Mapear configuraciones de días de la semana para acceso rápido
                    Map<DayOfWeek, ConfiguracionDia> configDias = configHorario.getConfiguracionesDias().stream()
                            .collect(Collectors.toMap(ConfiguracionDia::getDiaSemana, d -> d));

                    LocalDate currentFecha = fechaInicio;
                    while (!currentFecha.isAfter(fechaFin)) {
                        DayOfWeek dayOfWeek = currentFecha.getDayOfWeek();
                        ConfiguracionDia configDia = configDias.get(dayOfWeek);

                        if (configDia != null) {
                            LocalTime start = configDia.getHoraInicio();
                            LocalTime end = configDia.getHoraFin();
                            LocalTime currentSlotStart = start;

                            while (currentSlotStart.plus(duration).isBefore(end) || currentSlotStart.plus(duration).equals(end)) {
                                LocalTime currentSlotEnd = currentSlotStart.plus(duration);
                                
                                // Control de desbordamiento de medianoche
                                if (currentSlotEnd.isBefore(currentSlotStart)) {
                                    break;
                                }

                                final LocalDate finalFecha = currentFecha;
                                final LocalTime finalSlotStart = currentSlotStart;
                                final LocalTime finalSlotEnd = currentSlotEnd;

                                // Comprobar si hay algún turno que solape con el slot
                                Optional<Turno> overlappingTurno = turnos.stream()
                                        .filter(t -> t.getCancha().getUuid().equals(cancha.getUuid()))
                                        .filter(t -> t.getFecha().equals(finalFecha))
                                        .filter(t -> t.getHoraInicio().isBefore(finalSlotEnd) && t.getHoraFin().isAfter(finalSlotStart))
                                        .findFirst();

                                TurnoSlotDTO turnoSlotDTO;
                                if (overlappingTurno.isPresent()) {
                                    Turno t = overlappingTurno.get();
                                    TurnoDTO tDto = new TurnoDTO(
                                            t.getUuid(),
                                            t.getNombreOrganizador(),
                                            t.getDeporte(),
                                            cancha.getCapacidad(),
                                            t.getCantidadParticipantesConfirmados(),
                                            t.getEstadoEvento()
                                    );
                                    turnoSlotDTO = new TurnoSlotDTO(finalFecha, finalSlotStart, finalSlotEnd, cancha.getNombre(), t.getDeporte(), "OCUPADO", tDto);
                                } else {
                                    String deporteCancha = cancha.getDeporte() != null ? cancha.getDeporte().getNombre() : null;
                                    turnoSlotDTO = new TurnoSlotDTO(finalFecha, finalSlotStart, finalSlotEnd, cancha.getNombre(), deporteCancha, "LIBRE", null);
                                }

                                turnosList.add(turnoSlotDTO);
                                currentSlotStart = currentSlotEnd;
                            }
                        }
                        currentFecha = currentFecha.plusDays(1);
                    }
                }
            }

            disponibilidadCanchas.add(new DisponibilidadCanchaDTO(cancha.getUuid(), cancha.getNombre(), turnosList));
        }

        return disponibilidadCanchas;
    }

}
