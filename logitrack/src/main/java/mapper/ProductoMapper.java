package mapper;

import dto.request.ProductoRequest;
import dto.response.CategoriaResponse;
import dto.response.MarcaResponse;
import dto.response.ProductoResponse;
import model.Categoria;
import model.Marca;
import model.Producto;
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
