package service;

import model.Operacion;

public interface AuditoriaRegistroService {

    void registrar(
            Operacion operacion,
            String tablaAfectada,
            Integer registroId,
            Object valoresAnteriores,
            Object valoresNuevos
    );
}