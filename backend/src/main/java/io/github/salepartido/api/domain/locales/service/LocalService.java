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

import io.github.salepartido.api.domain.locales.exception.CanchaNoEncontradaException;
import io.github.salepartido.api.domain.locales.exception.CanchaNoPerteneceAlLocalException;
import io.github.salepartido.api.domain.locales.exception.LocalNoEncontradoException;
import io.github.salepartido.api.domain.locales.model.Cancha;
import io.github.salepartido.api.domain.locales.model.ConfiguracionDia;
import io.github.salepartido.api.domain.locales.model.ConfiguracionHorario;
import io.github.salepartido.api.domain.locales.model.Local;
import io.github.salepartido.api.domain.locales.repository.CanchaRepository;
import io.github.salepartido.api.domain.locales.repository.LocalRepository;
import io.github.salepartido.api.domain.locales.service.dto.ActualizarConfiguracionesHorariosOperation;
import io.github.salepartido.api.domain.locales.service.dto.BuscarLocalesOperation;

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

    public Optional<Local> buscarLocalPorCanchaUuid(UUID canchaUuid) {
        return localRepository.findByCanchaUuid(canchaUuid);
    }

    public List<Local> buscarLocales(BuscarLocalesOperation operation) {
        List<Local> locales = localRepository.findAll();

        return locales.stream()
            .filter(this::localActivo)
            .filter(local -> matchesUbicacion(local, operation.ubicacion()))
            .filter(local -> matchesTipoDeporte(local, operation.tipoDeporte()))
            .filter(local -> matchesFecha(local, operation.fecha()))
            .filter(local -> matchesHorarioDisponible(local, operation.fecha(), operation.desde(), operation.hasta()))
            .collect(Collectors.toList());
    }

    private boolean localActivo(Local local) {
        return local.getCanchas() != null && local.getCanchas().stream()
            .flatMap(cancha -> cancha.getConfiguracionesHorarios() != null ? cancha.getConfiguracionesHorarios().stream() : List.<io.github.salepartido.api.domain.locales.model.ConfiguracionHorario>of().stream())
            .anyMatch(io.github.salepartido.api.domain.locales.model.ConfiguracionHorario::isActivo);
    }

    private boolean matchesUbicacion(Local local, String ubicacion) {
        if (ubicacion == null || ubicacion.isBlank()) {
            return true;
        }
        if (local.getUbicacion() == null) {
            return false;
        }

        String direccion = local.getUbicacion().getDireccion() != null ? local.getUbicacion().getDireccion().toLowerCase() : "";
        String localidad = (local.getUbicacion().getLocalidad() != null && local.getUbicacion().getLocalidad().getNombre() != null)
                ? local.getUbicacion().getLocalidad().getNombre().toLowerCase()
                : "";

        return direccion.contains(ubicacion.toLowerCase()) || localidad.contains(ubicacion.toLowerCase());
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

    @Transactional
    public void actualizarConfiguracionesHorarios(UUID localUuid, ActualizarConfiguracionesHorariosOperation operation) {
        Local local = localRepository.findById(localUuid)
            .orElseThrow(LocalNoEncontradoException::new);

        for (ActualizarConfiguracionesHorariosOperation.CanchaConfiguracionOperation configDto : operation.canchas()) {
            Cancha cancha = canchaRepository.findById(configDto.canchaUuid())
                .orElseThrow(() -> new CanchaNoEncontradaException(configDto.canchaUuid()));
            
            // Validar que la cancha le pertenece a este local (por seguridad)
            if (local.getCanchas() == null || local.getCanchas().stream().noneMatch(c -> c.getUuid().equals(cancha.getUuid()))) {
                throw new CanchaNoPerteneceAlLocalException();
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
            for (ActualizarConfiguracionesHorariosOperation.ConfiguracionDiaOperation diaDto : configDto.configuracionesDias()) {
                ConfiguracionDia dia = diasExistentes.getOrDefault(diaDto.diaSemana(), new ConfiguracionDia());
                dia.setDiaSemana(diaDto.diaSemana());
                dia.setHoraInicio(diaDto.horaInicio());
                dia.setHoraFin(diaDto.horaFin());
                nuevosDias.add(dia);
            }

            configHorario.getConfiguracionesDias().clear();
            configHorario.getConfiguracionesDias().addAll(nuevosDias);
        }
    }
}