
package controller;

import dto.response.InventarioResponse;
import service.InventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventarios")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping
    public ResponseEntity<List<InventarioResponse>> listarInventarios() {

        List<InventarioResponse> inventarios =
                inventarioService.listarInventarios();

        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/{idInventario}")
    public ResponseEntity<InventarioResponse> obtenerInventarioPorId(
            @PathVariable Long idInventario) {

        InventarioResponse respuesta =
                inventarioService.obtenerInventarioPorId(idInventario);

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/bodega/{idBodega}")
    public ResponseEntity<List<InventarioResponse>> listarInventarioPorBodega(
            @PathVariable Long idBodega) {

        List<InventarioResponse> inventarios =
                inventarioService.listarInventarioPorBodega(idBodega);

        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<InventarioResponse>> listarInventarioPorProducto(
            @PathVariable Long idProducto) {

        List<InventarioResponse> inventarios =
                inventarioService.listarInventarioPorProducto(idProducto);

        return ResponseEntity.ok(inventarios);
    }
}
