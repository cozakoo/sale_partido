package io.github.salepartido.api.domain.reservations.service;

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
import io.github.salepartido.api.domain.locales.repository.LocalRepository;
import io.github.salepartido.api.domain.reservations.controller.dto.DisponibilidadCanchaDTO;
import io.github.salepartido.api.domain.reservations.controller.dto.ReservaDTO;
import io.github.salepartido.api.domain.reservations.controller.dto.TurnoDTO;
import io.github.salepartido.api.domain.reservations.model.Reserva;
import io.github.salepartido.api.domain.reservations.repository.ReservaRepository;

@Service
public class DisponibilidadService {

    private final LocalRepository localRepository;
    private final ReservaRepository reservaRepository;

    public DisponibilidadService(LocalRepository localRepository, ReservaRepository reservaRepository) {
        this.localRepository = localRepository;
        this.reservaRepository = reservaRepository;
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

        // 3. Buscar todas las reservas correspondientes en el rango de fechas
        List<Reserva> reservas = reservaRepository.findByCanchasAndDateRange(canchaUuids, fechaInicio, fechaFin);

        List<DisponibilidadCanchaDTO> disponibilidadCanchas = new ArrayList<>();

        // 4. Calcular la disponibilidad para cada cancha
        for (Cancha cancha : canchas) {
            List<TurnoDTO> turnosList = new ArrayList<>();

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

                                // Comprobar si hay alguna reserva que solape con el turno
                                Optional<Reserva> overlappingReserva = reservas.stream()
                                        .filter(r -> r.getCancha().getUuid().equals(cancha.getUuid()))
                                        .filter(r -> r.getFecha().equals(finalFecha))
                                        .filter(r -> r.getHoraInicio().isBefore(finalSlotEnd) && r.getHoraFin().isAfter(finalSlotStart))
                                        .findFirst();

                                TurnoDTO turnoDTO;
                                if (overlappingReserva.isPresent()) {
                                    Reserva r = overlappingReserva.get();
                                    ReservaDTO rDto = new ReservaDTO(
                                            r.getUuid(),
                                            r.getNombreOrganizador(),
                                            r.getDeporte(),
                                            cancha.getCapacidad(),
                                            r.getCantidadParticipantesConfirmados(),
                                            r.getEstadoEvento()
                                    );
                                    turnoDTO = new TurnoDTO(finalFecha, finalSlotStart, finalSlotEnd, cancha.getNombre(), r.getDeporte(), "OCUPADO", rDto);
                                } else {
                                    String deporteCancha = cancha.getDeporte() != null ? cancha.getDeporte().getNombre() : null;
                                    turnoDTO = new TurnoDTO(finalFecha, finalSlotStart, finalSlotEnd, cancha.getNombre(), deporteCancha, "LIBRE", null);
                                }

                                turnosList.add(turnoDTO);
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
