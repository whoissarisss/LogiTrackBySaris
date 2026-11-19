package dto.response;

import model.Operacion;
import java.time.LocalDateTime;

public record AuditoriaResponse(

        Integer idAuditoria,
        LocalDateTime fechaHora,
        Operacion operacion,
        UsuarioResponse usuario,
        String tablaAfectada,
        Integer registroId,
        String valoresAnteriores,
        String valoresNuevos

) {
}