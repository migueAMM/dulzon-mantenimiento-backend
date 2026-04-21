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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MantenimientoService {

    @Autowired private CartaGanttRepository cartaGanttRepository;
    @Autowired private ActividadMantenimientoRepository actividadRepository;
    @Autowired private ObservacionRepository observacionRepository;
    @Autowired private MaquinaRepository maquinaRepository;
    @Autowired private TurnoRepository turnoRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    // ── OPERADOR: programar ─────────────────────────────────────

    @Transactional
    public CartaGantt programarMantenimiento(Long operadorId,
                                             ProgramarMantenimientoRequest request) {

        Usuario operador = usuarioRepository.findById(operadorId)
                .orElseThrow(() -> new RuntimeException("Operador no encontrado: " + operadorId));

        Maquina maquina = maquinaRepository.findById(request.getMaquinaId())
                .orElseThrow(() -> new RuntimeException("Máquina no encontrada: " + request.getMaquinaId()));

        Turno turno = turnoRepository.findById(request.getTurnoId())
                .orElseThrow(() -> new RuntimeException("Turno no encontrado: " + request.getTurnoId()));

        CartaGantt carta = new CartaGantt();
        carta.setMaquina(maquina);
        carta.setOperador(operador);
        carta.setTurno(turno);
        carta.setFechaProgramada(request.getFechaProgramada());
        carta.setEstado(EstadoMantenimiento.PROGRAMADO);

        List<ActividadMantenimiento> actividades = new ArrayList<>();
        for (ProgramarMantenimientoRequest.ActividadRequest ar : request.getActividades()) {
            ActividadMantenimiento act = new ActividadMantenimiento();
            act.setCartaGantt(carta);
            act.setDescripcion(ar.getDescripcion());
            act.setOrden(ar.getOrden());
            act.setDuracionEstimadaMinutos(ar.getDuracionEstimadaMinutos());
            act.setEstado(EstadoActividad.PENDIENTE);
            actividades.add(act);
        }
        carta.setActividades(actividades);

        return cartaGanttRepository.save(carta);
    }

    // ── SUPERVISOR: iniciar mantención ─────────────────────────

    @Transactional
    public CartaGantt iniciarMantenimiento(Long cartaGanttId) {
        CartaGantt carta = obtenerCartaOException(cartaGanttId);

        if (carta.getEstado() != EstadoMantenimiento.PROGRAMADO) {
            throw new RuntimeException(
                    "La mantención no está PROGRAMADA (estado actual: " + carta.getEstado() + ")");
        }

        carta.setEstado(EstadoMantenimiento.EN_PROCESO);
        carta.setFechaInicioReal(LocalDateTime.now());
        return cartaGanttRepository.save(carta);
    }

    // ── SUPERVISOR: iniciar actividad ──────────────────────────

    @Transactional
    public ActividadMantenimiento iniciarActividad(Long actividadId) {
        ActividadMantenimiento actividad = obtenerActividadOException(actividadId);

        if (actividad.getCartaGantt().getEstado() != EstadoMantenimiento.EN_PROCESO) {
            throw new RuntimeException("El proceso de mantenimiento no ha sido iniciado aún");
        }
        if (actividad.getEstado() != EstadoActividad.PENDIENTE) {
            throw new RuntimeException(
                    "La actividad no está PENDIENTE (estado: " + actividad.getEstado() + ")");
        }

        actividad.setEstado(EstadoActividad.EN_PROCESO);
        actividad.setFechaInicioReal(LocalDateTime.now());
        return actividadRepository.save(actividad);
    }

    // ── SUPERVISOR: cerrar actividad ───────────────────────────

    @Transactional
    public ActividadMantenimiento cerrarActividad(Long actividadId) {
        ActividadMantenimiento actividad = obtenerActividadOException(actividadId);

        if (actividad.getEstado() != EstadoActividad.EN_PROCESO) {
            throw new RuntimeException(
                    "La actividad no está EN_PROCESO (estado: " + actividad.getEstado() + ")");
        }

        actividad.setEstado(EstadoActividad.COMPLETADA);
        actividad.setFechaFinReal(LocalDateTime.now());
        return actividadRepository.save(actividad);
    }

    // ── SUPERVISOR: observación en actividad ───────────────────

    @Transactional
    public Observacion registrarObservacionEnActividad(Long actividadId,
                                                       Long supervisorId,
                                                       ObservacionRequest request) {
        ActividadMantenimiento actividad = obtenerActividadOException(actividadId);
        Usuario supervisor = obtenerUsuarioOException(supervisorId);
        validarTextoObservacion(request.getTexto());

        Observacion obs = new Observacion();
        obs.setActividad(actividad);
        obs.setSupervisor(supervisor);
        obs.setTexto(request.getTexto());
        obs.setFechaHora(LocalDateTime.now());

        return observacionRepository.save(obs);
    }

    // ── SUPERVISOR: terminar mantención ────────────────────────

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
                validarTextoObservacion(req.getTexto());
                Observacion obs = new Observacion();
                obs.setCartaGantt(carta);
                obs.setSupervisor(supervisor);
                obs.setTexto(req.getTexto());
                obs.setFechaHora(LocalDateTime.now());
                carta.getObservacionesGenerales().add(obs);
            }
        }

        carta.setEstado(EstadoMantenimiento.TERMINADO);
        carta.setFechaFinReal(LocalDateTime.now());
        return cartaGanttRepository.save(carta);
    }

    // ── CONSULTAS ──────────────────────────────────────────────

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

        AvanceMantenimientoResponse resp = new AvanceMantenimientoResponse();
        resp.setCartaGanttId(carta.getId());
        resp.setMaquinaNombre(carta.getMaquina().getNombre());
        resp.setMaquinaTipo(carta.getMaquina().getTipo().name());
        resp.setCodigoInternoMaquina(carta.getMaquina().getCodigoInterno());
        resp.setTurnoNombre(carta.getTurno().getNombre().name());
        resp.setFechaProgramada(carta.getFechaProgramada());
        resp.setOperadorNombre(carta.getOperador().getNombre());
        resp.setFechaInicioReal(carta.getFechaInicioReal());
        resp.setFechaFinReal(carta.getFechaFinReal());
        resp.setEstado(carta.getEstado());
        resp.setTotalActividades(actividades.size());
        resp.setActividadesCompletadas((int) completadas);
        resp.setPorcentajeAvance(porcentaje);
        resp.setDesviacionTotalMinutos(desviacionTotal);
        resp.setActividades(actividades.stream().map(this::mapActividad).collect(Collectors.toList()));
        resp.setObservacionesGenerales(carta.getObservacionesGenerales()
                .stream().map(this::mapObservacion).collect(Collectors.toList()));

        return resp;
    }

    @Transactional(readOnly = true)
    public List<AvanceMantenimientoResponse> listarEnProceso() {
        return cartaGanttRepository.findEnProceso().stream()
                .map(c -> obtenerAvance(c.getId()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AvanceMantenimientoResponse> listarPorEstado(EstadoMantenimiento estado) {
        return cartaGanttRepository.findByEstado(estado).stream()
                .map(c -> obtenerAvance(c.getId()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AvanceMantenimientoResponse> listarProgramadasHoy() {
        return cartaGanttRepository.findProgramadasHoy(LocalDate.now()).stream()
                .map(c -> obtenerAvance(c.getId()))
                .collect(Collectors.toList());
    }

    // ── helpers privados ────────────────────────────────────────

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

    private void validarTextoObservacion(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new RuntimeException("El texto de la observación no puede estar vacío");
        }
    }

    private ActividadResponse mapActividad(ActividadMantenimiento a) {
        ActividadResponse r = new ActividadResponse();
        r.setId(a.getId());
        r.setDescripcion(a.getDescripcion());
        r.setOrden(a.getOrden());
        r.setDuracionEstimadaMinutos(a.getDuracionEstimadaMinutos());
        r.setFechaInicioReal(a.getFechaInicioReal());
        r.setFechaFinReal(a.getFechaFinReal());
        r.setEstado(a.getEstado());
        r.setDesviacionMinutos(a.getDesviacionMinutos());
        r.setObservaciones(a.getObservaciones().stream()
                .map(this::mapObservacion).collect(Collectors.toList()));
        return r;
    }

    private ObservacionResponse mapObservacion(Observacion o) {
        ObservacionResponse r = new ObservacionResponse();
        r.setId(o.getId());
        r.setTexto(o.getTexto());
        r.setSupervisorNombre(o.getSupervisor().getNombre());
        r.setFechaHora(o.getFechaHora());
        return r;
    }
}
