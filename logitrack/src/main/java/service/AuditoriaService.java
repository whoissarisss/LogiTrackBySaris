package service;

import model.Operacion;
import dto.response.AuditoriaResponse;

import java.util.List;

public interface AuditoriaService {

    List<AuditoriaResponse> listarAuditorias();

    AuditoriaResponse obtenerAuditoriaPorId(Integer idAuditoria);

    List<AuditoriaResponse> listarAuditoriasPorUsuario(Integer idUsuario);

    List<AuditoriaResponse> listarAuditoriasPorTabla(String tablaAfectada);

    List<AuditoriaResponse> listarAuditoriasPorOperacion(Operacion operacion);
}