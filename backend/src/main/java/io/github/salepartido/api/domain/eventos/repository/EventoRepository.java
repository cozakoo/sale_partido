package io.github.salepartido.api.domain.eventos.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.salepartido.api.domain.eventos.model.Evento;

public interface EventoRepository extends JpaRepository<Evento, UUID> {
    java.util.Optional<Evento> findByTurnoUuid(UUID turnoUuid);
}
