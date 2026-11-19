package service;

import dto.request.BodegaRequest;
import dto.response.BodegaResponse;

import java.util.List;

public interface BodegaService {

    BodegaResponse registrarBodega(BodegaRequest dto);

    List<BodegaResponse> listarBodegas();

    BodegaResponse obtenerBodegaPorId(Integer idBodega);

    List<BodegaResponse> listarBodegasPorCiudad(Integer idCiudad);

    BodegaResponse actualizarBodega(Integer idBodega, BodegaRequest dto);

    void eliminarBodega(Integer idBodega);
}
