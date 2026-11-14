package service.imp;

import config.JwtService;
import dto.request.LoginRequest;
import dto.response.LoginResponse;
import model.EstadoSolicitud;
import model.Usuario;
import repository.UsuarioRepository;
import service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponse iniciarSesion(LoginRequest request) {

        // Buscar usuario por correo
        Usuario usuario = usuarioRepository.findByCorreo(request.correo())
                .orElseThrow(() ->
                        new IllegalArgumentException("Correo o contraseña incorrectos")
                );

        // Verificar contraseña
        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new IllegalArgumentException("Correo o contraseña incorrectos");
        }

        // Verificar aprobación de la solicitud
        if (usuario.getEstadoSolicitud() != EstadoSolicitud.APROBADO) {
            throw new IllegalStateException(
                    "Tu solicitud de registro todavía no está aprobada"
            );
        }

        // Generar token JWT
        String token = jwtService.generateToken(
                usuario.getCorreo(),
                usuario.getRol().name()
        );

        // Devolver información del usuario
        return new LoginResponse(
                token,
                "Bearer",
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getCorreo(),
                usuario.getRol()
        );
    }
}
