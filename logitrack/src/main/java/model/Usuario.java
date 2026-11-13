package com.saris.logitrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private long idUsuario;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 80)
    private String apellido;

    @Column(nullable = false, length = 120, unique = true)
    private String correo;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_solicitud", nullable = false)
    private EstadoSolicitud estadoSolicitud;

    @Column(name = "fecha_registro", nullable = false,
            insertable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_revision")
    private LocalDateTime fechaRevision;

    @Column(name = "motivo_rechazo", length = 500)
    private String motivoRechazo;
}
