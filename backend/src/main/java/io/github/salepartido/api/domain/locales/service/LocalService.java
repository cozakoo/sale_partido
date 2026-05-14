package io.github.salepartido.api.domain.locales.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import io.github.salepartido.api.domain.locales.model.Cancha;
import io.github.salepartido.api.domain.locales.model.Local;
import io.github.salepartido.api.domain.locales.repository.CanchaRepository;
import io.github.salepartido.api.domain.locales.repository.LocalRepository;
import io.github.salepartido.api.domain.locales.controller.dto.CanchaConfiguracionDTO;
import io.github.salepartido.api.domain.locales.controller.dto.SaveCanchaConfiguracionHorarioRequest;

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

    @Transactional
    public void actualizarConfiguracionesHorarios(UUID localUuid, SaveCanchaConfiguracionHorarioRequest request) {
        Local local = localRepository.findById(localUuid)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Local no encontrado"));

        for (CanchaConfiguracionDTO configDto : request.canchas()) {
            Cancha cancha = canchaRepository.findById(configDto.canchaUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cancha con UUID " + configDto.canchaUuid() + " no encontrada"));
            
            // Validar que la cancha le pertenece a este local (por seguridad)
            if (local.getCanchas() == null || local.getCanchas().stream().noneMatch(c -> c.getUuid().equals(cancha.getUuid()))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cancha no pertenece al local especificado");
            }

            // Aquí deberías colocar la lógica exacta de cómo actualizas/pisás configuraciones
            // Por ahora este servicio te arma el camino para tu DTO sin importar basura.
            // Podrías delegarlo de ser necesario.
        }
    }
}