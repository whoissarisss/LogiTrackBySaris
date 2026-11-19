package dto.response;

import java.math.BigDecimal;

public record ProductoMayorMovimientoResponse(
        Integer idProducto,
        String nombreProducto,
        BigDecimal totalMovilizado
) {
}