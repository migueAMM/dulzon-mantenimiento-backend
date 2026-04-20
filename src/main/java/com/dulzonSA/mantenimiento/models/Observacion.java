package com.dulzonSA.mantenimiento.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "observaciones")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Observacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Observación ligada a una actividad específica (puede ser null si es del cierre general)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actividad_id")
    private ActividadMantenimiento actividad;

    // Observación ligada al cierre general (puede ser null si es de una actividad)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carta_gantt_id")
    private CartaGantt cartaGantt;

    // Supervisor que registra la observación
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", nullable = false)
    private Usuario supervisor;

    @Column(length = 2000, nullable = false)
    private String texto;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @PrePersist
    public void prePersist() {
        if (this.fechaHora == null) {
            this.fechaHora = LocalDateTime.now();
        }
    }
}
