package com.saris.logitrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.saris.logitrack.model.Usuario;
import com.saris.logitrack.model.Ciudad;

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
    private Integer capacidad;

    @ManyToOne
    @JoinColumn(name = "id_ciudad", nullable = false)
    private Ciudad ciudad;

    @ManyToOne
    @JoinColumn(name = "id_encargado", nullable = false)
    private Usuario encargado;
}
