package com.saris.logitrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "inventario",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id_bodega", "id_producto"})
        })
@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario")
    private long idInventario;

    @ManyToOne
    @JoinColumn(name = "id_bodega", nullable = false)
    private Bodega bodega;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(name = "stock_actual", nullable = false, precision = 12, scale = 3)
    private BigDecimal stockActual;
}