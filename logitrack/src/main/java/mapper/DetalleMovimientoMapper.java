package com.saris.logitrack.mapper;

import com.saris.logitrack.dto.request.DetalleMovimientoRequest;
import com.saris.logitrack.dto.response.DetalleMovimientoResponse;
import com.saris.logitrack.dto.response.ProductoResponse;
import com.saris.logitrack.model.DetalleMovimiento;
import com.saris.logitrack.model.Movimiento;
import com.saris.logitrack.model.Producto;
import org.springframework.stereotype.Component;

@Component
public class DetalleMovimientoMapper {

    public DetalleMovimiento toEntity(
            DetalleMovimientoRequest request,
            Movimiento movimiento,
            Producto producto) {

        DetalleMovimiento detalle = new DetalleMovimiento();

        detalle.setMovimiento(movimiento);
        detalle.setProducto(producto);
        detalle.setCantidad(request.cantidad());

        return detalle;
    }

    public DetalleMovimientoResponse toResponse(
            DetalleMovimiento detalle,
            ProductoResponse producto) {

        return new DetalleMovimientoResponse(
                detalle.getIdDetalle(),
                producto,
                detalle.getCantidad()
        );
    }
}
