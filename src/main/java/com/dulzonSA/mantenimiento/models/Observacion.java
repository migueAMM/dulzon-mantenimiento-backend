package com.dulzonSA.mantenimiento.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "observaciones")
public class Observacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actividad_id")
    private ActividadMantenimiento actividad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carta_gantt_id")
    private CartaGantt cartaGantt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", nullable = false)
    private Usuario supervisor;

    @Column(length = 2000, nullable = false)
    private String texto;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    public Observacion() {}

    @PrePersist
    public void prePersist() {
        if (this.fechaHora == null) {
            this.fechaHora = LocalDateTime.now();
        }
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ActividadMantenimiento getActividad() { return actividad; }
    public void setActividad(ActividadMantenimiento actividad) { this.actividad = actividad; }

    public CartaGantt getCartaGantt() { return cartaGantt; }
    public void setCartaGantt(CartaGantt cartaGantt) { this.cartaGantt = cartaGantt; }

    public Usuario getSupervisor() { return supervisor; }
    public void setSupervisor(Usuario supervisor) { this.supervisor = supervisor; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
}
