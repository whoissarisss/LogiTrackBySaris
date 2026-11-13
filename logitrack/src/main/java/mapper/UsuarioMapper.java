package com.saris.logitrack.mapper;

import com.saris.logitrack.dto.request.UsuarioRequest;
import com.saris.logitrack.dto.response.UsuarioResponse;
import com.saris.logitrack.model.EstadoSolicitud;
import com.saris.logitrack.model.Rol;
import com.saris.logitrack.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponse entityToDto(Usuario usuario) {
        if (usuario == null) return null;
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

    public Usuario DtoToEntity(UsuarioRequest dto) {
        if (dto == null) return null;
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.nombre());
        usuario.setApellido(dto.apellido());
        usuario.setCorreo(dto.correo());
        usuario.setPassword(dto.password());
        usuario.setRol(Rol.EMPLEADO);
        usuario.setEstadoSolicitud(EstadoSolicitud.PENDIENTE);
        return usuario;
    }
}
