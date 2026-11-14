
package controller;

import dto.request.BodegaRequest;
import dto.response.BodegaResponse;
import service.BodegaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bodegas")
@RequiredArgsConstructor
public class BodegaController {

    private final BodegaService bodegaService;

    @PostMapping
    public ResponseEntity<BodegaResponse> registrarBodega(
            @Valid @RequestBody BodegaRequest dto) {

        BodegaResponse respuesta = bodegaService.registrarBodega(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<BodegaResponse>> listarBodegas() {

        List<BodegaResponse> bodegas = bodegaService.listarBodegas();
        return ResponseEntity.ok(bodegas);
    }

    @GetMapping("/{idBodega}")
    public ResponseEntity<BodegaResponse> obtenerBodegaPorId(
            @PathVariable Long idBodega) {

        BodegaResponse respuesta = bodegaService.obtenerBodegaPorId(idBodega);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/ciudad/{idCiudad}")
    public ResponseEntity<List<BodegaResponse>> listarBodegasPorCiudad(
            @PathVariable Long idCiudad) {

        List<BodegaResponse> bodegas =
                bodegaService.listarBodegasPorCiudad(idCiudad);

        return ResponseEntity.ok(bodegas);
    }

    @PutMapping("/{idBodega}")
    public ResponseEntity<BodegaResponse> actualizarBodega(
            @PathVariable Long idBodega,
            @Valid @RequestBody BodegaRequest dto) {

        BodegaResponse respuesta =
                bodegaService.actualizarBodega(idBodega, dto);

        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{idBodega}")
    public ResponseEntity<Void> eliminarBodega(
            @PathVariable Long idBodega) {

        bodegaService.eliminarBodega(idBodega);
        return ResponseEntity.noContent().build();
    }
}
