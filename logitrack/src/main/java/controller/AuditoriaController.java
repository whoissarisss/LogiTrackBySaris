
package controller;

import dto.response.AuditoriaResponse;
import service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditorias")
@RequiredArgsConstructor
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping
    public ResponseEntity<List<AuditoriaResponse>> listarAuditorias() {

        List<AuditoriaResponse> auditorias =
                auditoriaService.listarAuditorias();

        return ResponseEntity.ok(auditorias);
    }

    @GetMapping("/{idAuditoria}")
    public ResponseEntity<AuditoriaResponse> obtenerAuditoriaPorId(
            @PathVariable Long idAuditoria) {

        AuditoriaResponse respuesta =
                auditoriaService.obtenerAuditoriaPorId(idAuditoria);

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<AuditoriaResponse>> listarAuditoriasPorUsuario(
            @PathVariable Long idUsuario) {

        List<AuditoriaResponse> auditorias =
                auditoriaService.listarAuditoriasPorUsuario(idUsuario);

        return ResponseEntity.ok(auditorias);
    }

    @GetMapping("/tabla/{tablaAfectada}")
    public ResponseEntity<List<AuditoriaResponse>> listarAuditoriasPorTabla(
            @PathVariable String tablaAfectada) {

        List<AuditoriaResponse> auditorias =
                auditoriaService.listarAuditoriasPorTabla(tablaAfectada);

        return ResponseEntity.ok(auditorias);
    }
}
