package io.github.salepartido.api.domain.participation.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.github.salepartido.api.domain.locales.model.Turno;
import io.github.salepartido.api.infrastructure.config.AppConstants;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", nullable = false, updatable = false)
    private UUID uuid;

    @Column(name = "nombre", nullable = false, length = AppConstants.VARCHAR_NAME_LENGTH)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoEvento tipo;

    @Column(name = "cupo_minimo", nullable = false)
    private Integer cupoMinimo;

    @Column(name = "cupo_maximo", nullable = false)
    private Integer cupoMaximo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoEvento estado;

    // Opcional: el nivel mínimo requerido para participar.
    // nivelRequerido.deporte define implícitamente el deporte del evento.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nivel_requerido_uuid", nullable = true)
    private NivelDeporte nivelRequerido;

    // Turno al que pertenece este evento (fecha, hora y cancha).
    // Un Turno puede tener a lo sumo un Evento activo.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turno_uuid", nullable = false)
    private Turno turno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizador_uuid", nullable = false)
    private Usuario organizador;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "evento_uuid", nullable = false)
    private List<Participacion> participaciones = new ArrayList<>();

}
