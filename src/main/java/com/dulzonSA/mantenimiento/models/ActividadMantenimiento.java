package com.dulzonSA.mantenimiento.models;

import com.dulzonSA.mantenimiento.models.enums.EstadoActividad;
import jakarta.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "actividades_mantenimiento")
public class ActividadMantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carta_gantt_id", nullable = false)
    private CartaGantt cartaGantt;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private Integer orden;

    private Integer duracionEstimadaMinutos;

    private LocalDateTime fechaInicioReal;
    private LocalDateTime fechaFinReal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoActividad estado = EstadoActividad.PENDIENTE;

    @OneToMany(mappedBy = "actividad", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaHora ASC")
    private List<Observacion> observaciones = new ArrayList<>();

    public ActividadMantenimiento() {}

    /** Calcula desviación real vs estimada. Positivo = atraso, Negativo = adelanto. Null si no terminó. */
    @Transient
    public Long getDesviacionMinutos() {
        if (fechaInicioReal == null || fechaFinReal == null || duracionEstimadaMinutos == null) {
            return null;
        }
        long duracionReal = Duration.between(fechaInicioReal, fechaFinReal).toMinutes();
        return duracionReal - duracionEstimadaMinutos;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CartaGantt getCartaGantt() { return cartaGantt; }
    public void setCartaGantt(CartaGantt cartaGantt) { this.cartaGantt = cartaGantt; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }

    public Integer getDuracionEstimadaMinutos() { return duracionEstimadaMinutos; }
    public void setDuracionEstimadaMinutos(Integer duracionEstimadaMinutos) {
        this.duracionEstimadaMinutos = duracionEstimadaMinutos;
    }

    public LocalDateTime getFechaInicioReal() { return fechaInicioReal; }
    public void setFechaInicioReal(LocalDateTime fechaInicioReal) {
        this.fechaInicioReal = fechaInicioReal;
    }

    public LocalDateTime getFechaFinReal() { return fechaFinReal; }
    public void setFechaFinReal(LocalDateTime fechaFinReal) {
        this.fechaFinReal = fechaFinReal;
    }

    public EstadoActividad getEstado() { return estado; }
    public void setEstado(EstadoActividad estado) { this.estado = estado; }

    public List<Observacion> getObservaciones() { return observaciones; }
    public void setObservaciones(List<Observacion> observaciones) {
        this.observaciones = observaciones;
    }
}
