package io.github.salepartido.api.domain.eventos.model;

import java.time.LocalDateTime;
import java.util.UUID;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Participacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", nullable = false, updatable = false)
    private UUID uuid;

    // true = el organizador invitó al participante
    // false = el participante solicitó unirse
    @Column(name = "es_invitacion", nullable = false)
    private Boolean esInvitacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoParticipacion estado;

    @Column(name = "fecha_estado", nullable = false)
    private LocalDateTime fechaEstado;

    @Column(name = "asistio", nullable = false)
    private Boolean asistio = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participante_uuid", nullable = false)
    private Usuario participante;

}
