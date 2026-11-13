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

    public Producto toEntity(
            ProductoRequest request,
            Marca marca,
            Categoria categoria) {

        Producto producto = new Producto();

        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setEstado(true);
        producto.setMarca(marca);
        producto.setCategoria(categoria);

        return producto;
    }

    public ProductoResponse toResponse(
            Producto producto,
            MarcaResponse marca,
            CategoriaResponse categoria) {

        return new ProductoResponse(
                producto.getIdProducto(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getEstado(),
                marca,
                categoria
        );
    }
}
