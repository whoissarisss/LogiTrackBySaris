package com.saris.logitrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bodega")
@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class Bodega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bodega")
    private long idBodega;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, precision = 12, scale = 2)
    private java.math.BigDecimal capacidad;

    @ManyToOne
    @JoinColumn(name = "id_ciudad", nullable = false)
    private com.saris.logitrack.model.Ciudad ciudad;

    @ManyToOne
    @JoinColumn(name = "id_encargado", nullable = false)
    private com.saris.logitrack.model.Usuario encargado;
}
