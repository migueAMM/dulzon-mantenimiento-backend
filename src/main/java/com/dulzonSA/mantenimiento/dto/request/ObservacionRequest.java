package com.dulzonSA.mantenimiento.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ObservacionRequest {

    @NotBlank(message = "El texto de la observación no puede estar vacío")
    @Size(min = 3, max = 2000, message = "La observación debe tener entre 3 y 2000 caracteres")
    private String texto;

    public ObservacionRequest() {}

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
}
