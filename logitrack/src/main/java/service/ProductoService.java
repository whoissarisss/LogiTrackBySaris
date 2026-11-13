package service;

import dto.request.ProductoRequest;
import dto.response.ProductoResponse;

import java.util.List;

public interface ProductoService {

    ProductoResponse registrarProducto(ProductoRequest dto);

    List<ProductoResponse> listarProductos();

    List<ProductoResponse> listarProductosActivos();

    ProductoResponse obtenerProductoPorId(Long idProducto);

    List<ProductoResponse> listarProductosPorMarca(Long idMarca);

    List<ProductoResponse> listarProductosPorCategoria(Long idCategoria);

    ProductoResponse actualizarProducto(Long idProducto, ProductoRequest dto);

    void desactivarProducto(Long idProducto);
}
