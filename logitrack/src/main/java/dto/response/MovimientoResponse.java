package com.saris.logitrack.dto.response;

import com.saris.logitrack.model.TipoMovimiento;

import java.time.LocalDateTime;
import java.util.List;

public record MovimientoResponse(

        Long idMovimiento,
        LocalDateTime fecha,
        TipoMovimiento tipoMovimiento,
        UsuarioResponse usuario,
        BodegaResponse bodegaOrigen,
        BodegaResponse bodegaDestino,
        List<DetalleMovimientoResponse> detalles

) {
}
