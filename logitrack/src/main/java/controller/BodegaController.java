package controller;

import dto.request.BodegaRequest;
import dto.response.BodegaResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import service.BodegaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/bodegas")
@RequiredArgsConstructor
@Tag(name = "Bodegas", description = "Operaciones para la gestión de bodegas")
public class BodegaController {

    private final BodegaService bodegaService;

    @PostMapping
    @Operation(
            summary = "Registrar una bodega",
            description = "Registra una nueva bodega en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bodega registrada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la solicitud inválidos")
    })
    public ResponseEntity<BodegaResponse> registrarBodega(
            @Valid @RequestBody BodegaRequest dto) {

        BodegaResponse respuesta = bodegaService.registrarBodega(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    @Operation(
            summary = "Listar todas las bodegas",
            description = "Obtiene el listado de bodegas registradas en el sistema."
    )
    @ApiResponse(responseCode = "200", description = "Listado de bodegas obtenido exitosamente")
    public ResponseEntity<List<BodegaResponse>> listarBodegas() {

        List<BodegaResponse> bodegas = bodegaService.listarBodegas();
        return ResponseEntity.ok(bodegas);
    }

    @GetMapping("/{idBodega}")
    @Operation(
            summary = "Consultar una bodega por ID",
            description = "Obtiene la información de una bodega específica."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bodega encontrada"),
            @ApiResponse(responseCode = "404", description = "Bodega no encontrada")
    })
    public ResponseEntity<BodegaResponse> obtenerBodegaPorId(
            @Parameter(description = "Identificador de la bodega", example = "1")
            @PathVariable Long idBodega) {

        BodegaResponse respuesta = bodegaService.obtenerBodegaPorId(idBodega);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/ciudad/{idCiudad}")
    @Operation(
            summary = "Listar bodegas por ciudad",
            description = "Obtiene las bodegas asociadas a una ciudad específica."
    )
    @ApiResponse(responseCode = "200", description = "Bodegas de la ciudad obtenidas exitosamente")
    public ResponseEntity<List<BodegaResponse>> listarBodegasPorCiudad(
            @Parameter(description = "Identificador de la ciudad", example = "1")
            @PathVariable Long idCiudad) {

        List<BodegaResponse> bodegas =
                bodegaService.listarBodegasPorCiudad(idCiudad);

        return ResponseEntity.ok(bodegas);
    }

    @PutMapping("/{idBodega}")
    @Operation(
            summary = "Actualizar una bodega",
            description = "Actualiza la información de una bodega existente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bodega actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la solicitud inválidos"),
            @ApiResponse(responseCode = "404", description = "Bodega no encontrada")
    })
    public ResponseEntity<BodegaResponse> actualizarBodega(
            @Parameter(description = "Identificador de la bodega", example = "1")
            @PathVariable Long idBodega,
            @Valid @RequestBody BodegaRequest dto) {

        BodegaResponse respuesta =
                bodegaService.actualizarBodega(idBodega, dto);

        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{idBodega}")
    @Operation(
            summary = "Eliminar una bodega",
            description = "Elimina una bodega mediante su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Bodega eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Bodega no encontrada")
    })
    public ResponseEntity<Void> eliminarBodega(
            @Parameter(description = "Identificador de la bodega", example = "1")
            @PathVariable Long idBodega) {

        bodegaService.eliminarBodega(idBodega);
        return ResponseEntity.noContent().build();
    }
}
