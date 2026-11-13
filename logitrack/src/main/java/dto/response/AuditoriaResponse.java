package dto.response;

import model.Operacion;

import java.time.LocalDateTime;

public record AuditoriaResponse(

        Long idAuditoria,
        LocalDateTime fechaHora,
        Operacion operacion,
        UsuarioResponse usuario,
        String tablaAfectada,
        Long registroId,
        String valoresAnteriores,
        String valoresNuevos

) {
}
