package dto.response;

public record ProductoResponse(

        Long idProducto,
        String nombre,
        String descripcion,
        Boolean estado,
        MarcaResponse marca,
        CategoriaResponse categoria

) {
}