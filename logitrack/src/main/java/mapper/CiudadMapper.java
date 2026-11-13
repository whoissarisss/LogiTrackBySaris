package mapper;

import dto.request.CiudadRequest;
import dto.response.CiudadResponse;
import model.Ciudad;
import org.springframework.stereotype.Component;

@Component
public class CiudadMapper {

    public CiudadResponse entityToDto(Ciudad ciudad) {
        if (ciudad == null) return null;
        return new CiudadResponse(
                ciudad.getIdCiudad(),
                ciudad.getNombre(),
                ciudad.getDepartamento(),
                ciudad.getPais()
        );
    }

    public Ciudad DtoToEntity(CiudadRequest dto) {
        if (dto == null) return null;
        Ciudad ciudad = new Ciudad();
        ciudad.setNombre(dto.nombre());
        ciudad.setDepartamento(dto.departamento());
        ciudad.setPais(dto.pais());
        return ciudad;
    }

    public void updateEntityFromDto(CiudadRequest dto, Ciudad ciudad) {
        if (dto == null || ciudad == null) return;
        ciudad.setNombre(dto.nombre());
        ciudad.setDepartamento(dto.departamento());
        ciudad.setPais(dto.pais());
    }
}
