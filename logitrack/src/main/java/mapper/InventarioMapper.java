package mapper;

import dto.request.InventarioRequest;
import dto.response.BodegaResponse;
import dto.response.InventarioResponse;
import dto.response.ProductoResponse;
import model.Bodega;
import model.Inventario;
import model.Producto;
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
