package repository;

import model.Movimiento;
import model.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByUsuario_IdUsuario(Long idUsuario);

    List<Movimiento> findByTipoMovimiento(TipoMovimiento tipoMovimiento);

    List<Movimiento> findByBodegaOrigen_IdBodega(Long idBodega);

    List<Movimiento> findByBodegaDestino_IdBodega(Long idBodega);
}
