package com.dulzonSA.mantenimiento.dto.response;

import java.time.LocalDateTime;

public class ObservacionResponse {

    private Long id;
    private String texto;
    private String supervisorNombre;
    private LocalDateTime fechaHora;

    public ObservacionResponse() {}

    public ObservacionResponse(Long id, String texto, String supervisorNombre, LocalDateTime fechaHora) {
        this.id = id;
        this.texto = texto;
        this.supervisorNombre = supervisorNombre;
        this.fechaHora = fechaHora;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public String getSupervisorNombre() { return supervisorNombre; }
    public void setSupervisorNombre(String supervisorNombre) { this.supervisorNombre = supervisorNombre; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
}
