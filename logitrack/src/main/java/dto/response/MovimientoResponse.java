package dto.response;

import model.TipoMovimiento;

import java.time.LocalDateTime;
import java.util.List;

public record MovimientoResponse(

        Integer idMovimiento,
        LocalDateTime fecha,
        TipoMovimiento tipoMovimiento,
        UsuarioResponse usuario,
        BodegaResponse bodegaOrigen,
        BodegaResponse bodegaDestino,
        List<DetalleMovimientoResponse> detalles

) {
}
