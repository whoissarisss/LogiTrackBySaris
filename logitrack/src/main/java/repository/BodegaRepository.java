package repository;

import model.Bodega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BodegaRepository extends JpaRepository<Bodega, Integer> {

    List<Bodega> findByCiudad_IdCiudad(Integer idCiudad);

    List<Bodega> findByEncargado_IdUsuario(Integer idUsuario);
}