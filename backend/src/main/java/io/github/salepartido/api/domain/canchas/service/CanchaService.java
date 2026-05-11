package io.github.salepartido.api.domain.canchas.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.salepartido.api.domain.canchas.model.Cancha;
import io.github.salepartido.api.domain.canchas.repository.CanchaRepository;

@Service
public class CanchaService {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final CanchaRepository canchaRepository;

    public CanchaService(CanchaRepository canchaRepository) {
        this.canchaRepository = canchaRepository;
    }

    /**
     * Función de ejemplo: Obtiene todas las canchas disponibles
     */
    public List<Cancha> obtenerTodasLasCanchas() {
        return cargarCanchasDesdeJson();
    }

    private List<Cancha> cargarCanchasDesdeJson() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("canchas.json")) {
            if (inputStream == null) {
                return getDefaultCanchas();
            }

            return OBJECT_MAPPER.readValue(inputStream, new TypeReference<List<Cancha>>() {});
        } catch (IOException e) {
            return getDefaultCanchas();
        }
    }

    private List<Cancha> getDefaultCanchas() {
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