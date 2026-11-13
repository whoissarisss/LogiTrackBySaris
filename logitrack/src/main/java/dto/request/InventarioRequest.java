package dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InventarioRequest(

        @NotNull(message = "La bodega es obligatoria")
        @Positive(message = "El identificador de la bodega debe ser positivo")
        Long idBodega,

        @NotNull(message = "El producto es obligatorio")
        @Positive(message = "El identificador del producto debe ser positivo")
        Long idProducto

) {
}
