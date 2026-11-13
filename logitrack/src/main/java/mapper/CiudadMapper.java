package com.saris.logitrack.mapper;

import com.saris.logitrack.dto.request.CiudadRequest;
import com.saris.logitrack.dto.response.CiudadResponse;
import com.saris.logitrack.model.Ciudad;
import org.springframework.stereotype.Component;

@Component
public class CiudadMapper {

    public Ciudad toEntity(CiudadRequest request) {

        Ciudad ciudad = new Ciudad();

        ciudad.setNombre(request.nombre());
        ciudad.setDepartamento(request.departamento());
        ciudad.setPais(request.pais());

        return ciudad;
    }

    public CiudadResponse toResponse(Ciudad ciudad) {

        return new CiudadResponse(
                ciudad.getIdCiudad(),
                ciudad.getNombre(),
                ciudad.getDepartamento(),
                ciudad.getPais()
        );
    }
}
