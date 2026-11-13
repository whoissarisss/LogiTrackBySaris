package service.imp;

import dto.request.CiudadRequest;
import dto.response.CiudadResponse;
import mapper.CiudadMapper;
import model.Ciudad;
import repository.CiudadRepository;
import service.CiudadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CiudadServiceImpl implements CiudadService {

    private final CiudadRepository ciudadRepository;
    private final CiudadMapper ciudadMapper;

    @Override
    public CiudadResponse registrarCiudad(CiudadRequest dto) {

        if (ciudadRepository.existsByNombreAndDepartamentoAndPais(
                dto.nombre(), dto.departamento(), dto.pais())) {
            throw new IllegalArgumentException("La ciudad ya se encuentra registrada");
        }

        Ciudad ciudad = ciudadMapper.DtoToEntity(dto);

        Ciudad ciudadGuardada = ciudadRepository.save(ciudad);

        return ciudadMapper.entityToDto(ciudadGuardada);
    }

    @Override
    public List<CiudadResponse> listarCiudades() {

        List<Ciudad> ciudades = ciudadRepository.findAll();

        return ciudades.stream()
                .map(ciudadMapper::entityToDto)
                .toList();
    }

    @Override
    public CiudadResponse obtenerCiudadPorId(Long idCiudad) {

        Ciudad ciudad = ciudadRepository.findById(idCiudad)
                .orElseThrow(() -> new IllegalArgumentException("Ciudad no encontrada"));

        return ciudadMapper.entityToDto(ciudad);
    }

    @Override
    public CiudadResponse actualizarCiudad(Long idCiudad, CiudadRequest dto) {

        Ciudad ciudad = ciudadRepository.findById(idCiudad)
                .orElseThrow(() -> new IllegalArgumentException("Ciudad no encontrada"));

        boolean existe = ciudadRepository.existsByNombreAndDepartamentoAndPais(
                dto.nombre(), dto.departamento(), dto.pais());

        if (existe && !(ciudad.getNombre().equals(dto.nombre())
                && ciudad.getDepartamento().equals(dto.departamento())
                && ciudad.getPais().equals(dto.pais()))) {
            throw new IllegalArgumentException("Ya existe una ciudad con esos datos");
        }

        ciudadMapper.updateEntityFromDto(dto, ciudad);

        Ciudad ciudadActualizada = ciudadRepository.save(ciudad);

        return ciudadMapper.entityToDto(ciudadActualizada);
    }

    @Override
    public void eliminarCiudad(Long idCiudad) {

        Ciudad ciudad = ciudadRepository.findById(idCiudad)
                .orElseThrow(() -> new IllegalArgumentException("Ciudad no encontrada"));

        ciudadRepository.delete(ciudad);
    }
}
