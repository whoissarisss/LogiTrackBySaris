package model;

import model.TipoMovimiento;
import model.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimiento")
@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private long idMovimiento;

    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false)
    private TipoMovimiento tipoMovimiento;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_bodega_origen")
    private Bodega bodegaOrigen;

    @ManyToOne
    @JoinColumn(name = "id_bodega_destino")
    private Bodega bodegaDestino;
}