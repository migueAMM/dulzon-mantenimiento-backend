package com.dulzonSA.mantenimiento.models;

import com.dulzonSA.mantenimiento.models.enums.TipoMaquina;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "maquinas")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Maquina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMaquina tipo;

    private String descripcion;

    // Código o número de serie interno de la máquina
    @Column(unique = true)
    private String codigoInterno;
}
