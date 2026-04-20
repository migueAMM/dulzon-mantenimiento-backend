package com.dulzonSA.mantenimiento.models;

import com.dulzonSA.mantenimiento.models.enums.EstadoMantenimiento;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cartas_gantt")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartaGantt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Máquina a mantener
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maquina_id", nullable = false)
    private Maquina maquina;

    // Operador que programó la mantención
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operador_id", nullable = false)
    private Usuario operador;

    // Turno en que inicia la mantención (según lo informado por empresa mantenedora)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turno_id", nullable = false)
    private Turno turno;

    // Fecha programada de inicio (informada por empresa mantenedora)
    @Column(nullable = false)
    private LocalDate fechaProgramada;

    // Registro exacto cuando el supervisor da inicio real al proceso
    private LocalDateTime fechaInicioReal;

    // Registro exacto cuando el supervisor cierra el proceso
    private LocalDateTime fechaFinReal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoMantenimiento estado = EstadoMantenimiento.PROGRAMADO;

    // Actividades que componen esta mantención (orden importa)
    @OneToMany(mappedBy = "cartaGantt", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC")
    @Builder.Default
    private List<ActividadMantenimiento> actividades = new ArrayList<>();

    // Observaciones generales al cierre del proceso
    @OneToMany(mappedBy = "cartaGantt", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Observacion> observacionesGenerales = new ArrayList<>();
}
