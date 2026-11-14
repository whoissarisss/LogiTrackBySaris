
package controller;

import dto.request.ProductoRequest;
import dto.response.ProductoResponse;
import service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    public ResponseEntity<ProductoResponse> registrarProducto(
            @Valid @RequestBody ProductoRequest dto) {

        ProductoResponse respuesta = productoService.registrarProducto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listarProductos() {

        List<ProductoResponse> productos = productoService.listarProductos();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<ProductoResponse>> listarProductosActivos() {

        List<ProductoResponse> productos =
                productoService.listarProductosActivos();

        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{idProducto}")
    public ResponseEntity<ProductoResponse> obtenerProductoPorId(
            @PathVariable Long idProducto) {

        ProductoResponse respuesta =
                productoService.obtenerProductoPorId(idProducto);

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/marca/{idMarca}")
    public ResponseEntity<List<ProductoResponse>> listarProductosPorMarca(
            @PathVariable Long idMarca) {

        List<ProductoResponse> productos =
                productoService.listarProductosPorMarca(idMarca);

        return ResponseEntity.ok(productos);
    }

    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<List<ProductoResponse>> listarProductosPorCategoria(
            @PathVariable Long idCategoria) {

        List<ProductoResponse> productos =
                productoService.listarProductosPorCategoria(idCategoria);

        return ResponseEntity.ok(productos);
    }

    @PutMapping("/{idProducto}")
    public ResponseEntity<ProductoResponse> actualizarProducto(
            @PathVariable Long idProducto,
            @Valid @RequestBody ProductoRequest dto) {

        ProductoResponse respuesta =
                productoService.actualizarProducto(idProducto, dto);

        return ResponseEntity.ok(respuesta);
    }

    @PatchMapping("/{idProducto}/desactivar")
    public ResponseEntity<ProductoResponse> desactivarProducto(
            @PathVariable Long idProducto) {

        ProductoResponse respuesta =
                productoService.desactivarProducto(idProducto);

        return ResponseEntity.ok(respuesta);
    }
}

