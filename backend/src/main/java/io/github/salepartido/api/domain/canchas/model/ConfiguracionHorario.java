package io.github.salepartido.api.domain.canchas.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ConfiguracionHorario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @Column(name = "duracion_turno", nullable = false)
    private Duration duracionTurno;

    @OneToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinColumn(name = "configuracion_horario_uuid")
    private List<ConfiguracionDia> configuracionesDias = new ArrayList<>();

}