package io.github.salepartido.api.domain.canchas.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

import io.github.salepartido.api.infrastructure.config.AppConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ConfiguracionDia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", nullable = false, updatable = false)
    private String uuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = AppConstants.VARCHAR_NAME_LENGTH)
    private DayOfWeek diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

}
