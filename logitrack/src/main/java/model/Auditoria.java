package com.saris.logitrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private long idAuditoria;

    @Column(name = "fecha_hora", nullable = false,
            insertable = false, updatable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Operacion operacion;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "tabla_afectada", nullable = false, length = 100)
    private String tablaAfectada;

    @Column(name = "registro_id", nullable = false)
    private long registroId;

    @Column(name = "valores_anteriores", columnDefinition = "JSON")
    private String valoresAnteriores;

    @Column(name = "valores_nuevos", columnDefinition = "JSON")
    private String valoresNuevos;
}
