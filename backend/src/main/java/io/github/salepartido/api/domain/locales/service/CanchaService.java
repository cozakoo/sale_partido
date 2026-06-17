package io.github.salepartido.api.domain.locales.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.salepartido.api.domain.locales.model.Cancha;
import io.github.salepartido.api.domain.locales.repository.CanchaRepository;
import io.github.salepartido.api.domain.locales.service.dto.CrearCanchaOperation;
import io.github.salepartido.api.domain.locales.service.dto.ActualizarCanchaOperation;
import io.github.salepartido.api.domain.locales.service.mapper.CrearCanchaOperationMapper;
import io.github.salepartido.api.domain.locales.service.mapper.ActualizarCanchaOperationMapper;
import io.github.salepartido.api.domain.locales.exception.CanchaNoEncontradaException;

@Service
public class CanchaService {

    private final CanchaRepository canchaRepository;
    private final CrearCanchaOperationMapper crearCanchaOperationMapper;
    private final ActualizarCanchaOperationMapper actualizarCanchaOperationMapper;

    public CanchaService(
            CanchaRepository canchaRepository,
            CrearCanchaOperationMapper crearCanchaOperationMapper,
            ActualizarCanchaOperationMapper actualizarCanchaOperationMapper) {
        this.canchaRepository = canchaRepository;
        this.crearCanchaOperationMapper = crearCanchaOperationMapper;
        this.actualizarCanchaOperationMapper = actualizarCanchaOperationMapper;
    }

    public List<Cancha> obtenerTodasLasCanchas() {
        return canchaRepository.findAll();
    }

    public Optional<Cancha> buscarCanchaPorId(UUID uuid) {
        return canchaRepository.findById(uuid);
    }

    @Transactional
    public Cancha crearCancha(CrearCanchaOperation operation) {
        Cancha cancha = crearCanchaOperationMapper.toEntity(operation);
        return canchaRepository.save(cancha);
    }

    @Transactional
    public Cancha actualizarCancha(UUID uuid, ActualizarCanchaOperation operation) {
        Cancha existing = canchaRepository.findById(uuid)
                .orElseThrow(() -> new CanchaNoEncontradaException(uuid));
        actualizarCanchaOperationMapper.updateEntity(operation, existing);
        return canchaRepository.save(existing);
    }

    public Cancha guardarCancha(Cancha cancha) {
        return canchaRepository.save(cancha);
    }

    @Transactional
    public void eliminarCancha(UUID uuid) {
        Cancha cancha = canchaRepository.findById(uuid)
                .orElseThrow(() -> new CanchaNoEncontradaException(uuid));
        canchaRepository.delete(cancha);
    }
}