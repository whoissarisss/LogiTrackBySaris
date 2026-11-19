package controller;

import dto.request.CategoriaRequest;
import dto.response.CategoriaResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import service.CategoriaService;

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
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Tag(
        name = "Categorías",
        description = "Administración y consulta de categorías de productos de Bake Track"
)
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    @Operation(
            summary = "Registrar categoría",
            description = "Registra una nueva categoría para clasificar los productos del sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoría registrada correctamente"),
            @ApiResponse(responseCode = "400", description = "Los datos de la categoría son inválidos"),
            @ApiResponse(responseCode = "409", description = "La categoría ya se encuentra registrada")
    })
    public ResponseEntity<CategoriaResponse> registrarCategoria(
            @Valid @RequestBody CategoriaRequest dto) {

        CategoriaResponse respuesta = categoriaService.registrarCategoria(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    @Operation(
            summary = "Listar categorías",
            description = "Obtiene todas las categorías registradas en Bake Track."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categorías consultadas correctamente")
    })
    public ResponseEntity<List<CategoriaResponse>> listarCategorias() {

        List<CategoriaResponse> categorias = categoriaService.listarCategorias();

        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{idCategoria}")
    @Operation(
            summary = "Consultar categoría por ID",
            description = "Obtiene la información de una categoría mediante su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    public ResponseEntity<CategoriaResponse> obtenerCategoriaPorId(
            @Parameter(description = "Identificador de la categoría", example = "1")
            @PathVariable Integer idCategoria) {

        CategoriaResponse respuesta =
                categoriaService.obtenerCategoriaPorId(idCategoria);

        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{idCategoria}")
    @Operation(
            summary = "Actualizar categoría",
            description = "Actualiza la información de una categoría existente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Los datos enviados son inválidos"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    public ResponseEntity<CategoriaResponse> actualizarCategoria(
            @Parameter(description = "Identificador de la categoría", example = "1")
            @PathVariable Integer idCategoria,

            @Valid @RequestBody CategoriaRequest dto) {

        CategoriaResponse respuesta =
                categoriaService.actualizarCategoria(idCategoria, dto);

        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{idCategoria}")
    @Operation(
            summary = "Eliminar categoría",
            description = "Elimina una categoría mediante su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoría eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    public ResponseEntity<Void> eliminarCategoria(
            @Parameter(description = "Identificador de la categoría", example = "1")
            @PathVariable Integer idCategoria) {

        categoriaService.eliminarCategoria(idCategoria);

        return ResponseEntity.noContent().build();
    }
}
