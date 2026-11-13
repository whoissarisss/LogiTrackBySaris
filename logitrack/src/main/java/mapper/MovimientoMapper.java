package mapper;

import dto.request.MovimientoRequest;
import dto.response.BodegaResponse;
import dto.response.DetalleMovimientoResponse;
import dto.response.MovimientoResponse;
import dto.response.UsuarioResponse;
import model.Bodega;
import model.Movimiento;
import model.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovimientoMapper {

    public MovimientoResponse entityToDto(Movimiento movimiento, UsuarioResponse usuario, BodegaResponse origen, BodegaResponse destino,
            List<DetalleMovimientoResponse> detalles) {
        if (movimiento == null) {
            return null;
        }
        return new MovimientoResponse(movimiento.getIdMovimiento(), movimiento.getFecha(), movimiento.getTipoMovimiento(), usuario, origen, destino, detalles);
    }

    public Movimiento DtoToEntity(MovimientoRequest dto, Usuario usuario, Bodega origen, Bodega destino) {
        if (dto == null) {
            return null;
        }
        Movimiento movimiento = new Movimiento();
        movimiento.setTipoMovimiento(dto.tipoMovimiento());
        movimiento.setUsuario(usuario);
        movimiento.setBodegaOrigen(origen);
        movimiento.setBodegaDestino(destino);
        return movimiento;
    }
}
