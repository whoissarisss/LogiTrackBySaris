package com.saris.logitrack.mapper;

import com.saris.logitrack.dto.request.UsuarioRequest;
import com.saris.logitrack.dto.response.UsuarioResponse;
import com.saris.logitrack.model.EstadoSolicitud;
import com.saris.logitrack.model.Rol;
import com.saris.logitrack.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioRequest request) {

        Usuario usuario = new Usuario();

        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setCorreo(request.correo());
        usuario.setPassword(request.password());
        usuario.setRol(Rol.EMPLEADO);
        usuario.setEstadoSolicitud(EstadoSolicitud.PENDIENTE);

        return usuario;
    }

    public UsuarioResponse toResponse(Usuario usuario) {

        return new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getCorreo(),
                usuario.getRol(),
                usuario.getEstadoSolicitud(),
                usuario.getFechaRegistro()
        );
    }
}
