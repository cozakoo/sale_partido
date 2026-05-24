package io.github.salepartido.api.domain.locales.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.github.salepartido.api.infrastructure.config.AppConstants;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
    private UUID uuid;

    @Column(name = "nombre", nullable = false, length = AppConstants.VARCHAR_NAME_LENGTH)
    private String nombre;

    @Column(name = "direccion", nullable = false, length = AppConstants.VARCHAR_NAME_LENGTH)
    private String direccion;

    @OneToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST } )
    @JoinColumn(name = "local_uuid")
    private List<Cancha> canchas = new ArrayList<>();
    
    // RELACIÓN: Muchos a Muchos Unidireccional
    // La tabla intermedia 'local_deporte' une dos columnas de tipo UUID
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "local_deporte",
        joinColumns = @JoinColumn(name = "local_uuid", referencedColumnName = "uuid"),
        inverseJoinColumns = @JoinColumn(name = "deporte_uuid", referencedColumnName = "uuid")
    )
    private List<Deporte> deportes = new ArrayList<>();

    // RELACIÓN: Uno a Muchos Unidireccional
    // Se crea la columna fk 'local_uuid' (tipo UUID) dentro de la tabla 'horarioatencion'
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "local_uuid", referencedColumnName = "uuid", nullable = false) 
    private List<HorarioAtencion> horariosAtencion = new ArrayList<>();

}
