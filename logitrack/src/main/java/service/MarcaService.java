package service;

import dto.request.MarcaRequest;
import dto.response.MarcaResponse;

import java.util.List;

public interface MarcaService {

    MarcaResponse registrarMarca(MarcaRequest dto);

    List<MarcaResponse> listarMarcas();

    MarcaResponse obtenerMarcaPorId(Long idMarca);

    MarcaResponse actualizarMarca(Long idMarca, MarcaRequest dto);

    void eliminarMarca(Long idMarca);
}
