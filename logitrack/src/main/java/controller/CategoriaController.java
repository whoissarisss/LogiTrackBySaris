
package controller;

import dto.request.CategoriaRequest;
import dto.response.CategoriaResponse;
import service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaResponse> registrarCategoria(
            @Valid @RequestBody CategoriaRequest dto) {

        CategoriaResponse respuesta = categoriaService.registrarCategoria(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listarCategorias() {

        List<CategoriaResponse> categorias = categoriaService.listarCategorias();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{idCategoria}")
    public ResponseEntity<CategoriaResponse> obtenerCategoriaPorId(
            @PathVariable Long idCategoria) {

        CategoriaResponse respuesta =
                categoriaService.obtenerCategoriaPorId(idCategoria);

        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{idCategoria}")
    public ResponseEntity<CategoriaResponse> actualizarCategoria(
            @PathVariable Long idCategoria,
            @Valid @RequestBody CategoriaRequest dto) {

        CategoriaResponse respuesta =
                categoriaService.actualizarCategoria(idCategoria, dto);

        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{idCategoria}")
    public ResponseEntity<Void> eliminarCategoria(
            @PathVariable Long idCategoria) {

        categoriaService.eliminarCategoria(idCategoria);
        return ResponseEntity.noContent().build();
    }
}
