package service.imp;

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

import java.lang.reflect.Array;
import java.lang.reflect.RecordComponent;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuditoriaRegistroServiceImpl implements AuditoriaRegistroService {

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public void registrar(
            Operacion operacion,
            String tablaAfectada,
            Integer registroId,
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

        if (valores instanceof String texto) {
            return escaparTexto(texto);
        }

        if (valores instanceof Number || valores instanceof Boolean) {
            return valores.toString();
        }

        if (valores instanceof Enum<?> enumeracion) {
            return escaparTexto(enumeracion.name());
        }

        if (valores instanceof TemporalAccessor) {
            return escaparTexto(valores.toString());
        }

        if (valores instanceof Collection<?> coleccion) {
            StringBuilder json = new StringBuilder("[");
            boolean primero = true;

            for (Object elemento : coleccion) {
                if (!primero) {
                    json.append(",");
                }

                json.append(convertirAJson(elemento));
                primero = false;
            }

            return json.append("]").toString();
        }

        if (valores instanceof Map<?, ?> mapa) {
            StringBuilder json = new StringBuilder("{");
            boolean primero = true;

            for (Map.Entry<?, ?> entrada : mapa.entrySet()) {
                if (!primero) {
                    json.append(",");
                }

                json.append(escaparTexto(String.valueOf(entrada.getKey())));
                json.append(":");
                json.append(convertirAJson(entrada.getValue()));
                primero = false;
            }

            return json.append("}").toString();
        }

        if (valores.getClass().isArray()) {
            StringBuilder json = new StringBuilder("[");

            for (int i = 0; i < Array.getLength(valores); i++) {
                if (i > 0) {
                    json.append(",");
                }

                json.append(convertirAJson(Array.get(valores, i)));
            }

            return json.append("]").toString();
        }

        if (valores.getClass().isRecord()) {
            StringBuilder json = new StringBuilder("{");
            RecordComponent[] componentes =
                    valores.getClass().getRecordComponents();

            try {
                for (int i = 0; i < componentes.length; i++) {
                    if (i > 0) {
                        json.append(",");
                    }

                    RecordComponent componente = componentes[i];

                    json.append(escaparTexto(componente.getName()));
                    json.append(":");
                    json.append(convertirAJson(
                            componente.getAccessor().invoke(valores)
                    ));
                }

                return json.append("}").toString();

            } catch (ReflectiveOperationException e) {
                throw new IllegalArgumentException(
                        "No fue posible convertir los datos de auditoría a JSON",
                        e
                );
            }
        }

        throw new IllegalArgumentException(
                "Tipo de dato no compatible con la auditoría: "
                        + valores.getClass().getName()
        );
    }

    private String escaparTexto(String texto) {

        StringBuilder json = new StringBuilder("\"");

        for (int i = 0; i < texto.length(); i++) {
            char caracter = texto.charAt(i);

            switch (caracter) {
                case '"' -> json.append("\\\"");
                case '\\' -> json.append("\\\\");
                case '\b' -> json.append("\\b");
                case '\f' -> json.append("\\f");
                case '\n' -> json.append("\\n");
                case '\r' -> json.append("\\r");
                case '\t' -> json.append("\\t");
                default -> {
                    if (caracter < 0x20) {
                        json.append(String.format("\\u%04x", (int) caracter));
                    } else {
                        json.append(caracter);
                    }
                }
            }
        }

        return json.append("\"").toString();
    }
}
