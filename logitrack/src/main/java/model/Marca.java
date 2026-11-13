package com.saris.logitrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "marca")
@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class Marca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_marca")
    private long idMarca;

    @Column(nullable = false, length = 100, unique = true)
    private String nombre;
}
