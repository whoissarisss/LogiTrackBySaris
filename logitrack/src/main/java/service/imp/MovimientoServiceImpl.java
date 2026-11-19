package service.imp;

import dto.request.DetalleMovimientoRequest;
import dto.request.MovimientoRequest;
import dto.response.BodegaResponse;
import dto.response.DetalleMovimientoResponse;
import dto.response.MovimientoResponse;
import dto.response.ProductoResponse;
import dto.response.ProductoMayorMovimientoResponse;
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
import service.AuditoriaRegistroService;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Objects;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final DetalleMovimientoRepository detalleMovimientoRepository;
    private final InventarioRepository inventarioRepository;
    private final BodegaRepository bodegaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaRegistroService auditoriaRegistroService;

    private final MovimientoMapper movimientoMapper;
    private final DetalleMovimientoMapper detalleMovimientoMapper;
    private final UsuarioMapper usuarioMapper;
    private final BodegaMapper bodegaMapper;
    private final CiudadMapper ciudadMapper;
    private final ProductoMapper productoMapper;
    private final MarcaMapper marcaMapper;
    private final CategoriaMapper categoriaMapper;

    private final EntityManager entityManager;

    // ==========================================
    // REGISTRAR MOVIMIENTO
    // ==========================================

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

        Movimiento movimientoGuardado =
                movimientoRepository.save(movimiento);

        movimientoRepository.flush();

        entityManager.refresh(movimientoGuardado);

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
                            detalle,
                            movimientoGuardado,
                            producto
                    );

            detalleMovimientoRepository.save(detalleMovimiento);
        }

        MovimientoResponse respuesta =
                convertirARespuesta(movimientoGuardado);

        auditoriaRegistroService.registrar(
                Operacion.INSERT,
                "movimiento",
                movimientoGuardado.getIdMovimiento(),
                null,
                dto
        );

        return respuesta;
    }

    // ==========================================
    // VALIDAR DETALLES DEL MOVIMIENTO
    // ==========================================

    private void validarDetalles(List<DetalleMovimientoRequest> detalles) {

        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException(
                    "El movimiento debe contener al menos un producto");
        }

        Set<Integer> productos = new HashSet<>();

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

    // ==========================================
    // VALIDAR BODEGAS
    // ==========================================

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
                && (origen == null
                || destino == null
                || Objects.equals(origen.getIdBodega(), destino.getIdBodega()))) {

            throw new IllegalArgumentException(
                    "La transferencia requiere dos bodegas diferentes");
        }
    }

    // ==========================================
    // REGISTRAR ENTRADA DE INVENTARIO
    // ==========================================

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

    // ==========================================
    // REGISTRAR SALIDA DE INVENTARIO
    // ==========================================

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

    // ==========================================
    // OBTENER USUARIO AUTENTICADO
    // ==========================================

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

    // ==========================================
    // VALIDAR SI ES ADMINISTRADOR
    // ==========================================

    private boolean esAdministrador() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    // ==========================================
    // VALIDAR ACCESO A MOVIMIENTOS DE UN USUARIO
    // ==========================================

    private void validarAccesoUsuario(Integer idUsuario) {

        if (esAdministrador()) {
            return;
        }

        Usuario usuario = obtenerUsuarioAutenticado();

        if (!Objects.equals(usuario.getIdUsuario(), idUsuario)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No tienes permiso para consultar los movimientos de otro usuario"
            );
        }
    }

    // ==========================================
    // FILTRAR MOVIMIENTOS SEGÚN EL USUARIO
    // ==========================================

    private List<Movimiento> filtrarMovimientosDelUsuario(
            List<Movimiento> movimientos) {

        if (esAdministrador()) {
            return movimientos;
        }

        Integer idUsuario = obtenerUsuarioAutenticado().getIdUsuario();

        return movimientos.stream()
                .filter(movimiento ->
                        movimiento.getUsuario() != null
                                && idUsuario.equals(
                                movimiento.getUsuario().getIdUsuario()))
                .toList();
    }

    // ==========================================
    // LISTAR MOVIMIENTOS
    // ADMIN: TODOS
    // EMPLEADO: SOLO LOS PROPIOS
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientos() {

        List<Movimiento> movimientos =
                filtrarMovimientosDelUsuario(movimientoRepository.findAll());

        return movimientos.stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    // ==========================================
    // OBTENER MOVIMIENTO POR ID
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public MovimientoResponse obtenerMovimientoPorId(Integer idMovimiento) {

        Movimiento movimiento = movimientoRepository.findById(idMovimiento)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Movimiento no encontrado"));

        validarAccesoUsuario(movimiento.getUsuario().getIdUsuario());

        return convertirARespuesta(movimiento);
    }

    // ==========================================
    // LISTAR MOVIMIENTOS POR USUARIO
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorUsuario(
            Integer idUsuario) {

        if (!usuarioRepository.existsById(idUsuario)) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        validarAccesoUsuario(idUsuario);

        return movimientoRepository.findByUsuario_IdUsuario(idUsuario).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    // ==========================================
    // LISTAR MOVIMIENTOS POR TIPO
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorTipo(
            String tipoMovimiento) {

        TipoMovimiento tipo;

        try {
            tipo = TipoMovimiento.valueOf(
                    tipoMovimiento.trim().toUpperCase()
            );
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Tipo de movimiento no válido");
        }

        return filtrarMovimientosDelUsuario(
                movimientoRepository.findByTipoMovimiento(tipo)
        ).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    // ==========================================
    // LISTAR MOVIMIENTOS POR BODEGA
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorBodega(
            Integer idBodega) {

        if (!bodegaRepository.existsById(idBodega)) {
            throw new IllegalArgumentException("Bodega no encontrada");
        }

        List<Movimiento> movimientos =
                movimientoRepository
                        .findByBodegaOrigen_IdBodegaOrBodegaDestino_IdBodega(
                                idBodega,
                                idBodega
                        );

        return filtrarMovimientosDelUsuario(movimientos).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    // ==========================================
    // LISTAR MOVIMIENTOS ENTRE FECHAS
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosEntreFechas(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException(
                    "Debe indicar la fecha de inicio y la fecha de fin");
        }

        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException(
                    "La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        List<Movimiento> movimientos =
                movimientoRepository.findByFechaBetweenOrderByFechaDesc(
                        fechaInicio,
                        fechaFin
                );

        return filtrarMovimientosDelUsuario(movimientos).stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    // ==========================================
    // CONVERTIR MOVIMIENTO A RESPUESTA
    // ==========================================

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

    // ==========================================
    // CONVERTIR BODEGA A RESPUESTA
    // ==========================================

    private BodegaResponse convertirBodega(Bodega bodega) {

        return bodegaMapper.entityToDto(
                bodega,
                ciudadMapper.entityToDto(bodega.getCiudad()),
                usuarioMapper.entityToDto(bodega.getEncargado())
        );
    }

    // ==========================================
    // CONVERTIR PRODUCTO A RESPUESTA
    // ==========================================

    private ProductoResponse convertirProducto(Producto producto) {

        return productoMapper.entityToDto(
                producto,
                marcaMapper.entityToDto(producto.getMarca()),
                categoriaMapper.entityToDto(producto.getCategoria())
        );
    }

    // ==========================================
    // PRODUCTOS CON MAYOR MOVIMIENTO
    // SOLO ADMINISTRADOR
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public List<ProductoMayorMovimientoResponse> listarProductosConMayorMovimiento() {

        if (!esAdministrador()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No tienes permiso para consultar este reporte"
            );
        }

        return detalleMovimientoRepository.obtenerProductosConMayorMovimiento();
    }
}
