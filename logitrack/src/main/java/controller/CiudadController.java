
package controller;

import dto.request.CiudadRequest;
import dto.response.CiudadResponse;
import service.CiudadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ciudades")
@RequiredArgsConstructor
public class CiudadController {

    private final CiudadService ciudadService;

    @PostMapping
    public ResponseEntity<CiudadResponse> registrarCiudad(
            @Valid @RequestBody CiudadRequest dto) {

        CiudadResponse respuesta = ciudadService.registrarCiudad(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<CiudadResponse>> listarCiudades() {

        List<CiudadResponse> ciudades = ciudadService.listarCiudades();
        return ResponseEntity.ok(ciudades);
    }

    @GetMapping("/{idCiudad}")
    public ResponseEntity<CiudadResponse> obtenerCiudadPorId(
            @PathVariable Long idCiudad) {

        CiudadResponse respuesta = ciudadService.obtenerCiudadPorId(idCiudad);
        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{idCiudad}")
    public ResponseEntity<CiudadResponse> actualizarCiudad(
            @PathVariable Long idCiudad,
            @Valid @RequestBody CiudadRequest dto) {

        CiudadResponse respuesta = ciudadService.actualizarCiudad(idCiudad, dto);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{idCiudad}")
    public ResponseEntity<Void> eliminarCiudad(@PathVariable Long idCiudad) {

        ciudadService.eliminarCiudad(idCiudad);
        return ResponseEntity.noContent().build();
    }
}
