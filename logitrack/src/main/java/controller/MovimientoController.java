package controller;

import dto.request.MovimientoRequest;
import dto.response.MovimientoResponse;
import dto.response.ProductoMayorMovimientoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import model.TipoMovimiento;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.MovimientoService;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
@Tag(
        name = "Movimientos",
        description = "Operaciones para registrar y consultar movimientos de inventario"
)
public class MovimientoController {

    private final MovimientoService movimientoService;

    // ==========================================
    // REGISTRAR MOVIMIENTO
    // ==========================================

    @PostMapping
    @Operation(
            summary = "Registrar un movimiento",
            description = "Registra un movimiento de inventario, como una entrada, salida o transferencia."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Movimiento registrado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de la solicitud inválidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario no autenticado"
            )
    })
    public ResponseEntity<MovimientoResponse> registrarMovimiento(
            @Valid @RequestBody MovimientoRequest dto) {

        MovimientoResponse respuesta =
                movimientoService.registrarMovimiento(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    // ==========================================
    // LISTAR MOVIMIENTOS
    // ==========================================

    @GetMapping
    @Operation(
            summary = "Listar movimientos",
            description = "El administrador consulta todos los movimientos y el empleado únicamente los que ha registrado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado de movimientos obtenido exitosamente"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario no autenticado"
            )
    })
    public ResponseEntity<List<MovimientoResponse>> listarMovimientos() {

        List<MovimientoResponse> movimientos =
                movimientoService.listarMovimientos();

        return ResponseEntity.ok(movimientos);
    }

    // ==========================================
    // OBTENER MOVIMIENTO POR ID
    // ==========================================

    @GetMapping("/{idMovimiento}")
    @Operation(
            summary = "Consultar movimiento por ID",
            description = "Obtiene los detalles de un movimiento específico, validando los permisos del usuario."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Movimiento encontrado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No tiene permiso para consultar este movimiento"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Movimiento no encontrado"
            )
    })
    public ResponseEntity<MovimientoResponse> obtenerMovimientoPorId(
            @Parameter(
                    description = "Identificador del movimiento",
                    example = "1"
            )
            @PathVariable("idMovimiento") Long idMovimiento) {

        MovimientoResponse respuesta =
                movimientoService.obtenerMovimientoPorId(idMovimiento);

        return ResponseEntity.ok(respuesta);
    }

    // ==========================================
    // LISTAR MOVIMIENTOS POR USUARIO
    // ==========================================

    @GetMapping("/usuario/{idUsuario}")
    @Operation(
            summary = "Listar movimientos por usuario",
            description = "Consulta los movimientos registrados por un usuario. El empleado solo puede consultar los propios."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Movimientos del usuario obtenidos exitosamente"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No tiene permiso para consultar movimientos de otro usuario"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            )
    })
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosPorUsuario(
            @Parameter(
                    description = "Identificador del usuario",
                    example = "1"
            )
            @PathVariable("idUsuario") Long idUsuario) {

        List<MovimientoResponse> movimientos =
                movimientoService.listarMovimientosPorUsuario(idUsuario);

        return ResponseEntity.ok(movimientos);
    }

    // ==========================================
    // LISTAR MOVIMIENTOS POR TIPO
    // ==========================================

    @GetMapping("/tipo/{tipoMovimiento}")
    @Operation(
            summary = "Listar movimientos por tipo",
            description = "Filtra los movimientos según su tipo de operación."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Movimientos filtrados por tipo"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Tipo de movimiento no válido"
            )
    })
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosPorTipo(
            @Parameter(
                    description = "Tipo de movimiento",
                    example = "ENTRADA"
            )
            @PathVariable("tipoMovimiento") TipoMovimiento tipoMovimiento) {

        List<MovimientoResponse> movimientos =
                movimientoService.listarMovimientosPorTipo(
                        tipoMovimiento.name()
                );

        return ResponseEntity.ok(movimientos);
    }

    // ==========================================
    // LISTAR MOVIMIENTOS POR BODEGA
    // ==========================================

    @GetMapping("/bodega/{idBodega}")
    @Operation(
            summary = "Listar movimientos por bodega",
            description = "Consulta los movimientos asociados a una bodega específica, respetando los permisos del usuario."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Movimientos de la bodega obtenidos exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Bodega no encontrada"
            )
    })
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosPorBodega(
            @Parameter(
                    description = "Identificador de la bodega",
                    example = "1"
            )
            @PathVariable("idBodega") Long idBodega) {

        List<MovimientoResponse> movimientos =
                movimientoService.listarMovimientosPorBodega(idBodega);

        return ResponseEntity.ok(movimientos);
    }

    // ==========================================
    // LISTAR MOVIMIENTOS ENTRE FECHAS
    // ==========================================

    @GetMapping("/fechas")
    @Operation(
            summary = "Consultar movimientos entre fechas",
            description = "Obtiene los movimientos registrados dentro de un rango de fechas y horas, respetando los permisos del usuario."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Movimientos del periodo obtenidos exitosamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Formato o rango de fechas inválido"
            )
    })
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosEntreFechas(

            @Parameter(
                    description = "Fecha y hora inicial en formato ISO",
                    example = "2026-01-01T00:00:00"
            )
            @RequestParam("fechaInicio")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaInicio,

            @Parameter(
                    description = "Fecha y hora final en formato ISO",
                    example = "2026-01-31T23:59:59"
            )
            @RequestParam("fechaFin")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaFin) {

        List<MovimientoResponse> movimientos =
                movimientoService.listarMovimientosEntreFechas(
                        fechaInicio,
                        fechaFin
                );

        return ResponseEntity.ok(movimientos);
    }

    // ==========================================
    // PRODUCTOS CON MAYOR MOVIMIENTO
    // ==========================================

    @GetMapping("/productos-mayor-movimiento")
    @Operation(
            summary = "Consultar productos con mayor movimiento",
            description = "Obtiene el listado de productos con mayor cantidad de movimientos de inventario. Disponible para administradores."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reporte obtenido exitosamente"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No tiene permisos para consultar este reporte"
            )
    })
    public ResponseEntity<List<ProductoMayorMovimientoResponse>> listarProductosConMayorMovimiento() {

        List<ProductoMayorMovimientoResponse> productos =
                movimientoService.listarProductosConMayorMovimiento();

        return ResponseEntity.ok(productos);
    }
}
