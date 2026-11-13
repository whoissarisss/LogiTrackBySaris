package service.imp;

import dto.request.UsuarioRequest;
import dto.response.UsuarioResponse;
import mapper.UsuarioMapper;
import model.EstadoSolicitud;
import model.Usuario;
import repository.UsuarioRepository;
import service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioResponse registrarUsuario(UsuarioRequest dto) {

        if (usuarioRepository.existsByCorreo(dto.correo())) {
            throw new IllegalArgumentException("El correo ya se encuentra registrado");
        }

        Usuario usuario = usuarioMapper.DtoToEntity(dto);
        usuario.setPassword(passwordEncoder.encode(dto.password()));

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        return usuarioMapper.entityToDto(usuarioGuardado);
    }

    @Override
    public List<UsuarioResponse> listarSolicitudesPendientes() {

        List<Usuario> usuarios = usuarioRepository.findByEstadoSolicitud(
                EstadoSolicitud.PENDIENTE
        );

        return usuarios.stream()
                .map(usuarioMapper::entityToDto)
                .toList();
    }

    @Override
    public UsuarioResponse aprobarSolicitud(Long idUsuario) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (usuario.getEstadoSolicitud() != EstadoSolicitud.PENDIENTE) {
            throw new IllegalStateException("La solicitud ya fue revisada");
        }

        usuario.setEstadoSolicitud(EstadoSolicitud.APROBADO);
        usuario.setFechaRevision(LocalDateTime.now());
        usuario.setMotivoRechazo(null);

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return usuarioMapper.entityToDto(usuarioActualizado);
    }

    @Override
    public UsuarioResponse rechazarSolicitud(Long idUsuario, String motivo) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (usuario.getEstadoSolicitud() != EstadoSolicitud.PENDIENTE) {
            throw new IllegalStateException("La solicitud ya fue revisada");
        }

        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Debe indicar el motivo del rechazo");
        }

        usuario.setEstadoSolicitud(EstadoSolicitud.RECHAZADO);
        usuario.setFechaRevision(LocalDateTime.now());
        usuario.setMotivoRechazo(motivo);

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return usuarioMapper.entityToDto(usuarioActualizado);
    }

    @Override
    public UsuarioResponse obtenerUsuarioPorId(Long idUsuario) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return usuarioMapper.entityToDto(usuario);
    }
}
