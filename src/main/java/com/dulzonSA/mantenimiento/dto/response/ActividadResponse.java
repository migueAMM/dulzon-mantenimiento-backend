package com.dulzonSA.mantenimiento.dto.response;

import com.dulzonSA.mantenimiento.models.enums.EstadoActividad;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
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
}
