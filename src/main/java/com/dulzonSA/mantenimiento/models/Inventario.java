package com.dulzonSA.mantenimiento.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventario")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreInsumo;

    private String descripcion;

    @Column(nullable = false)
    private Integer cantidadDisponible;

    private String unidadMedida;

    // Asociación opcional a una carta gantt (insumos reservados para esa mantención)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carta_gantt_id")
    private CartaGantt cartaGantt;
}
