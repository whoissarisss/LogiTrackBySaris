package service.imp;

import dto.request.CategoriaRequest;
import dto.response.CategoriaResponse;
import mapper.CategoriaMapper;
import model.Categoria;
import repository.CategoriaRepository;
import service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Override
    public CategoriaResponse registrarCategoria(CategoriaRequest dto) {

        if (categoriaRepository.existsByNombre(dto.nombre())) {
            throw new IllegalArgumentException("La categoría ya se encuentra registrada");
        }

        Categoria categoria = categoriaMapper.DtoToEntity(dto);

        Categoria categoriaGuardada = categoriaRepository.save(categoria);

        return categoriaMapper.entityToDto(categoriaGuardada);
    }

    @Override
    public List<CategoriaResponse> listarCategorias() {

        List<Categoria> categorias = categoriaRepository.findAll();

        return categorias.stream()
                .map(categoriaMapper::entityToDto)
                .toList();
    }

    @Override
    public CategoriaResponse obtenerCategoriaPorId(Long idCategoria) {

        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        return categoriaMapper.entityToDto(categoria);
    }

    @Override
    public CategoriaResponse actualizarCategoria(Long idCategoria, CategoriaRequest dto) {

        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        boolean existe = categoriaRepository.existsByNombre(dto.nombre());

        if (existe && !categoria.getNombre().equals(dto.nombre())) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }

        categoriaMapper.updateEntityFromDto(dto, categoria);

        Categoria categoriaActualizada = categoriaRepository.save(categoria);

        return categoriaMapper.entityToDto(categoriaActualizada);
    }

    @Override
    public void eliminarCategoria(Long idCategoria) {

        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        categoriaRepository.delete(categoria);
    }
}
