package service;

import dto.response.ResumenStockBodegaResponse;
import dto.response.InventarioResponse;
import java.math.BigDecimal;

import java.util.List;

public interface InventarioService {

    List<InventarioResponse> listarInventarios();

    List<InventarioResponse> listarInventarioPorBodega(Integer idBodega);

    List<InventarioResponse> listarInventarioPorProducto(Integer idProducto);

    InventarioResponse obtenerInventarioPorId(Integer idInventario);

    List<InventarioResponse> listarInventarioStockBajo(BigDecimal limite);

    List<ResumenStockBodegaResponse> listarResumenStockPorBodega();
}
