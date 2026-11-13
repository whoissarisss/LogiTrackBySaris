package com.saris.logitrack.dto.response;

import java.math.BigDecimal;

public record DetalleMovimientoResponse(

        Long idDetalle,
        ProductoResponse producto,
        BigDecimal cantidad

) {
}
