package controller;

import dto.response.ResumenStockBodegaResponse;
import dto.response.InventarioResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import service.InventarioService;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
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
@RequestMapping("/api/inventarios")
@RequiredArgsConstructor
@Tag(name = "Inventarios", description = "Consultas de existencias y disponibilidad de productos en las bodegas")
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping
    @Operation(
            summary = "Listar inventarios",
            description = "Obtiene el listado general de inventarios registrados en el sistema."
    )
    @ApiResponse(responseCode = "200", description = "Inventarios obtenidos exitosamente")
    public ResponseEntity<List<InventarioResponse>> listarInventarios() {

        List<InventarioResponse> inventarios =
                inventarioService.listarInventarios();

        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/{idInventario}")
    @Operation(
            summary = "Consultar inventario por ID",
            description = "Obtiene la información de un registro de inventario específico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario encontrado"),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado")
    })
    public ResponseEntity<InventarioResponse> obtenerInventarioPorId(
            @Parameter(description = "Identificador del inventario", example = "1")
            @PathVariable Long idInventario) {

        InventarioResponse respuesta =
                inventarioService.obtenerInventarioPorId(idInventario);

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/bodega/{idBodega}")
    @Operation(
            summary = "Consultar inventario por bodega",
            description = "Obtiene los registros de inventario asociados a una bodega específica."
    )
    @ApiResponse(responseCode = "200", description = "Inventario de la bodega obtenido exitosamente")
    public ResponseEntity<List<InventarioResponse>> listarInventarioPorBodega(
            @Parameter(description = "Identificador de la bodega", example = "1")
            @PathVariable Long idBodega) {

        List<InventarioResponse> inventarios =
                inventarioService.listarInventarioPorBodega(idBodega);

        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/producto/{idProducto}")
    @Operation(
            summary = "Consultar inventario por producto",
            description = "Obtiene los registros de inventario correspondientes a un producto específico."
    )
    @ApiResponse(responseCode = "200", description = "Inventario del producto obtenido exitosamente")
    public ResponseEntity<List<InventarioResponse>> listarInventarioPorProducto(
            @Parameter(description = "Identificador del producto", example = "1")
            @PathVariable Long idProducto) {

        List<InventarioResponse> inventarios =
                inventarioService.listarInventarioPorProducto(idProducto);

        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/stock-bajo")
    @Operation(
            summary = "Consultar productos con stock bajo",
            description = "Obtiene los registros de inventario cuyo stock se encuentra por debajo del límite indicado."
    )
    @ApiResponse(responseCode = "200", description = "Consulta de stock bajo realizada exitosamente")
    public ResponseEntity<List<InventarioResponse>> listarInventarioStockBajo(
            @Parameter(
                    description = "Límite de stock para realizar el filtro",
                    example = "10"
            )
            @RequestParam BigDecimal limite) {

        List<InventarioResponse> inventarios =
                inventarioService.listarInventarioStockBajo(limite);

        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/resumen-por-bodega")
    @Operation(
            summary = "Consultar resumen de stock por bodega",
            description = "Obtiene un resumen de las existencias agrupadas por bodega."
    )
    @ApiResponse(responseCode = "200", description = "Resumen de stock obtenido exitosamente")
    public ResponseEntity<List<ResumenStockBodegaResponse>> listarResumenStockPorBodega() {

        List<ResumenStockBodegaResponse> resumen =
                inventarioService.listarResumenStockPorBodega();

        return ResponseEntity.ok(resumen);
    }
}
