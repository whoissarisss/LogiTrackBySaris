package dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DetalleMovimientoRequest(

        @NotNull(message = "El producto es obligatorio")
        @Positive(message = "El identificador del producto debe ser positivo")
        Long idProducto,

        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0.001", message = "La cantidad debe ser mayor que cero")
        BigDecimal cantidad

) {
}
