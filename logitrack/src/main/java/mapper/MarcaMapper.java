package com.saris.logitrack.mapper;

import com.saris.logitrack.dto.request.MarcaRequest;
import com.saris.logitrack.dto.response.MarcaResponse;
import com.saris.logitrack.model.Marca;
import org.springframework.stereotype.Component;

@Component
public class MarcaMapper {

    public Marca toEntity(MarcaRequest request) {

        Marca marca = new Marca();

        marca.setNombre(request.nombre());

        return marca;
    }

    public MarcaResponse toResponse(Marca marca) {

        return new MarcaResponse(
                marca.getIdMarca(),
                marca.getNombre()
        );
    }
}
