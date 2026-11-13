package com.saris.logitrack.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CiudadRequest(

        @NotBlank(message = "El nombre de la ciudad es obligatorio")
        String nombre,

        @NotBlank(message = "El departamento es obligatorio")
        String departamento,

        @NotBlank(message = "El país es obligatorio")
        String pais

) {
}
