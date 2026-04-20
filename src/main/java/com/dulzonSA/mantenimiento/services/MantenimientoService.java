package com.dulzonSA.mantenimiento.services;

import com.dulzonSA.mantenimiento.dto.request.ObservacionRequest;
import com.dulzonSA.mantenimiento.dto.request.ProgramarMantenimientoRequest;
import com.dulzonSA.mantenimiento.dto.response.ActividadResponse;
import com.dulzonSA.mantenimiento.dto.response.AvanceMantenimientoResponse;
import com.dulzonSA.mantenimiento.dto.response.ObservacionResponse;
import com.dulzonSA.mantenimiento.models.*;
import com.dulzonSA.mantenimiento.models.enums.EstadoActividad;
import com.dulzonSA.mantenimiento.models.enums.EstadoMantenimiento;
import com.dulzonSA.mantenimiento.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MantenimientoService {

    private final CartaGanttRepository cartaGanttRepository;
    private final ActividadMantenimientoRepository actividadRepository;
    private final ObservacionRepository observacionRepository;
    private final MaquinaRepository maquinaRepository;
    private final TurnoRepository turnoRepository;
    private final UsuarioRepository usuarioRepository;

    // ─────────────────────────────────────────────────────────────
    // OPERADOR: Programar una nueva mantención (ingresa la carta gantt)
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public CartaGantt programarMantenimiento(Long operadorId,
                                             ProgramarMantenimientoRequest request) {

        Usuario operador = usuarioRepository.findById(operadorId)
                .orElseThrow(() -> new RuntimeException("Operador no encontrado: " + operadorId));

        Maquina maquina = maquinaRepository.findById(request.getMaquinaId())
                .orElseThrow(() -> new RuntimeException("Máquina no encontrada: " + request.getMaquinaId()));

        Turno turno = turnoRepository.findById(request.getTurnoId())
                .orElseThrow(() -> new RuntimeException("Turno no encontrado: " + request.getTurnoId()));

        CartaGantt carta = CartaGantt.builder()
                .maquina(maquina)
                .operador(operador)
                .turno(turno)
                .fechaProgramada(request.getFechaProgramada())
                .estado(EstadoMantenimiento.PROGRAMADO)
                .build();

        List<ActividadMantenimiento> actividades = request.getActividades().stream()
                .map(a -> ActividadMantenimiento.builder()
                        .cartaGantt(carta)
                        .descripcion(a.getDescripcion())
                        .orden(a.getOrden())
                        .duracionEstimadaMinutos(a.getDuracionEstimadaMinutos())
                        .estado(EstadoActividad.PENDIENTE)
                        .build())
                .collect(Collectors.toList());

        carta.setActividades(actividades);
        return cartaGanttRepository.save(carta);
    }

    // ─────────────────────────────────────────────────────────────
    // SUPERVISOR: La cuadrilla llegó → dar inicio al proceso
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public CartaGantt iniciarMantenimiento(Long cartaGanttId) {
        CartaGantt carta = obtenerCartaOException(cartaGanttId);

        if (carta.getEstado() != EstadoMantenimiento.PROGRAMADO) {
            throw new RuntimeException("La mantención no está PROGRAMADA (estado actual: "
                    + carta.getEstado() + ")");
        }

        carta.setEstado(EstadoMantenimiento.EN_PROCESO);
        carta.setFechaInicioReal(LocalDateTime.now());
        return cartaGanttRepository.save(carta);
    }

    // ─────────────────────────────────────────────────────────────
    // SUPERVISOR: Pulsa "Iniciar actividad" en el móvil
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public ActividadMantenimiento iniciarActividad(Long actividadId) {
        ActividadMantenimiento actividad = obtenerActividadOException(actividadId);

        if (actividad.getCartaGantt().getEstado() != EstadoMantenimiento.EN_PROCESO) {
            throw new RuntimeException("El proceso de mantenimiento no ha sido iniciado aún");
        }

        if (actividad.getEstado() != EstadoActividad.PENDIENTE) {
            throw new RuntimeException("La actividad no está PENDIENTE (estado: "
                    + actividad.getEstado() + ")");
        }

        actividad.setEstado(EstadoActividad.EN_PROCESO);
        actividad.setFechaInicioReal(LocalDateTime.now());
        return actividadRepository.save(actividad);
    }

    // ─────────────────────────────────────────────────────────────
    // SUPERVISOR: Pulsa "Cerrar actividad" en el móvil
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public ActividadMantenimiento cerrarActividad(Long actividadId) {
        ActividadMantenimiento actividad = obtenerActividadOException(actividadId);

        if (actividad.getEstado() != EstadoActividad.EN_PROCESO) {
            throw new RuntimeException("La actividad no está EN_PROCESO (estado: "
                    + actividad.getEstado() + ")");
        }

        actividad.setEstado(EstadoActividad.COMPLETADA);
        actividad.setFechaFinReal(LocalDateTime.now());
        return actividadRepository.save(actividad);
    }

    // ─────────────────────────────────────────────────────────────
    // SUPERVISOR: Registrar observación sobre una actividad
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public Observacion registrarObservacionEnActividad(Long actividadId,
                                                       Long supervisorId,
                                                       ObservacionRequest request) {
        ActividadMantenimiento actividad = obtenerActividadOException(actividadId);
        Usuario supervisor = obtenerUsuarioOException(supervisorId);
        validarObservacion(request);

        Observacion obs = Observacion.builder()
                .actividad(actividad)
                .supervisor(supervisor)
                .texto(request.getTexto())
                .fechaHora(LocalDateTime.now())
                .build();

        return observacionRepository.save(obs);
    }

    // ─────────────────────────────────────────────────────────────
    // SUPERVISOR: Cerrar el proceso completo + observaciones finales
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public CartaGantt terminarMantenimiento(Long cartaGanttId,
                                            Long supervisorId,
                                            List<ObservacionRequest> observacionesCierre) {
        CartaGantt carta = obtenerCartaOException(cartaGanttId);
        Usuario supervisor = obtenerUsuarioOException(supervisorId);

        if (carta.getEstado() != EstadoMantenimiento.EN_PROCESO) {
            throw new RuntimeException("La mantención no está EN_PROCESO");
        }

        boolean hayActividadAbierta = carta.getActividades().stream()
                .anyMatch(a -> a.getEstado() == EstadoActividad.EN_PROCESO);
        if (hayActividadAbierta) {
            throw new RuntimeException(
                    "Existen actividades aún en proceso. Ciérrelas antes de terminar la mantención");
        }

        if (observacionesCierre != null && !observacionesCierre.isEmpty()) {
            for (ObservacionRequest req : observacionesCierre) {
                validarObservacion(req);
                Observacion obs = Observacion.builder()
                        .cartaGantt(carta)
                        .supervisor(supervisor)
                        .texto(req.getTexto())
                        .fechaHora(LocalDateTime.now())
                        .build();
                carta.getObservacionesGenerales().add(obs);
            }
        }

        carta.setEstado(EstadoMantenimiento.TERMINADO);
        carta.setFechaFinReal(LocalDateTime.now());
        return cartaGanttRepository.save(carta);
    }

    // ─────────────────────────────────────────────────────────────
    // TODOS: Vista de avance (planificado vs real)
    // ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public AvanceMantenimientoResponse obtenerAvance(Long cartaGanttId) {
        CartaGantt carta = obtenerCartaOException(cartaGanttId);
        List<ActividadMantenimiento> actividades = carta.getActividades();

        long completadas = actividades.stream()
                .filter(a -> a.getEstado() == EstadoActividad.COMPLETADA)
                .count();

        int porcentaje = actividades.isEmpty() ? 0
                : (int) Math.round((completadas * 100.0) / actividades.size());

        Long desviacionTotal = actividades.stream()
                .filter(a -> a.getDesviacionMinutos() != null)
                .mapToLong(ActividadMantenimiento::getDesviacionMinutos)
                .sum();

        return AvanceMantenimientoResponse.builder()
                .cartaGanttId(carta.getId())
                .maquinaNombre(carta.getMaquina().getNombre())
                .maquinaTipo(carta.getMaquina().getTipo().name())
                .codigoInternoMaquina(carta.getMaquina().getCodigoInterno())
                .turnoNombre(carta.getTurno().getNombre().name())
                .fechaProgramada(carta.getFechaProgramada())
                .operadorNombre(carta.getOperador().getNombre())
                .fechaInicioReal(carta.getFechaInicioReal())
                .fechaFinReal(carta.getFechaFinReal())
                .estado(carta.getEstado())
                .totalActividades(actividades.size())
                .actividadesCompletadas((int) completadas)
                .porcentajeAvance(porcentaje)
                .desviacionTotalMinutos(desviacionTotal)
                .actividades(actividades.stream().map(this::mapActividad).collect(Collectors.toList()))
                .observacionesGenerales(carta.getObservacionesGenerales().stream()
                        .map(this::mapObservacion).collect(Collectors.toList()))
                .build();
    }

    @Transactional(readOnly = true)
    public List<AvanceMantenimientoResponse> listarEnProceso() {
        return cartaGanttRepository.findEnProceso().stream()
                .map(c -> obtenerAvance(c.getId())).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AvanceMantenimientoResponse> listarPorEstado(EstadoMantenimiento estado) {
        return cartaGanttRepository.findByEstado(estado).stream()
                .map(c -> obtenerAvance(c.getId())).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AvanceMantenimientoResponse> listarProgramadasHoy() {
        return cartaGanttRepository.findProgramadasHoy(LocalDate.now()).stream()
                .map(c -> obtenerAvance(c.getId())).collect(Collectors.toList());
    }

    // ─── helpers privados ────────────────────────────────────────

    private CartaGantt obtenerCartaOException(Long id) {
        return cartaGanttRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carta Gantt no encontrada: " + id));
    }

    private ActividadMantenimiento obtenerActividadOException(Long id) {
        return actividadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actividad no encontrada: " + id));
    }

    private Usuario obtenerUsuarioOException(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
    }

    private void validarObservacion(ObservacionRequest req) {
        if (req.getTexto() == null || req.getTexto().isBlank()) {
            throw new RuntimeException("La observación no puede estar vacía");
        }
    }

    private ActividadResponse mapActividad(ActividadMantenimiento a) {
        return ActividadResponse.builder()
                .id(a.getId())
                .descripcion(a.getDescripcion())
                .orden(a.getOrden())
                .duracionEstimadaMinutos(a.getDuracionEstimadaMinutos())
                .fechaInicioReal(a.getFechaInicioReal())
                .fechaFinReal(a.getFechaFinReal())
                .estado(a.getEstado())
                .desviacionMinutos(a.getDesviacionMinutos())
                .observaciones(a.getObservaciones().stream()
                        .map(this::mapObservacion).collect(Collectors.toList()))
                .build();
    }

    private ObservacionResponse mapObservacion(Observacion o) {
        return ObservacionResponse.builder()
                .id(o.getId())
                .texto(o.getTexto())
                .supervisorNombre(o.getSupervisor().getNombre())
                .fechaHora(o.getFechaHora())
                .build();
    }
}
