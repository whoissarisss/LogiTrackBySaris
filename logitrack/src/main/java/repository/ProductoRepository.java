package repository;

import model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByEstadoTrue();
    List<Producto> findByMarca_IdMarca(Long idMarca);
    List<Producto> findByCategoria_IdCategoria(Long idCategoria);
}
