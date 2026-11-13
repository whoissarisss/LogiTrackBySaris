package com.saris.logitrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_movimiento",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id_movimiento", "id_producto"})
        })
@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class DetalleMovimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private long idDetalle;

    @ManyToOne
    @JoinColumn(name = "id_movimiento", nullable = false)
    private com.saris.logitrack.model.Movimiento movimiento;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private com.saris.logitrack.model.Producto producto;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;
}