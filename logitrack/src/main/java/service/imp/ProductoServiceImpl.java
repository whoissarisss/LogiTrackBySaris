package service.imp;

import dto.request.ProductoRequest;
import dto.response.ProductoResponse;
import jakarta.transaction.Transactional;
import mapper.CategoriaMapper;
import mapper.MarcaMapper;
import mapper.ProductoMapper;
import model.Categoria;
import model.Marca;
import model.Producto;
import model.Operacion;
import repository.InventarioRepository;
import repository.DetalleMovimientoRepository;
import service.AuditoriaRegistroService;
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
    private final AuditoriaRegistroService auditoriaRegistroService;
    private final InventarioRepository inventarioRepository;
    private final DetalleMovimientoRepository detalleMovimientoRepository;

    @Override
    @Transactional
    public ProductoResponse registrarProducto(ProductoRequest dto) {

        Marca marca = marcaRepository.findById(dto.idMarca())
                .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada"));

        Categoria categoria = categoriaRepository.findById(dto.idCategoria())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        Producto producto = productoMapper.DtoToEntity(dto, marca, categoria);

        Producto productoGuardado = productoRepository.save(producto);

        auditoriaRegistroService.registrar(
                Operacion.INSERT,
                "producto",
                productoGuardado.getIdProducto(),
                null,
                convertirARespuesta(productoGuardado)
        );

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
    public ProductoResponse obtenerProductoPorId(Integer idProducto) {

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        return convertirARespuesta(producto);
    }

    @Override
    public List<ProductoResponse> listarProductosPorMarca(Integer idMarca) {

        if (!marcaRepository.existsById(idMarca)) {
            throw new IllegalArgumentException("Marca no encontrada");
        }

        List<Producto> productos = productoRepository.findByMarca_IdMarca(idMarca);

        return productos.stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    public List<ProductoResponse> listarProductosPorCategoria(Integer idCategoria) {

        if (!categoriaRepository.existsById(idCategoria)) {
            throw new IllegalArgumentException("Categoría no encontrada");
        }

        List<Producto> productos = productoRepository.findByCategoria_IdCategoria(idCategoria);

        return productos.stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    @Transactional
    public ProductoResponse actualizarProducto(Integer idProducto, ProductoRequest dto) {

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        ProductoResponse valoresAnteriores = convertirARespuesta(producto);

        Marca marca = marcaRepository.findById(dto.idMarca())
                .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada"));

        Categoria categoria = categoriaRepository.findById(dto.idCategoria())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        productoMapper.updateEntityFromDto(dto, producto, marca, categoria);

        Producto productoActualizado = productoRepository.save(producto);

        auditoriaRegistroService.registrar(
                Operacion.UPDATE,
                "producto",
                productoActualizado.getIdProducto(),
                valoresAnteriores,
                convertirARespuesta(productoActualizado)
        );

        return convertirARespuesta(productoActualizado);
    }

    @Override
    @Transactional
    public void eliminarProducto(Integer idProducto) {

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        if (!inventarioRepository.findByProducto_IdProducto(idProducto).isEmpty()) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el producto porque tiene registros de inventario");
        }

        if (!detalleMovimientoRepository.findByProducto_IdProducto(idProducto).isEmpty()) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el producto porque tiene movimientos registrados");
        }

        ProductoResponse valoresAnteriores = convertirARespuesta(producto);

        auditoriaRegistroService.registrar(
                Operacion.DELETE,
                "producto",
                producto.getIdProducto(),
                valoresAnteriores,
                null
        );

        productoRepository.delete(producto);
    }

    private ProductoResponse convertirARespuesta(Producto producto) {

        return productoMapper.entityToDto(
                producto,
                marcaMapper.entityToDto(producto.getMarca()),
                categoriaMapper.entityToDto(producto.getCategoria())
        );
    }
}