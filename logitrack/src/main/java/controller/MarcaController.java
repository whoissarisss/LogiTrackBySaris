
package controller;

import dto.request.MarcaRequest;
import dto.response.MarcaResponse;
import service.MarcaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marcas")
@RequiredArgsConstructor
public class MarcaController {

    private final MarcaService marcaService;

    @PostMapping
    public ResponseEntity<MarcaResponse> registrarMarca(
            @Valid @RequestBody MarcaRequest dto) {

        MarcaResponse respuesta = marcaService.registrarMarca(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<MarcaResponse>> listarMarcas() {

        List<MarcaResponse> marcas = marcaService.listarMarcas();
        return ResponseEntity.ok(marcas);
    }

    @GetMapping("/{idMarca}")
    public ResponseEntity<MarcaResponse> obtenerMarcaPorId(
            @PathVariable Long idMarca) {

        MarcaResponse respuesta = marcaService.obtenerMarcaPorId(idMarca);
        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{idMarca}")
    public ResponseEntity<MarcaResponse> actualizarMarca(
            @PathVariable Long idMarca,
            @Valid @RequestBody MarcaRequest dto) {

        MarcaResponse respuesta = marcaService.actualizarMarca(idMarca, dto);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{idMarca}")
    public ResponseEntity<Void> eliminarMarca(@PathVariable Long idMarca) {

        marcaService.eliminarMarca(idMarca);
        return ResponseEntity.noContent().build();
    }
}
