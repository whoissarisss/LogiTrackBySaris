package com.saris.logitrack.dto.response;

import com.saris.logitrack.model.EstadoSolicitud;
import com.saris.logitrack.model.Rol;

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
