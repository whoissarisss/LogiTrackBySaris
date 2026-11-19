package service;

import dto.request.MarcaRequest;
import dto.response.MarcaResponse;

import java.util.List;

public interface MarcaService {

    MarcaResponse registrarMarca(MarcaRequest dto);

    List<MarcaResponse> listarMarcas();

    MarcaResponse obtenerMarcaPorId(Integer idMarca);

    MarcaResponse actualizarMarca(Integer idMarca, MarcaRequest dto);

    void eliminarMarca(Integer idMarca);
}
