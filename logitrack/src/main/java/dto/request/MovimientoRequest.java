package com.saris.logitrack.dto.request;

import com.saris.logitrack.model.TipoMovimiento;
import com.saris.logitrack.dto.request.DetalleMovimientoRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record MovimientoRequest(

        @NotNull(message = "El tipo de movimiento es obligatorio")
        TipoMovimiento tipoMovimiento,

        @Positive(message = "El identificador de la bodega de origen debe ser positivo")
        Long idBodegaOrigen,

        @Positive(message = "El identificador de la bodega de destino debe ser positivo")
        Long idBodegaDestino,

        @NotEmpty(message = "El movimiento debe contener al menos un producto")
        List<@Valid DetalleMovimientoRequest> detalles

) {
}
