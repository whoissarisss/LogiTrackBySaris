package repository;

import model.DetalleMovimiento;
import dto.response.ProductoMayorMovimientoResponse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleMovimientoRepository extends JpaRepository<DetalleMovimiento, Long> {

    List<DetalleMovimiento> findByMovimiento_IdMovimiento(Long idMovimiento);

    List<DetalleMovimiento> findByProducto_IdProducto(Long idProducto);

    @Query("""
    SELECT new dto.response.ProductoMayorMovimientoResponse(
        d.producto.idProducto,
        d.producto.nombre,
        SUM(d.cantidad)
    )
    FROM DetalleMovimiento d
    GROUP BY d.producto.idProducto, d.producto.nombre
    ORDER BY SUM(d.cantidad) DESC
    """)
    List<ProductoMayorMovimientoResponse> obtenerProductosConMayorMovimiento();
}
