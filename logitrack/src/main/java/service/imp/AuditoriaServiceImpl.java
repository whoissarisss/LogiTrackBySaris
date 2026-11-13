package service.imp;

import dto.response.AuditoriaResponse;
import mapper.AuditoriaMapper;
import mapper.UsuarioMapper;
import model.Auditoria;
import repository.AuditoriaRepository;
import repository.UsuarioRepository;
import service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;

    private final AuditoriaMapper auditoriaMapper;
    private final UsuarioMapper usuarioMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaResponse> listarAuditorias() {

        List<Auditoria> auditorias = auditoriaRepository.findAll();

        return auditorias.stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AuditoriaResponse obtenerAuditoriaPorId(Long idAuditoria) {

        Auditoria auditoria = auditoriaRepository.findById(idAuditoria)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Registro de auditoría no encontrado"));

        return convertirARespuesta(auditoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaResponse> listarAuditoriasPorUsuario(Long idUsuario) {

        if (!usuarioRepository.existsById(idUsuario)) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        List<Auditoria> auditorias =
                auditoriaRepository.findByUsuario_IdUsuario(idUsuario);

        return auditorias.stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaResponse> listarAuditoriasPorTabla(String tablaAfectada) {

        List<Auditoria> auditorias =
                auditoriaRepository.findByTablaAfectada(tablaAfectada);

        return auditorias.stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    private AuditoriaResponse convertirARespuesta(Auditoria auditoria) {

        return auditoriaMapper.entityToDto(
                auditoria,
                usuarioMapper.entityToDto(auditoria.getUsuario())
        );
    }
}
