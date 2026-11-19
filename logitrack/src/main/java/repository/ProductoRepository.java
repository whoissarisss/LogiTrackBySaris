package repository;

import model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByEstadoTrue();

    List<Producto> findByMarca_IdMarca(Integer idMarca);

    List<Producto> findByCategoria_IdCategoria(Integer idCategoria);
}