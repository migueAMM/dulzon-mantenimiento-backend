package com.dulzonSA.mantenimiento.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
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

    @Data
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
    }
}
