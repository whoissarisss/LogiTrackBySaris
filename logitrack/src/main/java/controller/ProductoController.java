package controller;

import dto.request.ProductoRequest;
import dto.response.ProductoResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import service.ProductoService;

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

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(
        name = "Productos",
        description = "Administración, consulta y actualización de productos de Bake Track"
)
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    @Operation(
            summary = "Registrar producto",
            description = "Registra un nuevo producto en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Los datos del producto son inválidos"),
            @ApiResponse(responseCode = "409", description = "El producto ya se encuentra registrado")
    })
    public ResponseEntity<ProductoResponse> registrarProducto(
            @Valid @RequestBody ProductoRequest dto) {

        ProductoResponse respuesta = productoService.registrarProducto(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los productos",
            description = "Obtiene el listado general de productos registrados en Bake Track."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos consultados correctamente")
    })
    public ResponseEntity<List<ProductoResponse>> listarProductos() {

        List<ProductoResponse> productos = productoService.listarProductos();

        return ResponseEntity.ok(productos);
    }

    @GetMapping("/activos")
    @Operation(
            summary = "Listar productos activos",
            description = "Obtiene únicamente los productos que se encuentran activos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos activos consultados correctamente")
    })
    public ResponseEntity<List<ProductoResponse>> listarProductosActivos() {

        List<ProductoResponse> productos =
                productoService.listarProductosActivos();

        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{idProducto}")
    @Operation(
            summary = "Consultar producto por ID",
            description = "Obtiene la información de un producto mediante su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductoResponse> obtenerProductoPorId(
            @Parameter(description = "Identificador del producto", example = "1")
            @PathVariable Long idProducto) {

        ProductoResponse respuesta =
                productoService.obtenerProductoPorId(idProducto);

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/marca/{idMarca}")
    @Operation(
            summary = "Listar productos por marca",
            description = "Consulta los productos asociados a una marca específica."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos consultados correctamente"),
            @ApiResponse(responseCode = "404", description = "Marca no encontrada")
    })
    public ResponseEntity<List<ProductoResponse>> listarProductosPorMarca(
            @Parameter(description = "Identificador de la marca", example = "1")
            @PathVariable Long idMarca) {

        List<ProductoResponse> productos =
                productoService.listarProductosPorMarca(idMarca);

        return ResponseEntity.ok(productos);
    }

    @GetMapping("/categoria/{idCategoria}")
    @Operation(
            summary = "Listar productos por categoría",
            description = "Consulta los productos que pertenecen a una categoría específica."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos consultados correctamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    public ResponseEntity<List<ProductoResponse>> listarProductosPorCategoria(
            @Parameter(description = "Identificador de la categoría", example = "1")
            @PathVariable Long idCategoria) {

        List<ProductoResponse> productos =
                productoService.listarProductosPorCategoria(idCategoria);

        return ResponseEntity.ok(productos);
    }

    @PutMapping("/{idProducto}")
    @Operation(
            summary = "Actualizar producto",
            description = "Actualiza la información de un producto existente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Los datos enviados son inválidos"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductoResponse> actualizarProducto(
            @Parameter(description = "Identificador del producto", example = "1")
            @PathVariable Long idProducto,

            @Valid @RequestBody ProductoRequest dto) {

        ProductoResponse respuesta =
                productoService.actualizarProducto(idProducto, dto);

        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{idProducto}")
    @Operation(
            summary = "Eliminar producto",
            description = "Elimina un producto mediante su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Void> eliminarProducto(
            @Parameter(description = "Identificador del producto", example = "1")
            @PathVariable Long idProducto) {

        productoService.eliminarProducto(idProducto);

        return ResponseEntity.noContent().build();
    }
}
