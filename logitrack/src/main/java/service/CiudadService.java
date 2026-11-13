package service;

import dto.request.CiudadRequest;
import dto.response.CiudadResponse;

import java.util.List;

public interface CiudadService {

    CiudadResponse registrarCiudad(CiudadRequest dto);

    List<CiudadResponse> listarCiudades();

    CiudadResponse obtenerCiudadPorId(Long idCiudad);

    CiudadResponse actualizarCiudad(Long idCiudad, CiudadRequest dto);

    void eliminarCiudad(Long idCiudad);
}
