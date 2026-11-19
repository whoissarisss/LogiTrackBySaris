package dto.response;

import model.Rol;

public record LoginResponse(

        String token,
        String tipo,
        Integer idUsuario,
        String nombre,
        String apellido,
        String correo,
        Rol rol

) {
}
