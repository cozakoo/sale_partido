package io.github.salepartido.api.domain.locales.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.github.salepartido.api.domain.locales.model.Cancha;
import io.github.salepartido.api.domain.locales.repository.CanchaRepository;

@Service
public class CanchaService {

    private final CanchaRepository canchaRepository;

    public CanchaService(CanchaRepository canchaRepository) {
        this.canchaRepository = canchaRepository;
    }

    public List<Cancha> obtenerTodasLasCanchas() {
        return canchaRepository.findAll();
    }

    public Optional<Cancha> buscarCanchaPorId(UUID uuid) {
        return canchaRepository.findById(uuid);
    }

    public Cancha guardarCancha(Cancha cancha) {
        return canchaRepository.save(cancha);
    }

    public void eliminarCancha(UUID uuid) {
        canchaRepository.deleteById(uuid);
    }
}