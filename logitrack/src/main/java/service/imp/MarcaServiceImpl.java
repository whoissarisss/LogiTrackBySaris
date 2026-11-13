package service.imp;

import dto.request.MarcaRequest;
import dto.response.MarcaResponse;
import mapper.MarcaMapper;
import model.Marca;
import repository.MarcaRepository;
import service.MarcaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarcaServiceImpl implements MarcaService {

    private final MarcaRepository marcaRepository;
    private final MarcaMapper marcaMapper;

    @Override
    public MarcaResponse registrarMarca(MarcaRequest dto) {

        if (marcaRepository.existsByNombre(dto.nombre())) {
            throw new IllegalArgumentException("La marca ya se encuentra registrada");
        }

        Marca marca = marcaMapper.DtoToEntity(dto);

        Marca marcaGuardada = marcaRepository.save(marca);

        return marcaMapper.entityToDto(marcaGuardada);
    }

    @Override
    public List<MarcaResponse> listarMarcas() {

        List<Marca> marcas = marcaRepository.findAll();

        return marcas.stream()
                .map(marcaMapper::entityToDto)
                .toList();
    }

    @Override
    public MarcaResponse obtenerMarcaPorId(Long idMarca) {

        Marca marca = marcaRepository.findById(idMarca)
                .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada"));

        return marcaMapper.entityToDto(marca);
    }

    @Override
    public MarcaResponse actualizarMarca(Long idMarca, MarcaRequest dto) {

        Marca marca = marcaRepository.findById(idMarca)
                .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada"));

        boolean existe = marcaRepository.existsByNombre(dto.nombre());

        if (existe && !marca.getNombre().equals(dto.nombre())) {
            throw new IllegalArgumentException("Ya existe una marca con ese nombre");
        }

        marcaMapper.updateEntityFromDto(dto, marca);

        Marca marcaActualizada = marcaRepository.save(marca);

        return marcaMapper.entityToDto(marcaActualizada);
    }

    @Override
    public void eliminarMarca(Long idMarca) {

        Marca marca = marcaRepository.findById(idMarca)
                .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada"));

        marcaRepository.delete(marca);
    }
}
