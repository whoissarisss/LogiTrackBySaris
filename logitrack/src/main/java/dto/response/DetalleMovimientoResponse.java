package dto.response;

import java.math.BigDecimal;

public record DetalleMovimientoResponse(

        Integer idDetalle,
        ProductoResponse producto,
        BigDecimal cantidad

) {
}
