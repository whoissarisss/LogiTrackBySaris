package service;

import dto.request.MovimientoRequest;
import dto.response.MovimientoResponse;

import java.util.List;

public interface MovimientoService {

    MovimientoResponse registrarMovimiento(MovimientoRequest dto);

    List<MovimientoResponse> listarMovimientos();

    MovimientoResponse obtenerMovimientoPorId(Long idMovimiento);

    List<MovimientoResponse> listarMovimientosPorUsuario(Long idUsuario);

    List<MovimientoResponse> listarMovimientosPorTipo(String tipoMovimiento);

    List<MovimientoResponse> listarMovimientosPorBodega(Long idBodega);
}
