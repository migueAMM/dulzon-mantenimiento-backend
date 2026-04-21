package com.dulzonSA.mantenimiento.models;

import com.dulzonSA.mantenimiento.models.enums.EstadoMantenimiento;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cartas_gantt")
public class CartaGantt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maquina_id", nullable = false)
    private Maquina maquina;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operador_id", nullable = false)
    private Usuario operador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turno_id", nullable = false)
    private Turno turno;

    @Column(nullable = false)
    private LocalDate fechaProgramada;

    private LocalDateTime fechaInicioReal;
    private LocalDateTime fechaFinReal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMantenimiento estado = EstadoMantenimiento.PROGRAMADO;

    @OneToMany(mappedBy = "cartaGantt", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC")
    private List<ActividadMantenimiento> actividades = new ArrayList<>();

    @OneToMany(mappedBy = "cartaGantt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Observacion> observacionesGenerales = new ArrayList<>();

    public CartaGantt() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Maquina getMaquina() { return maquina; }
    public void setMaquina(Maquina maquina) { this.maquina = maquina; }

    public Usuario getOperador() { return operador; }
    public void setOperador(Usuario operador) { this.operador = operador; }

    public Turno getTurno() { return turno; }
    public void setTurno(Turno turno) { this.turno = turno; }

    public LocalDate getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDate fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public LocalDateTime getFechaInicioReal() { return fechaInicioReal; }
    public void setFechaInicioReal(LocalDateTime fechaInicioReal) {
        this.fechaInicioReal = fechaInicioReal;
    }

    public LocalDateTime getFechaFinReal() { return fechaFinReal; }
    public void setFechaFinReal(LocalDateTime fechaFinReal) {
        this.fechaFinReal = fechaFinReal;
    }

    public EstadoMantenimiento getEstado() { return estado; }
    public void setEstado(EstadoMantenimiento estado) { this.estado = estado; }

    public List<ActividadMantenimiento> getActividades() { return actividades; }
    public void setActividades(List<ActividadMantenimiento> actividades) {
        this.actividades = actividades;
    }

    public List<Observacion> getObservacionesGenerales() { return observacionesGenerales; }
    public void setObservacionesGenerales(List<Observacion> observacionesGenerales) {
        this.observacionesGenerales = observacionesGenerales;
    }
}
