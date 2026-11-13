package repository;

import model.DetalleMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleMovimientoRepository extends JpaRepository<DetalleMovimiento, Long> {
}