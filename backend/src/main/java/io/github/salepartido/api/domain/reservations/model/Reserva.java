package io.github.salepartido.api.domain.reservations.model;

import java.time.LocalDate;
import java.util.UUID;

import io.github.salepartido.api.domain.locales.model.Cancha;
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
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", nullable = false, updatable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancha_uuid", nullable = false)
    private Cancha cancha;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private java.time.LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private java.time.LocalTime horaFin;

    @Column(name = "nombre_organizador", nullable = false)
    private String nombreOrganizador;

    @Column(name = "deporte", nullable = false)
    private String deporte;

    @Column(name = "cantidad_participantes_confirmados", nullable = false)
    private Integer cantidadParticipantesConfirmados;

    @Column(name = "estado_evento", nullable = false)
    private String estadoEvento;

}
