package com.dulzonSA.mantenimiento.models;

import com.dulzonSA.mantenimiento.models.enums.TipoTurno;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "turnos")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TipoTurno nombre;

    private LocalTime horaInicio;
    private LocalTime horaFin;
}
