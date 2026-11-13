package model;

import model.Movimiento;
import model.Producto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_movimiento", uniqueConstraints = {@UniqueConstraint(columnNames = {"id_movimiento", "id_producto"})})
@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class DetalleMovimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private long idDetalle;

    @ManyToOne
    @JoinColumn(name = "id_movimiento", nullable = false)
    private Movimiento movimiento;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;
}