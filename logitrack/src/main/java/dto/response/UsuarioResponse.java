package dto.response;

import model.EstadoSolicitud;
import model.Rol;

import java.time.LocalDateTime;

public record UsuarioResponse(

        Long idUsuario,
        String nombre,
        String apellido,
        String correo,
        Rol rol,
        EstadoSolicitud estadoSolicitud,
        LocalDateTime fechaRegistro

) {
}
