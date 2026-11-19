package controller;

import dto.request.MarcaRequest;
import dto.response.MarcaResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import service.MarcaService;

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
@RequestMapping("/api/marcas")
@RequiredArgsConstructor
@Tag(
        name = "Marcas",
        description = "Administración y consulta de marcas asociadas a los productos de Bake Track"
)
public class MarcaController {

    private final MarcaService marcaService;

    @PostMapping
    @Operation(
            summary = "Registrar marca",
            description = "Registra una nueva marca en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Marca registrada correctamente"),
            @ApiResponse(responseCode = "400", description = "Los datos de la marca son inválidos"),
            @ApiResponse(responseCode = "409", description = "La marca ya se encuentra registrada")
    })
    public ResponseEntity<MarcaResponse> registrarMarca(
            @Valid @RequestBody MarcaRequest dto) {

        MarcaResponse respuesta = marcaService.registrarMarca(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    @Operation(
            summary = "Listar marcas",
            description = "Obtiene todas las marcas registradas en Bake Track."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marcas consultadas correctamente")
    })
    public ResponseEntity<List<MarcaResponse>> listarMarcas() {

        List<MarcaResponse> marcas = marcaService.listarMarcas();

        return ResponseEntity.ok(marcas);
    }

    @GetMapping("/{idMarca}")
    @Operation(
            summary = "Consultar marca por ID",
            description = "Obtiene la información de una marca mediante su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marca encontrada"),
            @ApiResponse(responseCode = "404", description = "Marca no encontrada")
    })
    public ResponseEntity<MarcaResponse> obtenerMarcaPorId(
            @Parameter(description = "Identificador de la marca", example = "1")
            @PathVariable Long idMarca) {

        MarcaResponse respuesta = marcaService.obtenerMarcaPorId(idMarca);

        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{idMarca}")
    @Operation(
            summary = "Actualizar marca",
            description = "Actualiza la información de una marca existente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marca actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Los datos enviados son inválidos"),
            @ApiResponse(responseCode = "404", description = "Marca no encontrada")
    })
    public ResponseEntity<MarcaResponse> actualizarMarca(
            @Parameter(description = "Identificador de la marca", example = "1")
            @PathVariable Long idMarca,

            @Valid @RequestBody MarcaRequest dto) {

        MarcaResponse respuesta = marcaService.actualizarMarca(idMarca, dto);

        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{idMarca}")
    @Operation(
            summary = "Eliminar marca",
            description = "Elimina una marca mediante su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Marca eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Marca no encontrada")
    })
    public ResponseEntity<Void> eliminarMarca(
            @Parameter(description = "Identificador de la marca", example = "1")
            @PathVariable Long idMarca) {

        marcaService.eliminarMarca(idMarca);

        return ResponseEntity.noContent().build();
    }
}
