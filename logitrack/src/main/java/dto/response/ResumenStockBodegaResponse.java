package dto.response;

import java.math.BigDecimal;

public record ResumenStockBodegaResponse(
        Long idBodega,
        String nombreBodega,
        Integer capacidad,
        BigDecimal totalStock,
        Long cantidadProductos
) {
}
