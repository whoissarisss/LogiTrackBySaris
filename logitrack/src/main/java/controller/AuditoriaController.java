package controller;

import dto.response.AuditoriaResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import model.Operacion;
import service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/auditorias")
@RequiredArgsConstructor
@Tag(name = "Auditorías", description = "Consultas del historial de operaciones y registros de auditoría del sistema")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping
    @Operation(
            summary = "Listar todas las auditorías",
            description = "Obtiene el listado de registros de auditoría almacenados en el sistema."
    )
    @ApiResponse(responseCode = "200", description = "Auditorías obtenidas exitosamente")
    public ResponseEntity<List<AuditoriaResponse>> listarAuditorias() {

        List<AuditoriaResponse> auditorias =
                auditoriaService.listarAuditorias();

        return ResponseEntity.ok(auditorias);
    }

    @GetMapping("/{idAuditoria}")
    @Operation(
            summary = "Consultar auditoría por ID",
            description = "Obtiene la información de un registro de auditoría específico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro de auditoría encontrado"),
            @ApiResponse(responseCode = "404", description = "Registro de auditoría no encontrado")
    })
    public ResponseEntity<AuditoriaResponse> obtenerAuditoriaPorId(
            @Parameter(description = "Identificador del registro de auditoría", example = "1")
            @PathVariable Long idAuditoria) {

        AuditoriaResponse respuesta =
                auditoriaService.obtenerAuditoriaPorId(idAuditoria);

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(
            summary = "Listar auditorías por usuario",
            description = "Consulta los registros de auditoría asociados a un usuario específico."
    )
    @ApiResponse(responseCode = "200", description = "Auditorías del usuario obtenidas exitosamente")
    public ResponseEntity<List<AuditoriaResponse>> listarAuditoriasPorUsuario(
            @Parameter(description = "Identificador del usuario", example = "1")
            @PathVariable Long idUsuario) {

        List<AuditoriaResponse> auditorias =
                auditoriaService.listarAuditoriasPorUsuario(idUsuario);

        return ResponseEntity.ok(auditorias);
    }

    @GetMapping("/tabla/{tablaAfectada}")
    @Operation(
            summary = "Listar auditorías por tabla",
            description = "Consulta los registros de auditoría relacionados con una tabla específica de la base de datos."
    )
    @ApiResponse(responseCode = "200", description = "Auditorías de la tabla obtenidas exitosamente")
    public ResponseEntity<List<AuditoriaResponse>> listarAuditoriasPorTabla(
            @Parameter(description = "Nombre de la tabla afectada", example = "producto")
            @PathVariable String tablaAfectada) {

        List<AuditoriaResponse> auditorias =
                auditoriaService.listarAuditoriasPorTabla(tablaAfectada);

        return ResponseEntity.ok(auditorias);
    }

    @GetMapping("/operacion/{operacion}")
    @Operation(
            summary = "Listar auditorías por operación",
            description = "Consulta los registros de auditoría según el tipo de operación realizada."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Auditorías filtradas por operación"),
            @ApiResponse(responseCode = "400", description = "Tipo de operación inválido")
    })
    public ResponseEntity<List<AuditoriaResponse>> listarAuditoriasPorOperacion(
            @Parameter(description = "Tipo de operación", example = "INSERT")
            @PathVariable Operacion operacion) {

        List<AuditoriaResponse> auditorias =
                auditoriaService.listarAuditoriasPorOperacion(operacion);

        return ResponseEntity.ok(auditorias);
    }
}
