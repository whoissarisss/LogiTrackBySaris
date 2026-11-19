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

            // Validar configuración inicial
            if (nombre == null || nombre.isBlank()
                    || apellido == null || apellido.isBlank()
                    || correo == null || correo.isBlank()
                    || password == null || password.isBlank()) {

                throw new IllegalStateException(
                        "Faltan variables de configuración del administrador inicial."
                );
            }

            // Comprobar si el correo ya existe
            Usuario usuarioExistente = usuarioRepository
                    .findByCorreo(correo)
                    .orElse(null);

            if (usuarioExistente != null) {

                if (usuarioExistente.getRol() != Rol.ADMIN) {
                    throw new IllegalStateException(
                            "El correo configurado para el administrador ya pertenece a un empleado."
                    );
                }

                System.out.println(
                        "El administrador inicial ya se encuentra registrado."
                );

                return;
            }

            // Crear administrador inicial
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

            System.out.println(
                    "Administrador inicial creado correctamente."
            );
        };
    }
}
