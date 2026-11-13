package repository;

import com.saris.logitrack.model.Bodega;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodegaRepository extends JpaRepository<Bodega, Long> {
}