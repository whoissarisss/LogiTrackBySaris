package com.saris.logitrack.dto.response;

import com.saris.logitrack.dto.response.CiudadResponse;
import com.saris.logitrack.dto.response.UsuarioResponse;
import java.math.BigDecimal;

public record BodegaResponse(

        Long idBodega,
        String nombre,
        BigDecimal capacidad,
        CiudadResponse ciudad,
        UsuarioResponse encargado

) {
}