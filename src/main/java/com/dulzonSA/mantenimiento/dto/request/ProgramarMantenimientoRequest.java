package com.dulzonSA.mantenimiento.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public class ProgramarMantenimientoRequest {

    @NotNull(message = "El ID de máquina es requerido")
    private Long maquinaId;

    @NotNull(message = "El ID de turno es requerido")
    private Long turnoId;

    @NotNull(message = "La fecha programada es requerida")
    @Future(message = "La fecha programada debe ser en el futuro")
    private LocalDate fechaProgramada;

    @NotEmpty(message = "Debe incluir al menos una actividad")
    @Valid
    private List<ActividadRequest> actividades;

    // Getters y Setters
    public Long getMaquinaId() { return maquinaId; }
    public void setMaquinaId(Long maquinaId) { this.maquinaId = maquinaId; }

    public Long getTurnoId() { return turnoId; }
    public void setTurnoId(Long turnoId) { this.turnoId = turnoId; }

    public LocalDate getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDate fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public List<ActividadRequest> getActividades() { return actividades; }
    public void setActividades(List<ActividadRequest> actividades) {
        this.actividades = actividades;
    }

    public static class ActividadRequest {

        @NotBlank(message = "La descripción de la actividad es requerida")
        @Size(min = 3, max = 500, message = "La descripción debe tener entre 3 y 500 caracteres")
        private String descripcion;

        @NotNull(message = "El orden de la actividad es requerido")
        @Positive(message = "El orden debe ser un número positivo")
        private Integer orden;

        @NotNull(message = "La duración estimada es requerida")
        @Positive(message = "La duración debe ser mayor a 0 minutos")
        private Integer duracionEstimadaMinutos;

        // Getters y Setters
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public Integer getOrden() { return orden; }
        public void setOrden(Integer orden) { this.orden = orden; }

        public Integer getDuracionEstimadaMinutos() { return duracionEstimadaMinutos; }
        public void setDuracionEstimadaMinutos(Integer duracionEstimadaMinutos) {
            this.duracionEstimadaMinutos = duracionEstimadaMinutos;
        }
    }
}
