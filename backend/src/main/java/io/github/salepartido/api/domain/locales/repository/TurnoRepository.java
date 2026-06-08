package io.github.salepartido.api.domain.locales.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.salepartido.api.domain.locales.model.Turno;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, UUID> {

    @Query("SELECT t FROM Turno t WHERE t.cancha.uuid IN :canchaUuids AND t.fecha >= :fechaInicio AND t.fecha <= :fechaFin")
    List<Turno> findByCanchasAndDateRange(
        @Param("canchaUuids") List<UUID> canchaUuids,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );

}
