package io.github.salepartido.api.domain.eventos.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.salepartido.api.domain.eventos.model.NivelDeporte;

public interface NivelDeporteRepository extends JpaRepository<NivelDeporte, UUID> {

    List<NivelDeporte> findByDeporteUuidOrderByOrdenAsc(UUID deporteUuid);

}
