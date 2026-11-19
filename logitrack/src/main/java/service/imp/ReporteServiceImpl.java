package service.imp;

import dto.response.InventarioResponse;
import dto.response.MovimientoResponse;
import dto.response.ProductoMayorMovimientoResponse;
import dto.response.ResumenStockBodegaResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import service.InventarioService;
import service.MovimientoService;
import service.ReporteService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final InventarioService inventarioService;
    private final MovimientoService movimientoService;

    @Override
    public List<InventarioResponse> obtenerInventarioGeneral() {
        return inventarioService.listarInventarios();
    }

    @Override
    public List<InventarioResponse> obtenerInventarioPorBodega(Integer idBodega) {
        return inventarioService.listarInventarioPorBodega(idBodega);
    }

    @Override
    public List<InventarioResponse> obtenerInventarioPorProducto(Integer idProducto) {
        return inventarioService.listarInventarioPorProducto(idProducto);
    }

    @Override
    public List<InventarioResponse> obtenerProductosConStockBajo(BigDecimal limite) {
        return inventarioService.listarInventarioStockBajo(limite);
    }

    @Override
    public List<ResumenStockBodegaResponse> obtenerResumenPorBodega() {
        return inventarioService.listarResumenStockPorBodega();
    }

    @Override
    public List<MovimientoResponse> obtenerMovimientosPorUsuario(Integer idUsuario) {
        return movimientoService.listarMovimientosPorUsuario(idUsuario);
    }

    @Override
    public List<MovimientoResponse> obtenerMovimientosPorTipo(String tipoMovimiento) {
        return movimientoService.listarMovimientosPorTipo(tipoMovimiento);
    }

    @Override
    public List<MovimientoResponse> obtenerMovimientosPorBodega(Integer idBodega) {
        return movimientoService.listarMovimientosPorBodega(idBodega);
    }

    @Override
    public List<MovimientoResponse> obtenerMovimientosEntreFechas(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    ) {
        return movimientoService.listarMovimientosEntreFechas(
                fechaInicio,
                fechaFin
        );
    }

    @Override
    public List<ProductoMayorMovimientoResponse> obtenerProductosConMayorMovimiento() {
        return movimientoService.listarProductosConMayorMovimiento();
    }
}
