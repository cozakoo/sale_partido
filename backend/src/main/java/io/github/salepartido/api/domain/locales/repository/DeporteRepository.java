package io.github.salepartido.api.domain.locales.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.salepartido.api.domain.locales.model.Deporte;

@Repository
public interface DeporteRepository extends JpaRepository<Deporte, UUID> {
    Optional<Deporte> findByNombre(String nombre);
}
