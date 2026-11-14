package service;

import model.Operacion;

public interface AuditoriaRegistroService {

    void registrar(
            Operacion operacion,
            String tablaAfectada,
            Long registroId,
            Object valoresAnteriores,
            Object valoresNuevos
    );
}