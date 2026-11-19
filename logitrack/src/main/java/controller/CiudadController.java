package controller;

import dto.request.CiudadRequest;
import dto.response.CiudadResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import service.CiudadService;

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
@RequestMapping("/api/ciudades")
@RequiredArgsConstructor
@Tag(
        name = "Ciudades",
        description = "Administración y consulta de ciudades registradas en Bake Track"
)
public class CiudadController {

    private final CiudadService ciudadService;

    @PostMapping
    @Operation(
            summary = "Registrar ciudad",
            description = "Registra una nueva ciudad en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ciudad registrada correctamente"),
            @ApiResponse(responseCode = "400", description = "Los datos de la ciudad son inválidos"),
            @ApiResponse(responseCode = "409", description = "La ciudad ya se encuentra registrada")
    })
    public ResponseEntity<CiudadResponse> registrarCiudad(
            @Valid @RequestBody CiudadRequest dto) {

        CiudadResponse respuesta = ciudadService.registrarCiudad(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    @Operation(
            summary = "Listar ciudades",
            description = "Obtiene todas las ciudades registradas en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ciudades consultadas correctamente")
    })
    public ResponseEntity<List<CiudadResponse>> listarCiudades() {

        List<CiudadResponse> ciudades = ciudadService.listarCiudades();

        return ResponseEntity.ok(ciudades);
    }

    @GetMapping("/{idCiudad}")
    @Operation(
            summary = "Consultar ciudad por ID",
            description = "Obtiene la información de una ciudad mediante su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ciudad encontrada"),
            @ApiResponse(responseCode = "404", description = "Ciudad no encontrada")
    })
    public ResponseEntity<CiudadResponse> obtenerCiudadPorId(
            @Parameter(description = "Identificador de la ciudad", example = "1")
            @PathVariable Long idCiudad) {

        CiudadResponse respuesta = ciudadService.obtenerCiudadPorId(idCiudad);

        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{idCiudad}")
    @Operation(
            summary = "Actualizar ciudad",
            description = "Actualiza la información de una ciudad existente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ciudad actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Los datos enviados son inválidos"),
            @ApiResponse(responseCode = "404", description = "Ciudad no encontrada")
    })
    public ResponseEntity<CiudadResponse> actualizarCiudad(
            @Parameter(description = "Identificador de la ciudad", example = "1")
            @PathVariable Long idCiudad,

            @Valid @RequestBody CiudadRequest dto) {

        CiudadResponse respuesta = ciudadService.actualizarCiudad(idCiudad, dto);

        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{idCiudad}")
    @Operation(
            summary = "Eliminar ciudad",
            description = "Elimina una ciudad mediante su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ciudad eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Ciudad no encontrada")
    })
    public ResponseEntity<Void> eliminarCiudad(
            @Parameter(description = "Identificador de la ciudad", example = "1")
            @PathVariable Long idCiudad) {

        ciudadService.eliminarCiudad(idCiudad);

        return ResponseEntity.noContent().build();
    }
}
