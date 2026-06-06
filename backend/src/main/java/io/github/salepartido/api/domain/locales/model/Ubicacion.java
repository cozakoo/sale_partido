package io.github.salepartido.api.domain.locales.model;

import java.util.UUID;

import io.github.salepartido.api.infrastructure.config.AppConstants;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Ubicacion {
 
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", nullable = false, updatable = false)
    private UUID uuid;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "localidad_uuid", nullable = false)
    private Localidad localidad;

    @Column(name = "direccion", nullable = false, length = AppConstants.VARCHAR_NAME_LENGTH)
    private String direccion;

}
