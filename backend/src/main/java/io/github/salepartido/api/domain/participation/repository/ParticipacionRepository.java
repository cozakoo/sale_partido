package io.github.salepartido.api.domain.participation.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.salepartido.api.domain.participation.model.Participacion;

public interface ParticipacionRepository extends JpaRepository<Participacion, UUID> {
}
