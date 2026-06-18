package io.github.salepartido.api.domain.eventos.model;

import java.util.UUID;

import io.github.salepartido.api.domain.locales.model.Deporte;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class HabilidadJugador {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", nullable = false, updatable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deporte_uuid", nullable = false)
    private Deporte deporte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nivel_deporte_uuid", nullable = false)
    private NivelDeporte nivel;

}
