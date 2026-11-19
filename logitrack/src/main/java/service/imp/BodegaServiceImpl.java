package service.imp;

import dto.request.BodegaRequest;
import dto.response.BodegaResponse;
import mapper.BodegaMapper;
import mapper.CiudadMapper;
import mapper.UsuarioMapper;
import model.Bodega;
import model.Ciudad;
import model.Usuario;
import model.Operacion;
import repository.BodegaRepository;
import repository.CiudadRepository;
import repository.UsuarioRepository;
import repository.InventarioRepository;
import repository.MovimientoRepository;
import service.BodegaService;
import service.AuditoriaRegistroService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BodegaServiceImpl implements BodegaService {

    private final BodegaRepository bodegaRepository;
    private final CiudadRepository ciudadRepository;
    private final UsuarioRepository usuarioRepository;
    private final InventarioRepository inventarioRepository;
    private final MovimientoRepository movimientoRepository;

    private final BodegaMapper bodegaMapper;
    private final CiudadMapper ciudadMapper;
    private final UsuarioMapper usuarioMapper;
    private final AuditoriaRegistroService auditoriaRegistroService;

    @Override
    @Transactional
    public BodegaResponse registrarBodega(BodegaRequest dto) {

        Ciudad ciudad = ciudadRepository.findById(dto.idCiudad())
                .orElseThrow(() -> new IllegalArgumentException("Ciudad no encontrada"));

        Usuario encargado = usuarioRepository.findById(dto.idEncargado())
                .orElseThrow(() -> new IllegalArgumentException("Encargado no encontrado"));

        Bodega bodega = bodegaMapper.dtoToEntity(dto, ciudad, encargado);

        Bodega bodegaGuardada = bodegaRepository.save(bodega);

        auditoriaRegistroService.registrar(
                Operacion.INSERT,
                "bodega",
                bodegaGuardada.getIdBodega(),
                null,
                convertirARespuesta(bodegaGuardada)
        );

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
    public BodegaResponse obtenerBodegaPorId(Integer idBodega) {

        Bodega bodega = bodegaRepository.findById(idBodega)
                .orElseThrow(() -> new IllegalArgumentException("Bodega no encontrada"));

        return convertirARespuesta(bodega);
    }

    @Override
    public List<BodegaResponse> listarBodegasPorCiudad(Integer idCiudad) {

        if (!ciudadRepository.existsById(idCiudad)) {
            throw new IllegalArgumentException("Ciudad no encontrada");
        }

        List<Bodega> bodegas = bodegaRepository.findByCiudad_IdCiudad(idCiudad);

        return bodegas.stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    @Transactional
    public BodegaResponse actualizarBodega(Integer idBodega, BodegaRequest dto) {

        Bodega bodega = bodegaRepository.findById(idBodega)
                .orElseThrow(() -> new IllegalArgumentException("Bodega no encontrada"));

        BodegaResponse valoresAnteriores = convertirARespuesta(bodega);

        Ciudad ciudad = ciudadRepository.findById(dto.idCiudad())
                .orElseThrow(() -> new IllegalArgumentException("Ciudad no encontrada"));

        Usuario encargado = usuarioRepository.findById(dto.idEncargado())
                .orElseThrow(() -> new IllegalArgumentException("Encargado no encontrado"));

        bodegaMapper.updateEntityFromDto(dto, bodega, ciudad, encargado);

        Bodega bodegaActualizada = bodegaRepository.save(bodega);

        auditoriaRegistroService.registrar(
                Operacion.UPDATE,
                "bodega",
                bodegaActualizada.getIdBodega(),
                valoresAnteriores,
                convertirARespuesta(bodegaActualizada)
        );

        return convertirARespuesta(bodegaActualizada);
    }

    @Override
    @Transactional
    public void eliminarBodega(Integer idBodega) {

        Bodega bodega = bodegaRepository.findById(idBodega)
                .orElseThrow(() -> new IllegalArgumentException("Bodega no encontrada"));

        if (!inventarioRepository.findByBodega_IdBodega(idBodega).isEmpty()) {
            throw new IllegalArgumentException(
                    "No se puede eliminar la bodega porque tiene registros de inventario");
        }

        if (!movimientoRepository
                .findByBodegaOrigen_IdBodegaOrBodegaDestino_IdBodega(idBodega, idBodega)
                .isEmpty()) {
            throw new IllegalArgumentException(
                    "No se puede eliminar la bodega porque tiene movimientos registrados");
        }

        BodegaResponse valoresAnteriores = convertirARespuesta(bodega);

        auditoriaRegistroService.registrar(
                Operacion.DELETE,
                "bodega",
                bodega.getIdBodega(),
                valoresAnteriores,
                null
        );

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