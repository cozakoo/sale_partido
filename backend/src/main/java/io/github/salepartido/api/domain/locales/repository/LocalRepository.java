package io.github.salepartido.api.domain.locales.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.salepartido.api.domain.locales.model.Local;

@Repository
public interface LocalRepository extends JpaRepository<Local, UUID> {

    @Query("SELECT l FROM Local l JOIN l.canchas c WHERE c.uuid = :canchaUuid")
    Optional<Local> findByCanchaUuid(@Param("canchaUuid") UUID canchaUuid);
}