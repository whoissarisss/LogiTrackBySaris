package service;

import dto.response.InventarioResponse;

import java.util.List;

public interface InventarioService {

    List<InventarioResponse> listarInventarios();

    List<InventarioResponse> listarInventarioPorBodega(Long idBodega);

    List<InventarioResponse> listarInventarioPorProducto(Long idProducto);

    InventarioResponse obtenerInventarioPorId(Long idInventario);
}
