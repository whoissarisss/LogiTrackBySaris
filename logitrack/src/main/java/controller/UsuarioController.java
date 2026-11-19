package controller;

import dto.request.UsuarioRequest;
import dto.response.UsuarioResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(
        name = "Usuarios",
        description = "Administración de usuarios, solicitudes de registro y aprobación de empleados"
)
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/registro")
    @Operation(
            summary = "Registrar usuario",
            description = "Registra un nuevo usuario en el sistema. La solicitud queda pendiente de aprobación."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos"),
            @ApiResponse(responseCode = "409", description = "El usuario ya se encuentra registrado")
    })
    public ResponseEntity<UsuarioResponse> registrarUsuario(
            @Valid @RequestBody UsuarioRequest dto) {

        UsuarioResponse respuesta = usuarioService.registrarUsuario(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/pendientes")
    @Operation(
            summary = "Listar solicitudes pendientes",
            description = "Obtiene la lista de usuarios que están pendientes de aprobación por parte del administrador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitudes pendientes consultadas correctamente")
    })
    public ResponseEntity<List<UsuarioResponse>> listarSolicitudesPendientes() {

        List<UsuarioResponse> usuarios =
                usuarioService.listarSolicitudesPendientes();

        return ResponseEntity.ok(usuarios);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/empleados-aprobados")
    @Operation(
            summary = "Listar empleados aprobados",
            description = "Consulta los usuarios cuyo registro ha sido aprobado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleados aprobados consultados correctamente")
    })
    public ResponseEntity<List<UsuarioResponse>> listarEmpleadosAprobados() {

        List<UsuarioResponse> usuarios =
                usuarioService.listarUsuariosAprobados();

        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("/{idUsuario}/aprobar")
    @Operation(
            summary = "Aprobar solicitud de usuario",
            description = "Aprueba la solicitud de registro de un usuario identificado por su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud aprobada correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UsuarioResponse> aprobarSolicitud(
            @Parameter(description = "Identificador del usuario", example = "1")
            @PathVariable Integer idUsuario) {

        UsuarioResponse respuesta =
                usuarioService.aprobarSolicitud(idUsuario);

        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{idUsuario}/rechazar")
    @Operation(
            summary = "Rechazar solicitud de usuario",
            description = "Rechaza la solicitud de registro de un usuario y recibe el motivo del rechazo."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud rechazada correctamente"),
            @ApiResponse(responseCode = "400", description = "El motivo del rechazo no es válido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UsuarioResponse> rechazarSolicitud(
            @Parameter(description = "Identificador del usuario", example = "1")
            @PathVariable Integer idUsuario,

            @Parameter(description = "Motivo por el cual se rechaza la solicitud", example = "Información de registro incompleta")
            @RequestParam String motivo) {

        UsuarioResponse respuesta =
                usuarioService.rechazarSolicitud(idUsuario, motivo);

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{idUsuario}")
    @Operation(
            summary = "Consultar usuario por ID",
            description = "Obtiene la información de un usuario mediante su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UsuarioResponse> obtenerUsuarioPorId(
            @Parameter(description = "Identificador del usuario", example = "1")
            @PathVariable Integer idUsuario) {

        UsuarioResponse respuesta =
                usuarioService.obtenerUsuarioPorId(idUsuario);

        return ResponseEntity.ok(respuesta);
    }
}
