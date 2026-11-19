package dto.response;

public record BodegaResponse(

        Integer idBodega,
        String nombre,
        Integer capacidad,
        CiudadResponse ciudad,
        UsuarioResponse encargado

) {
}