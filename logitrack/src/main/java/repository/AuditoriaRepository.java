package repository;

import model.Auditoria;
import model.Operacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {

    List<Auditoria> findByUsuario_IdUsuario(Integer idUsuario);

    List<Auditoria> findByTablaAfectada(String tablaAfectada);

    List<Auditoria> findByOperacion(Operacion operacion);

}
