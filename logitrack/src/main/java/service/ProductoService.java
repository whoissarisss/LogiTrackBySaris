package service;

import dto.request.ProductoRequest;
import dto.response.ProductoResponse;

import java.util.List;

public interface ProductoService {

    ProductoResponse registrarProducto(ProductoRequest dto);

    List<ProductoResponse> listarProductos();

    List<ProductoResponse> listarProductosActivos();

    ProductoResponse obtenerProductoPorId(Integer idProducto);

    List<ProductoResponse> listarProductosPorMarca(Integer idMarca);

    List<ProductoResponse> listarProductosPorCategoria(Integer idCategoria);

    ProductoResponse actualizarProducto(Integer idProducto, ProductoRequest dto);

    void eliminarProducto(Integer idProducto);
}