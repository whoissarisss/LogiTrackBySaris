package service;

import dto.response.InventarioResponse;
import dto.response.MovimientoResponse;
import dto.response.ProductoMayorMovimientoResponse;
import dto.response.ResumenStockBodegaResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ReporteService {

    List<InventarioResponse> obtenerInventarioGeneral();

    List<InventarioResponse> obtenerInventarioPorBodega(Integer idBodega);

    List<InventarioResponse> obtenerInventarioPorProducto(Integer idProducto);

    List<InventarioResponse> obtenerProductosConStockBajo(BigDecimal limite);

    List<ResumenStockBodegaResponse> obtenerResumenPorBodega();

    List<MovimientoResponse> obtenerMovimientosPorUsuario(Integer idUsuario);

    List<MovimientoResponse> obtenerMovimientosPorTipo(String tipoMovimiento);

    List<MovimientoResponse> obtenerMovimientosPorBodega(Integer idBodega);

    List<MovimientoResponse> obtenerMovimientosEntreFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<ProductoMayorMovimientoResponse> obtenerProductosConMayorMovimiento();
}
