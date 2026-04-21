package com.dulzonSA.mantenimiento.dto.response;

import com.dulzonSA.mantenimiento.models.enums.EstadoActividad;
import java.time.LocalDateTime;
import java.util.List;

public class ActividadResponse {

    private Long id;
    private String descripcion;
    private Integer orden;
    private Integer duracionEstimadaMinutos;
    private LocalDateTime fechaInicioReal;
    private LocalDateTime fechaFinReal;
    private EstadoActividad estado;
    /** Positivo = atraso en minutos. Negativo = adelanto. Null = aún no terminó */
    private Long desviacionMinutos;
    private List<ObservacionResponse> observaciones;

    public ActividadResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }

    public Integer getDuracionEstimadaMinutos() { return duracionEstimadaMinutos; }
    public void setDuracionEstimadaMinutos(Integer duracionEstimadaMinutos) {
        this.duracionEstimadaMinutos = duracionEstimadaMinutos;
    }

    public LocalDateTime getFechaInicioReal() { return fechaInicioReal; }
    public void setFechaInicioReal(LocalDateTime fechaInicioReal) { this.fechaInicioReal = fechaInicioReal; }

    public LocalDateTime getFechaFinReal() { return fechaFinReal; }
    public void setFechaFinReal(LocalDateTime fechaFinReal) { this.fechaFinReal = fechaFinReal; }

    public EstadoActividad getEstado() { return estado; }
    public void setEstado(EstadoActividad estado) { this.estado = estado; }

    public Long getDesviacionMinutos() { return desviacionMinutos; }
    public void setDesviacionMinutos(Long desviacionMinutos) { this.desviacionMinutos = desviacionMinutos; }

    public List<ObservacionResponse> getObservaciones() { return observaciones; }
    public void setObservaciones(List<ObservacionResponse> observaciones) { this.observaciones = observaciones; }
}
