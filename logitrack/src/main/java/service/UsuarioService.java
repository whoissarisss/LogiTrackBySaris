package service;

import dto.request.UsuarioRequest;
import dto.response.UsuarioResponse;

import java.util.List;

public interface UsuarioService {

    UsuarioResponse registrarUsuario(UsuarioRequest dto);

    List<UsuarioResponse> listarSolicitudesPendientes();

    List<UsuarioResponse> listarUsuariosAprobados();

    UsuarioResponse aprobarSolicitud(Integer idUsuario);

    UsuarioResponse rechazarSolicitud(Integer idUsuario, String motivo);

    UsuarioResponse obtenerUsuarioPorId(Integer idUsuario);
}
