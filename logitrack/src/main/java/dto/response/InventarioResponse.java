package com.saris.logitrack.dto.response;

import java.math.BigDecimal;

public record InventarioResponse(

        Long idInventario,
        BodegaResponse bodega,
        ProductoResponse producto,
        BigDecimal stockActual

) {
}
