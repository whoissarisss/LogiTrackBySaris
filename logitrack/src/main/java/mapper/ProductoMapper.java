package com.saris.logitrack.mapper;

import com.saris.logitrack.dto.request.ProductoRequest;
import com.saris.logitrack.dto.response.CategoriaResponse;
import com.saris.logitrack.dto.response.MarcaResponse;
import com.saris.logitrack.dto.response.ProductoResponse;
import com.saris.logitrack.model.Categoria;
import com.saris.logitrack.model.Marca;
import com.saris.logitrack.model.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public ProductoResponse entityToDto(Producto producto, MarcaResponse marca, CategoriaResponse categoria) {
        if (producto == null) {
            return null;
        }
        return new ProductoResponse(
                producto.getIdProducto(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getEstado(),
                marca,
                categoria
        );
    }

    public Producto DtoToEntity(ProductoRequest dto, Marca marca, Categoria categoria) {
        if (dto == null) {
            return null;
        }
        Producto producto = new Producto();
        producto.setNombre(dto.nombre());
        producto.setDescripcion(dto.descripcion());
        producto.setEstado(true);
        producto.setMarca(marca);
        producto.setCategoria(categoria);

        return producto;
    }

    public void updateEntityFromDto(ProductoRequest dto, Producto producto, Marca marca, Categoria categoria) {
        if (dto == null || producto == null) {
            return;
        }
        producto.setNombre(dto.nombre());
        producto.setDescripcion(dto.descripcion());
        producto.setMarca(marca);
        producto.setCategoria(categoria);
    }
}
