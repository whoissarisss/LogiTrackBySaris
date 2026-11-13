package com.saris.logitrack.mapper;

import com.saris.logitrack.dto.request.MarcaRequest;
import com.saris.logitrack.dto.response.MarcaResponse;
import com.saris.logitrack.model.Marca;
import org.springframework.stereotype.Component;

@Component
public class MarcaMapper {
    public MarcaResponse entityToDto(Marca marca) {
        if (marca == null) return null;
        return new MarcaResponse(
                marca.getIdMarca(),
                marca.getNombre()
        );
    }

    public Marca DtoToEntity(MarcaRequest dto) {
        if (dto == null) return null;
        Marca marca = new Marca();
        marca.setNombre(dto.nombre());
        return marca;
    }

    public void updateEntityFromDto(MarcaRequest dto, Marca marca) {
        if (dto == null || marca == null) return;
        marca.setNombre(dto.nombre());
    }
}
