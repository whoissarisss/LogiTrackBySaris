package com.saris.logitrack.dto.response;

import com.saris.logitrack.dto.response.MarcaResponse;
import com.saris.logitrack.dto.response.CategoriaResponse;
public record ProductoResponse(

        Long idProducto,
        String nombre,
        String descripcion,
        Boolean estado,
        MarcaResponse marca,
        CategoriaResponse categoria

) {
}