package repository;

import model.Auditoria;
import model.Operacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    List<Auditoria> findByUsuario_IdUsuario(Long idUsuario);

    List<Auditoria> findByTablaAfectada(String tablaAfectada);

    List<Auditoria> findByOperacion(Operacion operacion);

}
