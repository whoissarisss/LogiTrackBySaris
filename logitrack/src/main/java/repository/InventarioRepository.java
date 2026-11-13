package repository;

import model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findByBodega_IdBodegaAndProducto_IdProducto(Long idBodega, Long idProducto);

    List<Inventario> findByBodega_IdBodega(Long idBodega);

    List<Inventario> findByProducto_IdProducto(Long idProducto);
}
