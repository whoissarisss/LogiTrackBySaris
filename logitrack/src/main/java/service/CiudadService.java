package service;

import dto.request.CiudadRequest;
import dto.response.CiudadResponse;

import java.util.List;

public interface CiudadService {

    CiudadResponse registrarCiudad(CiudadRequest dto);

    List<CiudadResponse> listarCiudades();

    CiudadResponse obtenerCiudadPorId(Integer idCiudad);

    CiudadResponse actualizarCiudad(Integer idCiudad, CiudadRequest dto);

    void eliminarCiudad(Integer idCiudad);
}
