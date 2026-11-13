package service.imp;

import dto.response.BodegaResponse;
import dto.response.InventarioResponse;
import dto.response.ProductoResponse;
import mapper.BodegaMapper;
import mapper.CiudadMapper;
import mapper.InventarioMapper;
import mapper.ProductoMapper;
import mapper.MarcaMapper;
import mapper.CategoriaMapper;
import mapper.UsuarioMapper;
import model.Inventario;
import repository.BodegaRepository;
import repository.InventarioRepository;
import repository.ProductoRepository;
import service.InventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepository;
    private final BodegaRepository bodegaRepository;
    private final ProductoRepository productoRepository;

    private final InventarioMapper inventarioMapper;
    private final BodegaMapper bodegaMapper;
    private final CiudadMapper ciudadMapper;
    private final UsuarioMapper usuarioMapper;
    private final ProductoMapper productoMapper;
    private final MarcaMapper marcaMapper;
    private final CategoriaMapper categoriaMapper;

    @Override
    public List<InventarioResponse> listarInventarios() {

        List<Inventario> inventarios = inventarioRepository.findAll();

        return inventarios.stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    public List<InventarioResponse> listarInventarioPorBodega(Long idBodega) {

        if (!bodegaRepository.existsById(idBodega)) {
            throw new IllegalArgumentException("Bodega no encontrada");
        }

        List<Inventario> inventarios =
                inventarioRepository.findByBodega_IdBodega(idBodega);

        return inventarios.stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    public List<InventarioResponse> listarInventarioPorProducto(Long idProducto) {

        if (!productoRepository.existsById(idProducto)) {
            throw new IllegalArgumentException("Producto no encontrado");
        }

        List<Inventario> inventarios =
                inventarioRepository.findByProducto_IdProducto(idProducto);

        return inventarios.stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    public InventarioResponse obtenerInventarioPorId(Long idInventario) {

        Inventario inventario = inventarioRepository.findById(idInventario)
                .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado"));

        return convertirARespuesta(inventario);
    }

    private InventarioResponse convertirARespuesta(Inventario inventario) {

        BodegaResponse bodega = bodegaMapper.entityToDto(
                inventario.getBodega(),
                ciudadMapper.entityToDto(inventario.getBodega().getCiudad()),
                usuarioMapper.entityToDto(inventario.getBodega().getEncargado())
        );

        ProductoResponse producto = productoMapper.entityToDto(
                inventario.getProducto(),
                marcaMapper.entityToDto(inventario.getProducto().getMarca()),
                categoriaMapper.entityToDto(inventario.getProducto().getCategoria())
        );

        return inventarioMapper.entityToDto(inventario, bodega, producto);
    }
}
