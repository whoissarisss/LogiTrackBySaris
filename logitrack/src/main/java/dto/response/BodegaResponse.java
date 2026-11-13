package com.saris.logitrack.dto.response;


import com.saris.logitrack.dto.response.CiudadResponse;
import com.saris.logitrack.dto.response.UsuarioResponse;

public record BodegaResponse(

        Long idBodega,
        String nombre,
        Integer capacidad,
        CiudadResponse ciudad,
        UsuarioResponse encargado

) {
}