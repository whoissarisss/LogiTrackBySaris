package service.imp;

import dto.request.BodegaRequest;
import dto.response.BodegaResponse;
import mapper.BodegaMapper;
import mapper.CiudadMapper;
import mapper.UsuarioMapper;
import model.Bodega;
import model.Ciudad;
import model.Usuario;
import repository.BodegaRepository;
import repository.CiudadRepository;
import repository.UsuarioRepository;
import service.BodegaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BodegaServiceImpl implements BodegaService {

    private final BodegaRepository bodegaRepository;
    private final CiudadRepository ciudadRepository;
    private final UsuarioRepository usuarioRepository;

    private final BodegaMapper bodegaMapper;
    private final CiudadMapper ciudadMapper;
    private final UsuarioMapper usuarioMapper;

    @Override
    public BodegaResponse registrarBodega(BodegaRequest dto) {

        Ciudad ciudad = ciudadRepository.findById(dto.idCiudad())
                .orElseThrow(() -> new IllegalArgumentException("Ciudad no encontrada"));

        Usuario encargado = usuarioRepository.findById(dto.idEncargado())
                .orElseThrow(() -> new IllegalArgumentException("Encargado no encontrado"));

        Bodega bodega = bodegaMapper.DtoToEntity(dto, ciudad, encargado);

        Bodega bodegaGuardada = bodegaRepository.save(bodega);

        return convertirARespuesta(bodegaGuardada);
    }

    @Override
    public List<BodegaResponse> listarBodegas() {

        List<Bodega> bodegas = bodegaRepository.findAll();

        return bodegas.stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    public BodegaResponse obtenerBodegaPorId(Long idBodega) {

        Bodega bodega = bodegaRepository.findById(idBodega)
                .orElseThrow(() -> new IllegalArgumentException("Bodega no encontrada"));

        return convertirARespuesta(bodega);
    }

    @Override
    public List<BodegaResponse> listarBodegasPorCiudad(Long idCiudad) {

        if (!ciudadRepository.existsById(idCiudad)) {
            throw new IllegalArgumentException("Ciudad no encontrada");
        }

        List<Bodega> bodegas = bodegaRepository.findByCiudad_IdCiudad(idCiudad);

        return bodegas.stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    public BodegaResponse actualizarBodega(Long idBodega, BodegaRequest dto) {

        Bodega bodega = bodegaRepository.findById(idBodega)
                .orElseThrow(() -> new IllegalArgumentException("Bodega no encontrada"));

        Ciudad ciudad = ciudadRepository.findById(dto.idCiudad())
                .orElseThrow(() -> new IllegalArgumentException("Ciudad no encontrada"));

        Usuario encargado = usuarioRepository.findById(dto.idEncargado())
                .orElseThrow(() -> new IllegalArgumentException("Encargado no encontrado"));

        bodegaMapper.updateEntityFromDto(dto, bodega, ciudad, encargado);

        Bodega bodegaActualizada = bodegaRepository.save(bodega);

        return convertirARespuesta(bodegaActualizada);
    }

    @Override
    public void eliminarBodega(Long idBodega) {

        Bodega bodega = bodegaRepository.findById(idBodega)
                .orElseThrow(() -> new IllegalArgumentException("Bodega no encontrada"));

        bodegaRepository.delete(bodega);
    }

    private BodegaResponse convertirARespuesta(Bodega bodega) {

        return bodegaMapper.entityToDto(
                bodega,
                ciudadMapper.entityToDto(bodega.getCiudad()),
                usuarioMapper.entityToDto(bodega.getEncargado())
        );
    }
}
