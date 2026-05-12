package io.github.salepartido.api.domain.canchas.model;

import java.util.ArrayList;
import java.util.List;

import io.github.salepartido.api.infrastructure.config.AppConstants;
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
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", nullable = false, updatable = false)
    private String uuid;

    @Column(name = "nombre", nullable = false, length = AppConstants.VARCHAR_NAME_LENGTH)
    private String nombre;

    @OneToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST }, orphanRemoval = true)
    @JoinColumn(name = "local_uuid")
    private List<Cancha> canchas = new ArrayList<>();

}
