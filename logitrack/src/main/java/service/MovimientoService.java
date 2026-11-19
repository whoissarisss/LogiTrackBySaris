package service;

import dto.request.MovimientoRequest;
import dto.response.ProductoMayorMovimientoResponse;
import dto.response.MovimientoResponse;
import java.time.LocalDateTime;

import java.util.List;

public interface MovimientoService {

    MovimientoResponse registrarMovimiento(MovimientoRequest dto);

    List<MovimientoResponse> listarMovimientos();

    MovimientoResponse obtenerMovimientoPorId(Integer idMovimiento);

    List<MovimientoResponse> listarMovimientosPorUsuario(Integer idUsuario);

    List<MovimientoResponse> listarMovimientosPorTipo(String tipoMovimiento);

    List<MovimientoResponse> listarMovimientosPorBodega(Integer idBodega);

    List<MovimientoResponse> listarMovimientosEntreFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<ProductoMayorMovimientoResponse> listarProductosConMayorMovimiento();
}
