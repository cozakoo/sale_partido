package io.github.salepartido.api.domain.eventos.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.salepartido.api.domain.eventos.model.Participacion;

public interface ParticipacionRepository extends JpaRepository<Participacion, UUID> {
}
