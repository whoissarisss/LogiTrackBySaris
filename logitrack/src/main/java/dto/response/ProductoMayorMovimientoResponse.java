package dto.response;

import java.math.BigDecimal;

public record ProductoMayorMovimientoResponse(
        Long idProducto,
        String nombreProducto,
        BigDecimal totalMovilizado
) {
}