package com.saris.logitrack.mapper;

import com.saris.logitrack.dto.request.InventarioRequest;
import com.saris.logitrack.dto.response.BodegaResponse;
import com.saris.logitrack.dto.response.InventarioResponse;
import com.saris.logitrack.dto.response.ProductoResponse;
import com.saris.logitrack.model.Bodega;
import com.saris.logitrack.model.Inventario;
import com.saris.logitrack.model.Producto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class InventarioMapper {

    public Inventario toEntity(
            InventarioRequest request,
            Bodega bodega,
            Producto producto) {

        Inventario inventario = new Inventario();

        inventario.setBodega(bodega);
        inventario.setProducto(producto);
        inventario.setStockActual(BigDecimal.ZERO);

        return inventario;
    }

    public InventarioResponse toResponse(
            Inventario inventario,
            BodegaResponse bodega,
            ProductoResponse producto) {

        return new InventarioResponse(
                inventario.getIdInventario(),
                bodega,
                producto,
                inventario.getStockActual()
        );
    }
}
