package dto.response;

import java.math.BigDecimal;

public record InventarioResponse(

        Integer idInventario,
        BodegaResponse bodega,
        ProductoResponse producto,
        BigDecimal stockActual

) {
}
