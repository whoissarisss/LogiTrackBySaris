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

    public BodegaResponse entityToDto(Bodega bodega, CiudadResponse ciudad, UsuarioResponse encargado) {
        if (bodega == null) {
            return null;
        }
        return new BodegaResponse(
                bodega.getIdBodega(),
                bodega.getNombre(),
                bodega.getCapacidad(),
                ciudad,
                encargado
        );
    }

    public Bodega DtoToEntity(BodegaRequest dto, Ciudad ciudad, Usuario encargado) {
        if (dto == null) {
            return null;
        }
        Bodega bodega = new Bodega();
        bodega.setNombre(dto.nombre());
        bodega.setCapacidad(dto.capacidad());
        bodega.setCiudad(ciudad);
        bodega.setEncargado(encargado);
        return bodega;
    }

    public void updateEntityFromDto(BodegaRequest dto, Bodega bodega, Ciudad ciudad, Usuario encargado) {
        if (dto == null || bodega == null) {
            return;
        }
        bodega.setNombre(dto.nombre());
        bodega.setCapacidad(dto.capacidad());
        bodega.setCiudad(ciudad);
        bodega.setEncargado(encargado);
    }
}
