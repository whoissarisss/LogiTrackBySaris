package repository;

import model.Ciudad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CiudadRepository extends JpaRepository<Ciudad, Integer> {

    boolean existsByNombreAndDepartamentoAndPais(String nombre, String departamento, String pais);
}