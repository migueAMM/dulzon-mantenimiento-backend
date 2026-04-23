package com.dulzonSA.mantenimiento.dto.response;

import com.dulzonSA.mantenimiento.models.enums.EstadoActividad;
import com.dulzonSA.mantenimiento.models.enums.EstadoMantenimiento;

import java.time.LocalDateTime;

/**
 * DTO genérico para respuestas de acciones sobre mantenciones y actividades.
 * Evita serializar proxies de Hibernate (ByteBuddyInterceptor).
 */
public class AccionMantenimientoResponse {

    private Long id;
    private String tipo;           // "MANTENIMIENTO" | "ACTIVIDAD"
    private String estado;         // Valor del enum como String
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Long desviacionMinutos;
    private String mensaje;

    public AccionMantenimientoResponse() {}

    // ── Factory methods estáticos ─────────────────────────────

    public static AccionMantenimientoResponse deActividad(
            Long id,
            EstadoActividad estado,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Long desviacionMinutos) {

        AccionMantenimientoResponse r = new AccionMantenimientoResponse();
        r.setId(id);
        r.setTipo("ACTIVIDAD");
        r.setEstado(estado.name());
        r.setFechaInicio(fechaInicio);
        r.setFechaFin(fechaFin);
        r.setDesviacionMinutos(desviacionMinutos);
        return r;
    }

    public static AccionMantenimientoResponse deMantenimiento(
            Long id,
            EstadoMantenimiento estado,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        AccionMantenimientoResponse r = new AccionMantenimientoResponse();
        r.setId(id);
        r.setTipo("MANTENIMIENTO");
        r.setEstado(estado.name());
        r.setFechaInicio(fechaInicio);
        r.setFechaFin(fechaFin);
        return r;
    }

    // ── Getters y Setters ─────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }

    public Long getDesviacionMinutos() { return desviacionMinutos; }
    public void setDesviacionMinutos(Long desviacionMinutos) { this.desviacionMinutos = desviacionMinutos; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
