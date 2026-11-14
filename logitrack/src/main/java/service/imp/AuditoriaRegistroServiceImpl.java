package service.imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import model.Auditoria;
import model.Operacion;
import model.Usuario;
import repository.AuditoriaRepository;
import repository.UsuarioRepository;
import service.AuditoriaRegistroService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditoriaRegistroServiceImpl implements AuditoriaRegistroService {

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void registrar(
            Operacion operacion,
            String tablaAfectada,
            Long registroId,
            Object valoresAnteriores,
            Object valoresNuevos
    ) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException(
                    "No se pudo identificar al usuario que realiza la operación"
            );
        }

        Usuario usuario = usuarioRepository.findByCorreo(
                authentication.getName()
        ).orElseThrow(() -> new IllegalStateException(
                "No se encontró el usuario que realiza la operación"
        ));

        Auditoria auditoria = new Auditoria();

        auditoria.setOperacion(operacion);
        auditoria.setUsuario(usuario);
        auditoria.setTablaAfectada(tablaAfectada);
        auditoria.setRegistroId(registroId);
        auditoria.setValoresAnteriores(convertirAJson(valoresAnteriores));
        auditoria.setValoresNuevos(convertirAJson(valoresNuevos));

        auditoriaRepository.save(auditoria);
    }

    private String convertirAJson(Object valores) {

        if (valores == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(valores);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "No fue posible convertir los datos de auditoría a JSON", e
            );
        }
    }
}