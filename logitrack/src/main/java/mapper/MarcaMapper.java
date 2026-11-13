package mapper;

import dto.request.MarcaRequest;
import dto.response.MarcaResponse;
import model.Marca;
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
