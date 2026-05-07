package io.github.salepartido.api.domain.canchas.model;

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
public class Cancha {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;

    private String nombre;

    private String codigo;

    @ManyToOne
    private Local local;

}
