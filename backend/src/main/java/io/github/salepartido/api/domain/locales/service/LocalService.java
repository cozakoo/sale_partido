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
import io.github.salepartido.api.domain.locales.repository.CanchaRepository;
import io.github.salepartido.api.domain.locales.repository.LocalRepository;
import io.github.salepartido.api.domain.locales.controller.dto.CanchaConfiguracionDTO;
import io.github.salepartido.api.domain.locales.controller.dto.ConfiguracionDiaDTO;
import io.github.salepartido.api.domain.locales.controller.dto.FiltroViewModel;
import io.github.salepartido.api.domain.locales.controller.dto.SaveCanchasConfiguracionesHorariosRequest;
import io.github.salepartido.api.domain.locales.controller.dto.SaveCanchasConfiguracionesHorariosResponse;

@Service
public class LocalService {

    private final LocalRepository localRepository;
    private final CanchaRepository canchaRepository;

    public LocalService(LocalRepository localRepository, CanchaRepository canchaRepository) {
        this.localRepository = localRepository;
        this.canchaRepository = canchaRepository;
    }

    public Local guardarLocal(Local local) {
        return localRepository.save(local);
    }

    public void eliminarLocal(UUID uuid) {
        localRepository.deleteById(uuid);
    }

    public List<Local> obtenerTodosLosLocales() {
        return localRepository.findAll();
    }

    public Optional<Local> buscarLocalPorId(UUID uuid) {
        return localRepository.findById(uuid);
    }

    public List<Local> buscarLocales(FiltroViewModel filtro) {
        List<Local> locales = localRepository.findAll();

        LocalDate fechaFiltro = parseFecha(filtro.fecha());
        LocalTime desde = parseHora(filtro.horarioDisponible() != null ? filtro.horarioDisponible().desde() : null);
        LocalTime hasta = parseHora(filtro.horarioDisponible() != null ? filtro.horarioDisponible().hasta() : null);

        return locales.stream()
            .filter(this::localActivo)
            .filter(local -> matchesUbicacion(local, filtro.ubicacion(), filtro.zona()))
            .filter(local -> matchesTipoDeporte(local, filtro.tipoDeporte()))
            .filter(local -> matchesFecha(local, fechaFiltro))
            .filter(local -> matchesHorarioDisponible(local, fechaFiltro, desde, hasta))
            .collect(Collectors.toList());
    }

    private boolean localActivo(Local local) {
        return local.getCanchas() != null && local.getCanchas().stream()
            .flatMap(cancha -> cancha.getConfiguracionesHorarios() != null ? cancha.getConfiguracionesHorarios().stream() : List.<io.github.salepartido.api.domain.locales.model.ConfiguracionHorario>of().stream())
            .anyMatch(io.github.salepartido.api.domain.locales.model.ConfiguracionHorario::isActivo);
    }

    private boolean matchesUbicacion(Local local, String ubicacion, String zona) {
        if ((ubicacion == null || ubicacion.isBlank()) && (zona == null || zona.isBlank())) {
            return true;
        }

        String direccion = local.getDireccion() != null ? local.getDireccion().toLowerCase() : "";
        return (ubicacion != null && !ubicacion.isBlank() && direccion.contains(ubicacion.toLowerCase()))
            || (zona != null && !zona.isBlank() && direccion.contains(zona.toLowerCase()));
    }

    private boolean matchesTipoDeporte(Local local, String tipoDeporte) {
        if (tipoDeporte == null || tipoDeporte.isBlank()) {
            return true;
        }

        return local.getCanchas() != null && local.getCanchas().stream()
            .anyMatch(cancha -> cancha.getDeporte() != null && tipoDeporte.equalsIgnoreCase(cancha.getDeporte().getNombre()));
    }

    private boolean matchesFecha(Local local, LocalDate fechaFiltro) {
        if (fechaFiltro == null) {
            return true;
        }

        DayOfWeek diaSemana = fechaFiltro.getDayOfWeek();
        return local.getCanchas() != null && local.getCanchas().stream()
            .flatMap(cancha -> cancha.getConfiguracionesHorarios() != null ? cancha.getConfiguracionesHorarios().stream() : List.<io.github.salepartido.api.domain.locales.model.ConfiguracionHorario>of().stream())
            .filter(io.github.salepartido.api.domain.locales.model.ConfiguracionHorario::isActivo)
            .flatMap(config -> config.getConfiguracionesDias() != null ? config.getConfiguracionesDias().stream() : List.<io.github.salepartido.api.domain.locales.model.ConfiguracionDia>of().stream())
            .anyMatch(dia -> dia.getDiaSemana() == diaSemana);
    }

