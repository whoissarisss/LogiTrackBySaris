package repository;

import model.DetalleMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleMovimientoRepository extends JpaRepository<DetalleMovimiento, Long> {

    List<DetalleMovimiento> findByMovimiento_IdMovimiento(Long idMovimiento);

    List<DetalleMovimiento> findByProducto_IdProducto(Long idProducto);
}
