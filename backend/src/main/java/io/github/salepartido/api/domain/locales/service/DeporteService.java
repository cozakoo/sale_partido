package io.github.salepartido.api.domain.locales.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import io.github.salepartido.api.domain.locales.model.Deporte;
import io.github.salepartido.api.domain.locales.repository.DeporteRepository;
import net.datafaker.Faker;

@Service
public class DeporteService {

    private final DeporteRepository deporteRepository;
    private final Faker faker = new Faker(Locale.of("es"));

    private static final String[] DEPORTES_PREDETERMINADOS = {
        "Fútbol", "Tenis", "Paddle", "Vóley", "Básquet"
    };

    public DeporteService(DeporteRepository deporteRepository) {
        this.deporteRepository = deporteRepository;
    }

    public List<Deporte> poblarDeportes() {
        List<Deporte> deportes = new ArrayList<>();
        for (String nombre : DEPORTES_PREDETERMINADOS) {
            Deporte deporte = deporteRepository.findByNombre(nombre)
                    .orElseGet(() -> {
                        Deporte nuevo = new Deporte();
                        nuevo.setNombre(nombre);
                        return deporteRepository.save(nuevo);
                    });
            deportes.add(deporte);
        }
        return deportes;
    }

    public Deporte obtenerDeporteAleatorioPersistido() {
        List<Deporte> deportes = deporteRepository.findAll();
        if (deportes.isEmpty()) {
            deportes = poblarDeportes();
        }
        return deportes.get(faker.random().nextInt(deportes.size()));
    }
}
