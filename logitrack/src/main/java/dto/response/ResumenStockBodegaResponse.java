package dto.response;

import java.math.BigDecimal;

public record ResumenStockBodegaResponse(
        Integer idBodega,
        String nombreBodega,
        Integer capacidad,
        BigDecimal totalStock,
        Long cantidadProductos
) {
}