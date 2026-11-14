
package controller;

import dto.request.MovimientoRequest;
import dto.response.MovimientoResponse;
import model.TipoMovimiento;
import service.MovimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    @PostMapping
    public ResponseEntity<MovimientoResponse> registrarMovimiento(
            @Valid @RequestBody MovimientoRequest dto) {

        MovimientoResponse respuesta =
                movimientoService.registrarMovimiento(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<MovimientoResponse>> listarMovimientos() {

        List<MovimientoResponse> movimientos =
                movimientoService.listarMovimientos();

        return ResponseEntity.ok(movimientos);
    }

    @GetMapping("/{idMovimiento}")
    public ResponseEntity<MovimientoResponse> obtenerMovimientoPorId(
            @PathVariable Long idMovimiento) {

        MovimientoResponse respuesta =
                movimientoService.obtenerMovimientoPorId(idMovimiento);

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosPorUsuario(
            @PathVariable Long idUsuario) {

        List<MovimientoResponse> movimientos =
                movimientoService.listarMovimientosPorUsuario(idUsuario);

        return ResponseEntity.ok(movimientos);
    }

    @GetMapping("/tipo/{tipoMovimiento}")
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosPorTipo(
            @PathVariable TipoMovimiento tipoMovimiento) {

        List<MovimientoResponse> movimientos =
                movimientoService.listarMovimientosPorTipo(tipoMovimiento.name());

        return ResponseEntity.ok(movimientos);
    }

    @GetMapping("/bodega/{idBodega}")
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosPorBodega(
            @PathVariable Long idBodega) {

        List<MovimientoResponse> movimientos =
                movimientoService.listarMovimientosPorBodega(idBodega);

        return ResponseEntity.ok(movimientos);
    }
}
