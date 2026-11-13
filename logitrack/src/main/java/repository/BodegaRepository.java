package repository;

import model.Bodega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BodegaRepository extends JpaRepository<Bodega, Long> {

    List<Bodega> findByCiudad_IdCiudad(Long idCiudad);

    List<Bodega> findByEncargado_IdUsuario(Long idUsuario);
}
