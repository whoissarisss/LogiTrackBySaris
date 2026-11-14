package config;

import model.EstadoSolicitud;
import model.Rol;
import model.Usuario;
import repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class AdminInitializer {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${app.admin.nombre}")
    private String nombre;

    @Value("${app.admin.apellido}")
    private String apellido;

    @Value("${app.admin.correo}")
    private String correo;

    @Value("${app.admin.password}")
    private String password;

    @Bean
    public CommandLineRunner crearAdministradorInicial() {
        return args -> {
            if (!usuarioRepository.existsByCorreo(correo)) {
                Usuario admin = new Usuario();
                admin.setNombre(nombre);
                admin.setApellido(apellido);
                admin.setCorreo(correo);
                admin.setPassword(passwordEncoder.encode(password));
                admin.setRol(Rol.ADMIN);
                admin.setEstadoSolicitud(EstadoSolicitud.APROBADO);
                admin.setFechaRevision(LocalDateTime.now());
                admin.setMotivoRechazo(null);
                usuarioRepository.save(admin);
                System.out.println("Administrador inicial creado correctamente.");
            } else {
                System.out.println("El administrador ya se encuentra registrado.");
            }
        };
    }
}
