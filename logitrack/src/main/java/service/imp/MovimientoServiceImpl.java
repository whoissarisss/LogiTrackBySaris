package service.imp;

import dto.request.DetalleMovimientoRequest;
import dto.request.MovimientoRequest;
import dto.response.BodegaResponse;
import dto.response.DetalleMovimientoResponse;
import dto.response.MovimientoResponse;
import dto.response.ProductoResponse;
import dto.response.UsuarioResponse;
import mapper.BodegaMapper;
import mapper.CategoriaMapper;
import mapper.CiudadMapper;
import mapper.DetalleMovimientoMapper;
import mapper.MarcaMapper;
import mapper.MovimientoMapper;
import mapper.ProductoMapper;
import mapper.UsuarioMapper;
import model.*;
import repository.*;
import service.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final DetalleMovimientoRepository detalleMovimientoRepository;
    private final InventarioRepository inventarioRepository;
    private final BodegaRepository bodegaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    private final MovimientoMapper movimientoMapper;
    private final DetalleMovimientoMapper detalleMovimientoMapper;
    private final UsuarioMapper usuarioMapper;
    private final BodegaMapper bodegaMapper;
    private final CiudadMapper ciudadMapper;
    private final ProductoMapper productoMapper;
    private final MarcaMapper marcaMapper;
    private final CategoriaMapper categoriaMapper;

    @Override
    @Transactional
    public MovimientoResponse registrarMovimiento(MovimientoRequest dto) {

        validarDetalles(dto.detalles());

        Usuario usuario = obtenerUsuarioAutenticado();

        Bodega origen = null;
        Bodega destino = null;

        if (dto.idBodegaOrigen() != null) {
            origen = bodegaRepository.findById(dto.idBodegaOrigen())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Bodega de origen no encontrada"));
        }

        if (dto.idBodegaDestino() != null) {
            destino = bodegaRepository.findById(dto.idBodegaDestino())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Bodega de destino no encontrada"));
        }

        validarBodegas(dto.tipoMovimiento(), origen, destino);

        Movimiento movimiento = movimientoMapper.DtoToEntity(
                dto, usuario, origen, destino
        );

        Movimiento movimientoGuardado = movimientoRepository.save(movimiento);

        for (DetalleMovimientoRequest detalle : dto.detalles()) {

            Producto producto = productoRepository.findById(detalle.idProducto())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Producto no encontrado"));

            if (!Boolean.TRUE.equals(producto.getEstado())) {
                throw new IllegalArgumentException(
                        "No se pueden realizar movimientos de productos inactivos");
            }

            if (dto.tipoMovimiento() == TipoMovimiento.ENTRADA) {
                registrarEntrada(destino, producto, detalle.cantidad());
            }

            if (dto.tipoMovimiento() == TipoMovimiento.SALIDA) {
                registrarSalida(origen, producto, detalle.cantidad());
            }

            if (dto.tipoMovimiento() == TipoMovimiento.TRANSFERENCIA) {
                registrarSalida(origen, producto, detalle.cantidad());
                registrarEntrada(destino, producto, detalle.cantidad());
            }

            DetalleMovimiento detalleMovimiento =
                    detalleMovimientoMapper.DtoToEntity(
                            detalle, movimientoGuardado, producto
                    );

            detalleMovimientoRepository.save(detalleMovimiento);
        }

        return convertirARespuesta(movimientoGuardado);
    }

    private void validarDetalles(List<DetalleMovimientoRequest> detalles) {

        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException(
                    "El movimiento debe contener al menos un producto");
        }

        Set<Long> productos = new HashSet<>();

        for (DetalleMovimientoRequest detalle : detalles) {

            if (detalle.cantidad() == null
                    || detalle.cantidad().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "La cantidad debe ser mayor que cero");
            }

            if (!productos.add(detalle.idProducto())) {
                throw new IllegalArgumentException(
                        "No se puede repetir un producto en el mismo movimiento");
            }
        }
    }

    private void validarBodegas(
            TipoMovimiento tipo,
            Bodega origen,
            Bodega destino) {

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "Debe indicar el tipo de movimiento");
        }

        if (tipo == TipoMovimiento.ENTRADA
                && (origen != null || destino == null)) {
            throw new IllegalArgumentException(
                    "Una entrada requiere únicamente una bodega de destino");
        }

        if (tipo == TipoMovimiento.SALIDA
                && (origen == null || destino != null)) {
            throw new IllegalArgumentException(
                    "Una salida requiere únicamente una bodega de origen");
        }

        if (tipo == TipoMovimiento.TRANSFERENCIA
                && (origen == null || destino == null
                || origen.getIdBodega() == destino.getIdBodega())) {
            throw new IllegalArgumentException(
                    "La transferencia requiere dos bodegas diferentes");
        }
    }

    private void registrarEntrada(
            Bodega bodega,
            Producto producto,
            BigDecimal cantidad) {

        Inventario inventario = inventarioRepository
                .findByBodega_IdBodegaAndProducto_IdProducto(
                        bodega.getIdBodega(),
                        producto.getIdProducto()
                )
                .orElseGet(() -> {
                    Inventario nuevo = new Inventario();
                    nuevo.setBodega(bodega);
                    nuevo.setProducto(producto);
                    nuevo.setStockActual(BigDecimal.ZERO);
                    return nuevo;
                });

        inventario.setStockActual(
                inventario.getStockActual().add(cantidad)
        );

        inventarioRepository.save(inventario);
    }

    private void registrarSalida(
            Bodega bodega,
            Producto producto,
            BigDecimal cantidad) {

        Inventario inventario = inventarioRepository
                .findByBodega_IdBodegaAndProducto_IdProducto(
                        bodega.getIdBodega(),
                        producto.getIdProducto()
                )
                .orElseThrow(() -> new IllegalArgumentException(
                        "El producto no tiene existencias registradas en la bodega de origen"
                ));

        if (inventario.getStockActual().compareTo(cantidad) < 0) {
            throw new IllegalArgumentException(
                    "No hay suficiente stock para realizar la operación");
        }

        inventario.setStockActual(
                inventario.getStockActual().subtract(cantidad)
        );

        inventarioRepository.save(inventario);
    }

    private Usuario obtenerUsuarioAutenticado() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException(
                    "No hay un usuario autenticado");
        }

        return usuarioRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el usuario autenticado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientos() {

        return movimientoRepository.findAll().stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoResponse obtenerMovimientoPorId(Long idMovimiento) {

        Movimiento movimiento = movimientoRepository.findById(idMovimiento)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Movimiento no encontrado"));

        return convertirARespuesta(movimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorUsuario(Long idUsuario) {

        if (!usuarioRepository.existsById(idUsuario)) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        return movimientoRepository.findByUsuario_IdUsuario(idUsuario).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorTipo(String tipoMovimiento) {

        TipoMovimiento tipo;

        try {
            tipo = TipoMovimiento.valueOf(
                    tipoMovimiento.trim().toUpperCase()
            );
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Tipo de movimiento no válido");
        }

        return movimientoRepository.findByTipoMovimiento(tipo).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorBodega(Long idBodega) {

        if (!bodegaRepository.existsById(idBodega)) {
            throw new IllegalArgumentException("Bodega no encontrada");
        }

        List<Movimiento> movimientos = movimientoRepository.findAll().stream()
                .filter(movimiento ->
                        (movimiento.getBodegaOrigen() != null
                                && movimiento.getBodegaOrigen()
                                .getIdBodega() == idBodega)
                                ||
                                (movimiento.getBodegaDestino() != null
                                        && movimiento.getBodegaDestino()
                                        .getIdBodega() == idBodega)
                )
                .toList();

        return movimientos.stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    private MovimientoResponse convertirARespuesta(Movimiento movimiento) {

        UsuarioResponse usuario =
                usuarioMapper.entityToDto(movimiento.getUsuario());

        BodegaResponse origen = movimiento.getBodegaOrigen() == null
                ? null : convertirBodega(movimiento.getBodegaOrigen());

        BodegaResponse destino = movimiento.getBodegaDestino() == null
                ? null : convertirBodega(movimiento.getBodegaDestino());

        List<DetalleMovimientoResponse> detalles =
                detalleMovimientoRepository
                        .findByMovimiento_IdMovimiento(
                                movimiento.getIdMovimiento()
                        )
                        .stream()
                        .map(detalle -> detalleMovimientoMapper.entityToDto(
                                detalle,
                                convertirProducto(detalle.getProducto())
                        ))
                        .toList();

        return movimientoMapper.entityToDto(
                movimiento,
                usuario,
                origen,
                destino,
                detalles
        );
    }

    private BodegaResponse convertirBodega(Bodega bodega) {

        return bodegaMapper.entityToDto(
                bodega,
                ciudadMapper.entityToDto(bodega.getCiudad()),
                usuarioMapper.entityToDto(bodega.getEncargado())
        );
    }

    private ProductoResponse convertirProducto(Producto producto) {

        return productoMapper.entityToDto(
                producto,
                marcaMapper.entityToDto(producto.getMarca()),
                categoriaMapper.entityToDto(producto.getCategoria())
        );
    }
}
