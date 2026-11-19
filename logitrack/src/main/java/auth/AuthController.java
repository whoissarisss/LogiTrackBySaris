package auth;

import dto.request.LoginRequest;
import dto.response.LoginResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Operaciones de autenticación de usuarios en Bake Track")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica al usuario mediante sus credenciales y devuelve la respuesta de inicio de sesión."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso"),
            @ApiResponse(responseCode = "400", description = "Datos de inicio de sesión inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas o usuario no autorizado")
    })
    public ResponseEntity<LoginResponse> iniciarSesion(
            @Valid @RequestBody LoginRequest request
    ) {

        LoginResponse response = authService.iniciarSesion(request);

        return ResponseEntity.ok(response);
    }
}
