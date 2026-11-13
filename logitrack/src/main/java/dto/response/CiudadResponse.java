package com.saris.logitrack.dto.response;

public record CiudadResponse(

        Long idCiudad,
        String nombre,
        String departamento,
        String pais

) {
}
