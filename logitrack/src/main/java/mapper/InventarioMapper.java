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

    public InventarioResponse entityToDto(Inventario inventario, BodegaResponse bodega, ProductoResponse producto) {
        if (inventario == null) {
            return null;
        }
        return new InventarioResponse(
                inventario.getIdInventario(),
                bodega,
                producto,
                inventario.getStockActual()
        );
    }

    public Inventario DtoToEntity(InventarioRequest dto, Bodega bodega, Producto producto) {
        if (dto == null) {
            return null;
        }
        Inventario inventario = new Inventario();
        inventario.setBodega(bodega);
        inventario.setProducto(producto);
        inventario.setStockActual(BigDecimal.ZERO);
        return inventario;
    }
}
