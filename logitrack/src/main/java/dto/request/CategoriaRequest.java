package com.saris.logitrack.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequest(

        @NotBlank(message = "El nombre de la categoría es obligatorio")
        String nombre

) {
}
