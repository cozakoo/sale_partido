package io.github.salepartido.api.domain.locales.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.salepartido.api.domain.locales.model.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, UUID> {

    @Query("SELECT r FROM Reserva r WHERE r.cancha.uuid IN :canchaUuids AND r.fecha >= :fechaInicio AND r.fecha <= :fechaFin")
    List<Reserva> findByCanchasAndDateRange(
        @Param("canchaUuids") List<UUID> canchaUuids,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );

}
