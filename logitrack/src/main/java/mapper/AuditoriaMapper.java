package com.saris.logitrack.mapper;

import com.saris.logitrack.dto.response.AuditoriaResponse;
import com.saris.logitrack.dto.response.UsuarioResponse;
import com.saris.logitrack.model.Auditoria;
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
