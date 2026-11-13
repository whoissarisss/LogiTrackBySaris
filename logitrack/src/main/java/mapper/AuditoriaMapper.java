package mapper;

import dto.response.AuditoriaResponse;
import dto.response.UsuarioResponse;
import model.Auditoria;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaMapper {

    public AuditoriaResponse entityToDto(Auditoria auditoria, UsuarioResponse usuario) {
        if (auditoria == null) {
            return null;
        }

        return new AuditoriaResponse(
                auditoria.getIdAuditoria(),
                auditoria.getFechaHora(),
                auditoria.getOperacion(),
                usuario,
                auditoria.getTablaAfectada(),
                auditoria.getRegistroId(),
                auditoria.getValoresAnteriores(),
                auditoria.getValoresNuevos()
        );
    }
}
