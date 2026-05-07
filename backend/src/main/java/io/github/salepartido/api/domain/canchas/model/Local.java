package io.github.salepartido.api.domain.canchas.model;

import java.util.Collection;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private String uuid;

    private String nombre;

    private String codigo;

    @OneToMany
    private Collection<Cancha> canchas;

}
