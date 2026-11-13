package com.saris.logitrack.mapper;

import com.saris.logitrack.dto.request.MovimientoRequest;
import com.saris.logitrack.dto.response.BodegaResponse;
import com.saris.logitrack.dto.response.DetalleMovimientoResponse;
import com.saris.logitrack.dto.response.MovimientoResponse;
import com.saris.logitrack.dto.response.UsuarioResponse;
import com.saris.logitrack.model.Bodega;
import com.saris.logitrack.model.Movimiento;
import com.saris.logitrack.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovimientoMapper {

    public Movimiento toEntity(
            MovimientoRequest request,
            Usuario usuario,
            Bodega bodegaOrigen,
            Bodega bodegaDestino) {

        Movimiento movimiento = new Movimiento();

        movimiento.setTipoMovimiento(request.tipoMovimiento());
        movimiento.setUsuario(usuario);
        movimiento.setBodegaOrigen(bodegaOrigen);
        movimiento.setBodegaDestino(bodegaDestino);

        return movimiento;
    }

    public MovimientoResponse toResponse(
            Movimiento movimiento,
            UsuarioResponse usuario,
            BodegaResponse bodegaOrigen,
            BodegaResponse bodegaDestino,
            List<DetalleMovimientoResponse> detalles) {

        return new MovimientoResponse(
                movimiento.getIdMovimiento(),
                movimiento.getFecha(),
                movimiento.getTipoMovimiento(),
                usuario,
                bodegaOrigen,
                bodegaDestino,
                detalles
        );
    }
}
