
package exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarArgumentoInvalido(
            IllegalArgumentException exception) {

        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> manejarEstadoInvalido(
            IllegalStateException exception) {

        return construirRespuesta(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidaciones(
            MethodArgumentNotValidException exception) {

        Map<String, String> errores = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("fecha", LocalDateTime.now());
        respuesta.put("estado", HttpStatus.BAD_REQUEST.value());
        respuesta.put("mensaje", "Error de validación");
        respuesta.put("errores", errores);

        return ResponseEntity.badRequest().body(respuesta);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> manejarIntegridad(
            DataIntegrityViolationException exception) {

        return construirRespuesta(
                HttpStatus.CONFLICT,
                "No se puede realizar la operación porque existen registros relacionados o datos duplicados"
        );
    }

    private ResponseEntity<Map<String, Object>> construirRespuesta(
            HttpStatus estado,
            String mensaje) {

        Map<String, Object> respuesta = new HashMap<>();

        respuesta.put("fecha", LocalDateTime.now());
        respuesta.put("estado", estado.value());
        respuesta.put("mensaje", mensaje);

        return ResponseEntity.status(estado).body(respuesta);
    }
}
