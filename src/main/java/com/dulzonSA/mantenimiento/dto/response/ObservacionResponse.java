package com.dulzonSA.mantenimiento.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ObservacionResponse {
    private Long id;
    private String texto;
    private String supervisorNombre;
    private LocalDateTime fechaHora;
}
