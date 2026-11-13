package service;

import dto.request.UsuarioRequest;
import dto.response.UsuarioResponse;

import java.util.List;

public interface UsuarioService {

    UsuarioResponse registrarUsuario(UsuarioRequest dto);

    List<UsuarioResponse> listarSolicitudesPendientes();

    UsuarioResponse aprobarSolicitud(Long idUsuario);

    UsuarioResponse rechazarSolicitud(Long idUsuario, String motivo);

    UsuarioResponse obtenerUsuarioPorId(Long idUsuario);
}
