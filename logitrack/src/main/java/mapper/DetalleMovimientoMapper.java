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

    public DetalleMovimientoResponse entityToDto(DetalleMovimiento detalle, ProductoResponse producto) {
        if (detalle == null) {
            return null;
        }
        return new DetalleMovimientoResponse(
                detalle.getIdDetalle(),
                producto,
                detalle.getCantidad()
        );
    }

    public DetalleMovimiento DtoToEntity(DetalleMovimientoRequest dto, Movimiento movimiento, Producto producto) {
        if (dto == null) {
            return null;
        }
        DetalleMovimiento detalle = new DetalleMovimiento();
        detalle.setMovimiento(movimiento);
        detalle.setProducto(producto);
        detalle.setCantidad(dto.cantidad());
        return detalle;
    }
}
