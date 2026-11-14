package service.imp;

import dto.request.ProductoRequest;
import dto.response.ProductoResponse;
import mapper.CategoriaMapper;
import mapper.MarcaMapper;
import mapper.ProductoMapper;
import model.Categoria;
import model.Marca;
import model.Producto;
import repository.CategoriaRepository;
import repository.MarcaRepository;
import repository.ProductoRepository;
import service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final MarcaRepository marcaRepository;
    private final CategoriaRepository categoriaRepository;

    private final ProductoMapper productoMapper;
    private final MarcaMapper marcaMapper;
    private final CategoriaMapper categoriaMapper;

    @Override
    public ProductoResponse registrarProducto(ProductoRequest dto) {

        Marca marca = marcaRepository.findById(dto.idMarca())
                .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada"));

        Categoria categoria = categoriaRepository.findById(dto.idCategoria())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        Producto producto = productoMapper.DtoToEntity(dto, marca, categoria);

        Producto productoGuardado = productoRepository.save(producto);

        return convertirARespuesta(productoGuardado);
    }

    @Override
    public List<ProductoResponse> listarProductos() {

        List<Producto> productos = productoRepository.findAll();

        return productos.stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    public List<ProductoResponse> listarProductosActivos() {

        List<Producto> productos = productoRepository.findByEstadoTrue();

        return productos.stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    public ProductoResponse obtenerProductoPorId(Long idProducto) {

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        return convertirARespuesta(producto);
    }

    @Override
    public List<ProductoResponse> listarProductosPorMarca(Long idMarca) {

        if (!marcaRepository.existsById(idMarca)) {
            throw new IllegalArgumentException("Marca no encontrada");
        }

        List<Producto> productos = productoRepository.findByMarca_IdMarca(idMarca);

        return productos.stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    public List<ProductoResponse> listarProductosPorCategoria(Long idCategoria) {

        if (!categoriaRepository.existsById(idCategoria)) {
            throw new IllegalArgumentException("Categoría no encontrada");
        }

        List<Producto> productos = productoRepository.findByCategoria_IdCategoria(idCategoria);

        return productos.stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    public ProductoResponse actualizarProducto(Long idProducto, ProductoRequest dto) {

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        Marca marca = marcaRepository.findById(dto.idMarca())
                .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada"));

        Categoria categoria = categoriaRepository.findById(dto.idCategoria())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        productoMapper.updateEntityFromDto(dto, producto, marca, categoria);

        Producto productoActualizado = productoRepository.save(producto);

        return convertirARespuesta(productoActualizado);
    }

    @Override
    public ProductoResponse desactivarProducto(Long idProducto) {

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        producto.setEstado(false);

        Producto productoActualizado = productoRepository.save(producto);

        return convertirARespuesta(productoActualizado);
    }

    private ProductoResponse convertirARespuesta(Producto producto) {

        return productoMapper.entityToDto(
                producto,
                marcaMapper.entityToDto(producto.getMarca()),
                categoriaMapper.entityToDto(producto.getCategoria())
        );
    }
}
