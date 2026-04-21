package com.dulzonSA.mantenimiento.models;

import com.dulzonSA.mantenimiento.models.enums.TipoTurno;
import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "turnos")
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TipoTurno nombre;

    private LocalTime horaInicio;
    private LocalTime horaFin;

    public Turno() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoTurno getNombre() { return nombre; }
    public void setNombre(TipoTurno nombre) { this.nombre = nombre; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
}