    private boolean matchesHorarioDisponible(Local local, LocalDate fechaFiltro, LocalTime desde, LocalTime hasta) {
        if (desde == null && hasta == null) {
            return true;
        }

        // Si no se especifica fecha, se valida contra cualquier día activo de la semana.
        return local.getCanchas() != null && local.getCanchas().stream()
            .flatMap(cancha -> cancha.getConfiguracionesHorarios() != null ? cancha.getConfiguracionesHorarios().stream() : List.<io.github.salepartido.api.domain.locales.model.ConfiguracionHorario>of().stream())
            .filter(io.github.salepartido.api.domain.locales.model.ConfiguracionHorario::isActivo)
            .flatMap(config -> config.getConfiguracionesDias() != null ? config.getConfiguracionesDias().stream() : List.<io.github.salepartido.api.domain.locales.model.ConfiguracionDia>of().stream())
            .anyMatch(dia -> {
                if (fechaFiltro != null && dia.getDiaSemana() != fechaFiltro.getDayOfWeek()) {
                    return false;
                }
                LocalTime inicio = dia.getHoraInicio();
                LocalTime fin = dia.getHoraFin();
                if (desde != null && hasta != null) {
                    return !fin.isBefore(desde) && !inicio.isAfter(hasta);
                }
                if (desde != null) {
                    return !fin.isBefore(desde);
                }
                return !inicio.isAfter(hasta);
            });
    }

    private LocalDate parseFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return null;
        }

        String normalized = fecha.trim().toLowerCase();
        if (FiltroViewModel.FECHA_HOY.equals(normalized)) {
            return LocalDate.now();
        }
        if (FiltroViewModel.FECHA_MANANA.equals(normalized)) {
            return LocalDate.now().plusDays(1);
        }

        try {
            return LocalDate.parse(normalized);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fecha inválida: " + fecha);
        }
    }

    private LocalTime parseHora(String hora) {
        if (hora == null || hora.isBlank()) {
            return null;
        }

        String normalized = hora.trim().replace("hs", "").replace("HS", "").trim();
        if (normalized.endsWith(".") || normalized.endsWith("h")) {
            normalized = normalized.substring(0, normalized.length() - 1).trim();
        }

        try {
            return LocalTime.parse(normalized);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Horario inválido: " + hora + ". Use el formato HH:mm o HH:mm hs.");
        }
    }

    /* 
    Este método debe recibir ese DTO de request, procesarla y retornar ese DTO de response
    Como cada cancha puede tener varias configuraciones de horarios, lo que debe hacer es:
    - Para cada cancha, actualizar la configuración horario de esta forma:
        - con el UUID de la cancha y el UUID del horario, actualizar cada configuracion de horario:
            - Si viene en el request una ConfiguracionDia que no existe, añadirla
            - Si viene en el request una ConfiguracionDia que existe (con UUID), actualizarla
            - Si no viene en el request una ConfiguracionDia que existe, eliminarla
        - Por más que Cancha tiene una lista de ConfiguracionHorario, solo implementalo de forma que haya solo una en esa lista y siempre con el atributo "activo" true
    */
    @Transactional
    public SaveCanchasConfiguracionesHorariosResponse actualizarConfiguracionesHorarios(UUID localUuid, SaveCanchasConfiguracionesHorariosRequest request) {
        Local local = localRepository.findById(localUuid)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Local no encontrado"));

        for (CanchaConfiguracionDTO configDto : request.canchas()) {
            Cancha cancha = canchaRepository.findById(configDto.canchaUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cancha con UUID " + configDto.canchaUuid() + " no encontrada"));
            
            // Validar que la cancha le pertenece a este local (por seguridad)
            if (local.getCanchas() == null || local.getCanchas().stream().noneMatch(c -> c.getUuid().equals(cancha.getUuid()))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cancha no pertenece al local especificado");
            }

            if (cancha.getConfiguracionesHorarios() == null) {
                cancha.setConfiguracionesHorarios(new ArrayList<>());
            }

            ConfiguracionHorario configHorario;
            if (cancha.getConfiguracionesHorarios().isEmpty()) {
                configHorario = new ConfiguracionHorario();
                cancha.getConfiguracionesHorarios().add(configHorario);
            } else {
                configHorario = cancha.getConfiguracionesHorarios().get(0);
                if (cancha.getConfiguracionesHorarios().size() > 1) {
                    cancha.getConfiguracionesHorarios().subList(1, cancha.getConfiguracionesHorarios().size()).clear();
                }
            }

            configHorario.setActivo(true);
            configHorario.setDuracionTurno(Duration.ofMinutes(configDto.duracionTurno()));

            if (configHorario.getConfiguracionesDias() == null) {
                configHorario.setConfiguracionesDias(new ArrayList<>());
            }

            Map<DayOfWeek, ConfiguracionDia> diasExistentes = configHorario.getConfiguracionesDias().stream()
                    .collect(Collectors.toMap(ConfiguracionDia::getDiaSemana, d -> d));

            List<ConfiguracionDia> nuevosDias = new ArrayList<>();
            for (ConfiguracionDiaDTO diaDto : configDto.configuracionesDias()) {
                ConfiguracionDia dia = diasExistentes.getOrDefault(diaDto.diaSemana(), new ConfiguracionDia());
                dia.setDiaSemana(diaDto.diaSemana());
                dia.setHoraInicio(diaDto.horaInicio());
                dia.setHoraFin(diaDto.horaFin());
                nuevosDias.add(dia);
            }

            configHorario.getConfiguracionesDias().clear();
            configHorario.getConfiguracionesDias().addAll(nuevosDias);
        }

        return new SaveCanchasConfiguracionesHorariosResponse(request.canchas());
    }
}