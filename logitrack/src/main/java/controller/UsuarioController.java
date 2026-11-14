
package controller;

import dto.request.UsuarioRequest;
import dto.response.UsuarioResponse;
import service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrarUsuario(
            @Valid @RequestBody UsuarioRequest dto) {

        UsuarioResponse respuesta = usuarioService.registrarUsuario(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<UsuarioResponse>> listarSolicitudesPendientes() {

        List<UsuarioResponse> usuarios =
                usuarioService.listarSolicitudesPendientes();

        return ResponseEntity.ok(usuarios);
    }

    @PatchMapping("/{idUsuario}/aprobar")
    public ResponseEntity<UsuarioResponse> aprobarSolicitud(
            @PathVariable Long idUsuario) {

        UsuarioResponse respuesta =
                usuarioService.aprobarSolicitud(idUsuario);

        return ResponseEntity.ok(respuesta);
    }

    @PatchMapping("/{idUsuario}/rechazar")
    public ResponseEntity<UsuarioResponse> rechazarSolicitud(
            @PathVariable Long idUsuario,
            @RequestParam String motivo) {

        UsuarioResponse respuesta =
                usuarioService.rechazarSolicitud(idUsuario, motivo);

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<UsuarioResponse> obtenerUsuarioPorId(
            @PathVariable Long idUsuario) {

        UsuarioResponse respuesta =
                usuarioService.obtenerUsuarioPorId(idUsuario);

        return ResponseEntity.ok(respuesta);
    }
}
