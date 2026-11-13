package com.saris.logitrack.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record BodegaRequest(

        @NotBlank(message = "El nombre de la bodega es obligatorio")
        String nombre,

        @NotNull(message = "La capacidad es obligatoria")
        @DecimalMin(value = "0.01", message = "La capacidad debe ser mayor que cero")
        BigDecimal capacidad,

        @NotNull(message = "La ciudad es obligatoria")
        @Positive(message = "El identificador de la ciudad debe ser positivo")
        Long idCiudad,

        @NotNull(message = "El encargado es obligatorio")
        @Positive(message = "El identificador del encargado debe ser positivo")
        Long idEncargado

) {
}
