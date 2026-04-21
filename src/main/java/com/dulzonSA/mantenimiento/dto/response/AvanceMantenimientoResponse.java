package com.dulzonSA.mantenimiento.dto.response;

import com.dulzonSA.mantenimiento.models.enums.EstadoMantenimiento;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AvanceMantenimientoResponse {

    private Long cartaGanttId;

    // Máquina
    private String maquinaNombre;
    private String maquinaTipo;
    private String codigoInternoMaquina;

    // Planificación
    private String turnoNombre;
    private LocalDate fechaProgramada;
    private String operadorNombre;

    // Ejecución
    private LocalDateTime fechaInicioReal;
    private LocalDateTime fechaFinReal;
    private EstadoMantenimiento estado;

    // Métricas
    private int totalActividades;
    private int actividadesCompletadas;
    private int porcentajeAvance;
    private Long desviacionTotalMinutos;

    private List<ActividadResponse> actividades;
    private List<ObservacionResponse> observacionesGenerales;

    public AvanceMantenimientoResponse() {}

    // Getters y Setters
    public Long getCartaGanttId() { return cartaGanttId; }
    public void setCartaGanttId(Long cartaGanttId) { this.cartaGanttId = cartaGanttId; }

    public String getMaquinaNombre() { return maquinaNombre; }
    public void setMaquinaNombre(String maquinaNombre) { this.maquinaNombre = maquinaNombre; }

    public String getMaquinaTipo() { return maquinaTipo; }
    public void setMaquinaTipo(String maquinaTipo) { this.maquinaTipo = maquinaTipo; }

    public String getCodigoInternoMaquina() { return codigoInternoMaquina; }
    public void setCodigoInternoMaquina(String codigoInternoMaquina) { this.codigoInternoMaquina = codigoInternoMaquina; }

    public String getTurnoNombre() { return turnoNombre; }
    public void setTurnoNombre(String turnoNombre) { this.turnoNombre = turnoNombre; }

    public LocalDate getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDate fechaProgramada) { this.fechaProgramada = fechaProgramada; }

    public String getOperadorNombre() { return operadorNombre; }
    public void setOperadorNombre(String operadorNombre) { this.operadorNombre = operadorNombre; }

    public LocalDateTime getFechaInicioReal() { return fechaInicioReal; }
    public void setFechaInicioReal(LocalDateTime fechaInicioReal) { this.fechaInicioReal = fechaInicioReal; }

    public LocalDateTime getFechaFinReal() { return fechaFinReal; }
    public void setFechaFinReal(LocalDateTime fechaFinReal) { this.fechaFinReal = fechaFinReal; }

    public EstadoMantenimiento getEstado() { return estado; }
    public void setEstado(EstadoMantenimiento estado) { this.estado = estado; }

    public int getTotalActividades() { return totalActividades; }
    public void setTotalActividades(int totalActividades) { this.totalActividades = totalActividades; }

    public int getActividadesCompletadas() { return actividadesCompletadas; }
    public void setActividadesCompletadas(int actividadesCompletadas) { this.actividadesCompletadas = actividadesCompletadas; }

    public int getPorcentajeAvance() { return porcentajeAvance; }
    public void setPorcentajeAvance(int porcentajeAvance) { this.porcentajeAvance = porcentajeAvance; }

    public Long getDesviacionTotalMinutos() { return desviacionTotalMinutos; }
    public void setDesviacionTotalMinutos(Long desviacionTotalMinutos) { this.desviacionTotalMinutos = desviacionTotalMinutos; }

    public List<ActividadResponse> getActividades() { return actividades; }
    public void setActividades(List<ActividadResponse> actividades) { this.actividades = actividades; }

    public List<ObservacionResponse> getObservacionesGenerales() { return observacionesGenerales; }
    public void setObservacionesGenerales(List<ObservacionResponse> observacionesGenerales) { this.observacionesGenerales = observacionesGenerales; }
}
