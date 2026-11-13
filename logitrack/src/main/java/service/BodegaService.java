package service;

import dto.request.BodegaRequest;
import dto.response.BodegaResponse;

import java.util.List;

public interface BodegaService {

    BodegaResponse registrarBodega(BodegaRequest dto);

    List<BodegaResponse> listarBodegas();

    BodegaResponse obtenerBodegaPorId(Long idBodega);

    List<BodegaResponse> listarBodegasPorCiudad(Long idCiudad);

    BodegaResponse actualizarBodega(Long idBodega, BodegaRequest dto);

    void eliminarBodega(Long idBodega);
}
