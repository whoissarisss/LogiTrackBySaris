package service;

import dto.request.CategoriaRequest;
import dto.response.CategoriaResponse;

import java.util.List;

public interface CategoriaService {

    CategoriaResponse registrarCategoria(CategoriaRequest dto);

    List<CategoriaResponse> listarCategorias();

    CategoriaResponse obtenerCategoriaPorId(Long idCategoria);

    CategoriaResponse actualizarCategoria(Long idCategoria, CategoriaRequest dto);

    void eliminarCategoria(Long idCategoria);
}

