package io.github.salepartido.api.domain.canchas.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.github.salepartido.api.domain.canchas.model.Cancha;
import io.github.salepartido.api.domain.canchas.repository.CanchaRepository;

@Service
public class CanchaService {

    private final CanchaRepository canchaRepository;

    public CanchaService(CanchaRepository canchaRepository) {
        this.canchaRepository = canchaRepository;
    }

    /**
     * Función de ejemplo: Obtiene todas las canchas disponibles
     */
    public List<Cancha> obtenerTodasLasCanchas() {
        List<Cancha> canchas = canchaRepository.findAll();
        if (!canchas.isEmpty()) {
            return canchas;
        }

        Cancha cancha1 = new Cancha();
        cancha1.setNombre("Cancha Fútbol 5");
        cancha1.setCodigo("F05");

        Cancha cancha2 = new Cancha();
        cancha2.setNombre("Cancha Básquet");
        cancha2.setCodigo("BKT");

        Cancha cancha3 = new Cancha();
        cancha3.setNombre("Cancha Tenis");
        cancha3.setCodigo("TNS");

        return List.of(cancha1, cancha2, cancha3);
    }

    /**
     * Función de ejemplo: Busca una cancha por su UUID
     */
    public Optional<Cancha> buscarCanchaPorId(UUID uuid) {
        return canchaRepository.findById(uuid);
    }

    /**
     * Función de ejemplo: Guarda una nueva cancha
     */
    public Cancha guardarCancha(Cancha cancha) {
        return canchaRepository.save(cancha);
    }

    /**
     * Función de ejemplo: Elimina una cancha por su UUID
     */
    public void eliminarCancha(UUID uuid) {
        canchaRepository.deleteById(uuid);
    }
}