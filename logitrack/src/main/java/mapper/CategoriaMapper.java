package com.saris.logitrack.mapper;

import com.saris.logitrack.dto.request.CategoriaRequest;
import com.saris.logitrack.dto.response.CategoriaResponse;
import com.saris.logitrack.model.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    public Categoria toEntity(CategoriaRequest request) {

        Categoria categoria = new Categoria();

        categoria.setNombre(request.nombre());

        return categoria;
    }

    public CategoriaResponse toResponse(Categoria categoria) {

        return new CategoriaResponse(
                categoria.getIdCategoria(),
                categoria.getNombre()
        );
    }
}
