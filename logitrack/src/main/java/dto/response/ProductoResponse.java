package dto.response;

public record ProductoResponse(

        Integer idProducto,
        String nombre,
        String descripcion,
        Boolean estado,
        MarcaResponse marca,
        CategoriaResponse categoria

) {
}