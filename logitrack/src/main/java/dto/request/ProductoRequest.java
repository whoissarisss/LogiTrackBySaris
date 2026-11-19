package dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductoRequest(

        @NotBlank(message = "El nombre del producto es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
        String nombre,

        @Size(max = 300, message = "La descripción no puede superar los 300 caracteres")
        String descripcion,

        @NotNull(message = "La marca es obligatoria")
        @Positive(message = "El identificador de la marca debe ser positivo")
        Integer idMarca,

        @NotNull(message = "La categoría es obligatoria")
        @Positive(message = "El identificador de la categoría debe ser positivo")
        Integer idCategoria

) {
}