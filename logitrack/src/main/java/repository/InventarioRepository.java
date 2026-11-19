package repository;

import dto.response.ResumenStockBodegaResponse;
import model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Integer> {

    Optional<Inventario> findByBodega_IdBodegaAndProducto_IdProducto(
            Integer idBodega,
            Integer idProducto
    );

    List<Inventario> findByBodega_IdBodega(Integer idBodega);

    List<Inventario> findByProducto_IdProducto(Integer idProducto);

    List<Inventario> findByStockActualLessThanEqualOrderByStockActualAsc(
            BigDecimal limite
    );

    @Query("""
        SELECT new dto.response.ResumenStockBodegaResponse(
            i.bodega.idBodega,
            i.bodega.nombre,
            i.bodega.capacidad,
            COALESCE(SUM(i.stockActual), 0),
            COUNT(DISTINCT i.producto.idProducto)
        )
        FROM Inventario i
        GROUP BY i.bodega.idBodega, i.bodega.nombre, i.bodega.capacidad
        ORDER BY i.bodega.nombre
        """)
    List<ResumenStockBodegaResponse> obtenerResumenStockPorBodega();
}