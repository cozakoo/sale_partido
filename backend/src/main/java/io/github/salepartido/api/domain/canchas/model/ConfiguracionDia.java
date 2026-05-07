package io.github.salepartido.api.domain.canchas.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
class ConfiguracionDia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;

    private DayOfWeek diaSemana;

    private LocalTime horaInicio;

    private LocalTime horaFin;

    @ManyToOne
    private ConfiguracionHorario configuracionHorario;

}
