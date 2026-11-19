package model;

import model.Ciudad;
import model.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bodega")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Bodega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bodega")
    private Integer idBodega;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false)
    private Integer capacidad;

    @ManyToOne
    @JoinColumn(name = "id_ciudad", nullable = false)
    private Ciudad ciudad;

    @ManyToOne
    @JoinColumn(name = "id_encargado", nullable = false)
    private Usuario encargado;
}
