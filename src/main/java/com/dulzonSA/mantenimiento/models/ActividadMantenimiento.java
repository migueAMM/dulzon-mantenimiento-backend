package com.dulzonSA.mantenimiento.models;

import com.dulzonSA.mantenimiento.models.enums.EstadoActividad;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "actividades_mantenimiento")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActividadMantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carta_gantt_id", nullable = false)
    private CartaGantt cartaGantt;

    @Column(nullable = false)
    private String descripcion;

    // Posición dentro de la carta gantt
    @Column(nullable = false)
    private Integer orden;

    // Duración estimada en minutos (según lo informado por empresa mantenedora)
    private Integer duracionEstimadaMinutos;

    // Horas exactas registradas por el supervisor en terreno
    private LocalDateTime fechaInicioReal;
    private LocalDateTime fechaFinReal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoActividad estado = EstadoActividad.PENDIENTE;

    // Observaciones asociadas a esta actividad específica
    @OneToMany(mappedBy = "actividad", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaHora ASC")
    @Builder.Default
    private List<Observacion> observaciones = new ArrayList<>();

    // Calcula la desviación en minutos respecto a lo estimado (negativo = adelanto)
    @Transient
    public Long getDesviacionMinutos() {
        if (fechaInicioReal == null || fechaFinReal == null || duracionEstimadaMinutos == null) {
            return null;
        }
        long duracionReal = java.time.Duration.between(fechaInicioReal, fechaFinReal).toMinutes();
        return duracionReal - duracionEstimadaMinutos;
    }
}
