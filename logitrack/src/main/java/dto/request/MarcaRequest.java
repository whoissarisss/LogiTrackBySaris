package com.saris.logitrack.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MarcaRequest(

        @NotBlank(message = "El nombre de la marca es obligatorio")
        String nombre

) {
}
