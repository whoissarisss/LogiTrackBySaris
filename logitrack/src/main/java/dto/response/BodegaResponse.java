package dto.response;


public record BodegaResponse(

        Long idBodega,
        String nombre,
        Integer capacidad,
        CiudadResponse ciudad,
        UsuarioResponse encargado

) {
}