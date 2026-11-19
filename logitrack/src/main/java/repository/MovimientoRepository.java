package repository;

import model.Movimiento;
import model.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {

    List<Movimiento> findByUsuario_IdUsuario(Integer idUsuario);

    List<Movimiento> findByTipoMovimiento(TipoMovimiento tipoMovimiento);

    List<Movimiento> findByBodegaOrigen_IdBodega(Integer idBodega);

    List<Movimiento> findByBodegaDestino_IdBodega(Integer idBodega);

    List<Movimiento> findByBodegaOrigen_IdBodegaOrBodegaDestino_IdBodega(Integer idBodegaOrigen, Integer idBodegaDestino);

    List<Movimiento> findByFechaBetweenOrderByFechaDesc(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}