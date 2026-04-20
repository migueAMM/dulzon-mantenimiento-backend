package com.dulzonSA.mantenimiento.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ObservacionRequest {

    @NotBlank(message = "El texto de la observación no puede estar vacío")
    @Size(min = 3, max = 2000, message = "La observación debe tener entre 3 y 2000 caracteres")
    private String texto;
}
