package com.saris.logitrack.dto.response;

import com.saris.logitrack.dto.response.BodegaResponse;
import com.saris.logitrack.dto.response.ProductoResponse;
import java.math.BigDecimal;

public record InventarioResponse(

        Long idInventario,
        BodegaResponse bodega,
        ProductoResponse producto,
        BigDecimal stockActual

) {
}
