package io.github.salepartido.api.domain.locales.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.salepartido.api.domain.locales.model.Cancha;

public interface CanchaRepository extends JpaRepository<Cancha, UUID> {
    
}