package service;

import model.Operacion;
import dto.response.AuditoriaResponse;

import java.util.List;

public interface AuditoriaService {

    List<AuditoriaResponse> listarAuditorias();

    AuditoriaResponse obtenerAuditoriaPorId(Long idAuditoria);

    List<AuditoriaResponse> listarAuditoriasPorUsuario(Long idUsuario);

    List<AuditoriaResponse> listarAuditoriasPorTabla(String tablaAfectada);

    List<AuditoriaResponse> listarAuditoriasPorOperacion(Operacion operacion);
}
