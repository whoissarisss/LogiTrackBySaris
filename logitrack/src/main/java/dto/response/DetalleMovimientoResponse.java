package com.saris.logitrack.dto.response;

import java.math.BigDecimal;
import com.saris.logitrack.dto.response.ProductoResponse;

public record DetalleMovimientoResponse(

        Long idDetalle,
        ProductoResponse producto,
        BigDecimal cantidad

) {
}
