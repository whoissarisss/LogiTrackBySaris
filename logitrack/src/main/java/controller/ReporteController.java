package controller;

import dto.response.InventarioResponse;
import dto.response.MovimientoResponse;
import dto.response.ProductoMayorMovimientoResponse;
import dto.response.ResumenStockBodegaResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import service.ReporteService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Tag(
        name = "Reportes",
        description = "Consultas y reportes generales de inventario y movimientos de Bake Track"
)
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/inventario")
    @Operation(
            summary = "Reporte general de inventario",
            description = "Obtiene el inventario general registrado en el sistema."
    )
    @ApiResponse(responseCode = "200", description = "Reporte general de inventario obtenido exitosamente")
    public ResponseEntity<List<InventarioResponse>> obtenerInventarioGeneral() {

        return ResponseEntity.ok(
                reporteService.obtenerInventarioGeneral()
        );
    }

    @GetMapping("/inventario/bodega/{idBodega}")
    @Operation(
            summary = "Reporte de inventario por bodega",
            description = "Consulta las existencias de inventario correspondientes a una bodega específica."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte de inventario por bodega obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Bodega no encontrada")
    })
    public ResponseEntity<List<InventarioResponse>> obtenerInventarioPorBodega(
            @Parameter(description = "Identificador de la bodega", example = "1")
            @PathVariable Long idBodega) {

        return ResponseEntity.ok(
                reporteService.obtenerInventarioPorBodega(idBodega)
        );
    }

    @GetMapping("/inventario/producto/{idProducto}")
    @Operation(
            summary = "Reporte de inventario por producto",
            description = "Consulta las existencias de un producto específico en las bodegas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte de inventario por producto obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<List<InventarioResponse>> obtenerInventarioPorProducto(
            @Parameter(description = "Identificador del producto", example = "1")
            @PathVariable Long idProducto) {

        return ResponseEntity.ok(
                reporteService.obtenerInventarioPorProducto(idProducto)
        );
    }

    @GetMapping("/inventario/stock-bajo")
    @Operation(
            summary = "Reporte de productos con stock bajo",
            description = "Obtiene los registros de inventario cuyo stock se encuentra por debajo del límite indicado."
    )
    @ApiResponse(responseCode = "200", description = "Reporte de stock bajo obtenido exitosamente")
    public ResponseEntity<List<InventarioResponse>> obtenerProductosConStockBajo(
            @Parameter(description = "Límite de stock para realizar el filtro", example = "10")
            @RequestParam BigDecimal limite) {

        return ResponseEntity.ok(
                reporteService.obtenerProductosConStockBajo(limite)
        );
    }

    @GetMapping("/inventario/resumen-bodegas")
    @Operation(
            summary = "Resumen de inventario por bodegas",
            description = "Obtiene el resumen de las existencias de inventario agrupadas por bodega."
    )
    @ApiResponse(responseCode = "200", description = "Resumen de inventario obtenido exitosamente")
    public ResponseEntity<List<ResumenStockBodegaResponse>> obtenerResumenPorBodega() {

        return ResponseEntity.ok(
                reporteService.obtenerResumenPorBodega()
        );
    }

    @GetMapping("/movimientos/usuario/{idUsuario}")
    @Operation(
            summary = "Reporte de movimientos por usuario",
            description = "Consulta los movimientos de inventario asociados a un usuario específico."
    )
    @ApiResponse(responseCode = "200", description = "Reporte de movimientos por usuario obtenido exitosamente")
    public ResponseEntity<List<MovimientoResponse>> obtenerMovimientosPorUsuario(
            @Parameter(description = "Identificador del usuario", example = "1")
            @PathVariable Long idUsuario) {

        return ResponseEntity.ok(
                reporteService.obtenerMovimientosPorUsuario(idUsuario)
        );
    }

    @GetMapping("/movimientos/tipo/{tipoMovimiento}")
    @Operation(
            summary = "Reporte de movimientos por tipo",
            description = "Obtiene los movimientos de inventario filtrados por su tipo de operación."
    )
    @ApiResponse(responseCode = "200", description = "Reporte de movimientos por tipo obtenido exitosamente")
    public ResponseEntity<List<MovimientoResponse>> obtenerMovimientosPorTipo(
            @Parameter(description = "Tipo de movimiento que se desea consultar", example = "ENTRADA")
            @PathVariable String tipoMovimiento) {

        return ResponseEntity.ok(
                reporteService.obtenerMovimientosPorTipo(tipoMovimiento)
        );
    }

    @GetMapping("/movimientos/bodega/{idBodega}")
    @Operation(
            summary = "Reporte de movimientos por bodega",
            description = "Consulta los movimientos de inventario relacionados con una bodega específica."
    )
    @ApiResponse(responseCode = "200", description = "Reporte de movimientos por bodega obtenido exitosamente")
    public ResponseEntity<List<MovimientoResponse>> obtenerMovimientosPorBodega(
            @Parameter(description = "Identificador de la bodega", example = "1")
            @PathVariable Long idBodega) {

        return ResponseEntity.ok(
                reporteService.obtenerMovimientosPorBodega(idBodega)
        );
    }

    @GetMapping("/movimientos/fechas")
    @Operation(
            summary = "Reporte de movimientos entre fechas",
            description = "Consulta los movimientos de inventario realizados dentro de un rango de fechas y horas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte de movimientos del periodo obtenido exitosamente"),
            @ApiResponse(responseCode = "400", description = "Formato de fecha inválido")
    })
    public ResponseEntity<List<MovimientoResponse>> obtenerMovimientosEntreFechas(

            @Parameter(
                    description = "Fecha y hora inicial en formato ISO",
                    example = "2026-01-01T00:00:00"
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaInicio,

            @Parameter(
                    description = "Fecha y hora final en formato ISO",
                    example = "2026-01-31T23:59:59"
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaFin) {

        return ResponseEntity.ok(
                reporteService.obtenerMovimientosEntreFechas(
                        fechaInicio,
                        fechaFin
                )
        );
    }

    @GetMapping("/productos-mayor-movimiento")
    @Operation(
            summary = "Reporte de productos con mayor movimiento",
            description = "Obtiene el listado de productos que presentan mayor cantidad de movimientos de inventario."
    )
    @ApiResponse(responseCode = "200", description = "Reporte de productos con mayor movimiento obtenido exitosamente")
    public ResponseEntity<List<ProductoMayorMovimientoResponse>>
    obtenerProductosConMayorMovimiento() {

        return ResponseEntity.ok(
                reporteService.obtenerProductosConMayorMovimiento()
        );
    }
}
