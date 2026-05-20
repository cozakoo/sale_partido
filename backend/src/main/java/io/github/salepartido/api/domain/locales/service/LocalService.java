package io.github.salepartido.api.domain.locales.service;

import java.time.DayOfWeek;
import java.time.Duration;
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