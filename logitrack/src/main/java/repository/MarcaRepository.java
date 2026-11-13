package repository;

import model.Marca;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarcaRepository extends JpaRepository<Marca, Long> {
}