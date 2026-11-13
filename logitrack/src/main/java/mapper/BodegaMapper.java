package com.saris.logitrack.mapper;

import com.saris.logitrack.dto.request.BodegaRequest;
import com.saris.logitrack.dto.response.BodegaResponse;
import com.saris.logitrack.dto.response.CiudadResponse;
import com.saris.logitrack.dto.response.UsuarioResponse;
import com.saris.logitrack.model.Bodega;
import com.saris.logitrack.model.Ciudad;
import com.saris.logitrack.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class BodegaMapper {

    public Bodega toEntity(
            BodegaRequest request,
            Ciudad ciudad,
            Usuario encargado) {

        Bodega bodega = new Bodega();

        bodega.setNombre(request.nombre());
        bodega.setCapacidad(request.capacidad());
        bodega.setCiudad(ciudad);
        bodega.setEncargado(encargado);

        return bodega;
    }

    public BodegaResponse toResponse(
            Bodega bodega,
            CiudadResponse ciudad,
            UsuarioResponse encargado) {

        return new BodegaResponse(
                bodega.getIdBodega(),
                bodega.getNombre(),
                bodega.getCapacidad(),
                ciudad,
                encargado
        );
    }
}
