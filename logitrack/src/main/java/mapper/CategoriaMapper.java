package com.saris.logitrack.mapper;

import com.saris.logitrack.dto.request.CategoriaRequest;
import com.saris.logitrack.dto.response.CategoriaResponse;
import com.saris.logitrack.model.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    public CategoriaResponse entityToDto(Categoria categoria) {
        if (categoria == null) return null;
        return new CategoriaResponse(
                categoria.getIdCategoria(),
                categoria.getNombre()
        );
    }

    public Categoria DtoToEntity(CategoriaRequest dto) {
        if (dto == null) return null;
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.nombre());
        return categoria;
    }

    public void updateEntityFromDto(CategoriaRequest dto, Categoria categoria) {
        if (dto == null || categoria == null) return;
        categoria.setNombre(dto.nombre());
    }
}
