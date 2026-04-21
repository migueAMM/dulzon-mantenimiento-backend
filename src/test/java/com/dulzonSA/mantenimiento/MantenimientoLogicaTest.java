package com.dulzonSA.mantenimiento;

import com.dulzonSA.mantenimiento.dto.request.ProgramarMantenimientoRequest;
import com.dulzonSA.mantenimiento.dto.request.ObservacionRequest;
import com.dulzonSA.mantenimiento.dto.response.AvanceMantenimientoResponse;
import com.dulzonSA.mantenimiento.models.*;
import com.dulzonSA.mantenimiento.models.enums.*;
import com.dulzonSA.mantenimiento.repositories.*;
import com.dulzonSA.mantenimiento.services.MantenimientoService;
import com.dulzonSA.mantenimiento.services.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MantenimientoLogicaTest {

    @Autowired private MantenimientoService mantenimientoService;
    @Autowired private UsuarioService usuarioService;
    @Autowired private RolRepository rolRepository;
    @Autowired private TurnoRepository turnoRepository;
    @Autowired private MaquinaRepository maquinaRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    private Long operadorId;
    private Long supervisorId;
    private Long maquinaId;
    private Long turnoId;

    @BeforeEach
    void setUp() {
        // Crear roles
        Rol rolOp = new Rol(); rolOp.setNombre(TipoRol.OPERADOR); rolOp.setDescripcion("Op");
        Rol rolSup = new Rol(); rolSup.setNombre(TipoRol.SUPERVISOR); rolSup.setDescripcion("Sup");
        rolRepository.save(rolOp);
        rolRepository.save(rolSup);

        // Crear usuarios
        operadorId  = usuarioService.crearUsuario("Pedro Op", "pedro@test.com", "pass123", TipoRol.OPERADOR).getId();
        supervisorId = usuarioService.crearUsuario("Maria Sup", "maria@test.com", "pass123", TipoRol.SUPERVISOR).getId();

        // Crear máquina
        Maquina maq = new Maquina();
        maq.setNombre("Deshuesadora 1");
        maq.setTipo(TipoMaquina.DESHUESADORA);
        maq.setCodigoInterno("DH-TEST-001");
        maquinaId = maquinaRepository.save(maq).getId();

        // Crear turno
        Turno t = new Turno();
        t.setNombre(TipoTurno.MAÑANA);
        turnoId = turnoRepository.save(t).getId();
    }

    // ── TEST 1: Flujo completo ──────────────────────────────────

    @Test
    void flujoCompleto_programar_iniciar_actividades_terminar() {
        // 1. Programar
        ProgramarMantenimientoRequest req = buildRequest();
        CartaGantt carta = mantenimientoService.programarMantenimiento(operadorId, req);

        assertNotNull(carta.getId());
        assertEquals(EstadoMantenimiento.PROGRAMADO, carta.getEstado());
        assertEquals(2, carta.getActividades().size());

        // 2. Iniciar mantención
        carta = mantenimientoService.iniciarMantenimiento(carta.getId());
        assertEquals(EstadoMantenimiento.EN_PROCESO, carta.getEstado());
        assertNotNull(carta.getFechaInicioReal());

        // 3. Iniciar actividad 1
        Long act1Id = carta.getActividades().get(0).getId();
        Long act2Id = carta.getActividades().get(1).getId();

        ActividadMantenimiento a1 = mantenimientoService.iniciarActividad(act1Id);
        assertEquals(EstadoActividad.EN_PROCESO, a1.getEstado());
        assertNotNull(a1.getFechaInicioReal());

        // 4. Cerrar actividad 1
        a1 = mantenimientoService.cerrarActividad(act1Id);
        assertEquals(EstadoActividad.COMPLETADA, a1.getEstado());
        assertNotNull(a1.getFechaFinReal());

        // 5. Iniciar y cerrar actividad 2
        mantenimientoService.iniciarActividad(act2Id);
        mantenimientoService.cerrarActividad(act2Id);

        // 6. Registrar observación de cierre
        ObservacionRequest obs = new ObservacionRequest();
        obs.setTexto("Mantenimiento finalizado sin novedad");

        // 7. Terminar mantención
        carta = mantenimientoService.terminarMantenimiento(carta.getId(), supervisorId, List.of(obs));
        assertEquals(EstadoMantenimiento.TERMINADO, carta.getEstado());
        assertNotNull(carta.getFechaFinReal());
    }

    // ── TEST 2: Avance y porcentaje ────────────────────────────

    @Test
    void avance_porcentaje_correcto() {
        CartaGantt carta = mantenimientoService.programarMantenimiento(operadorId, buildRequest());
        mantenimientoService.iniciarMantenimiento(carta.getId());

        Long act1Id = carta.getActividades().get(0).getId();
        mantenimientoService.iniciarActividad(act1Id);
        mantenimientoService.cerrarActividad(act1Id);

        AvanceMantenimientoResponse avance = mantenimientoService.obtenerAvance(carta.getId());
        assertEquals(2, avance.getTotalActividades());
        assertEquals(1, avance.getActividadesCompletadas());
        assertEquals(50, avance.getPorcentajeAvance());
        assertEquals(EstadoMantenimiento.EN_PROCESO, avance.getEstado());
    }

    // ── TEST 3: No se puede iniciar si ya está EN_PROCESO ──────

    @Test
    void iniciarMantenimiento_yaEnProceso_lanzaError() {
        CartaGantt carta = mantenimientoService.programarMantenimiento(operadorId, buildRequest());
        mantenimientoService.iniciarMantenimiento(carta.getId());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> mantenimientoService.iniciarMantenimiento(carta.getId()));
        assertTrue(ex.getMessage().contains("no está PROGRAMADA"));
    }

    // ── TEST 4: No terminar con actividad abierta ──────────────

    @Test
    void terminarMantenimiento_conActividadAbierta_lanzaError() {
        CartaGantt carta = mantenimientoService.programarMantenimiento(operadorId, buildRequest());
        mantenimientoService.iniciarMantenimiento(carta.getId());
        mantenimientoService.iniciarActividad(carta.getActividades().get(0).getId());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> mantenimientoService.terminarMantenimiento(carta.getId(), supervisorId, null));
        assertTrue(ex.getMessage().contains("actividades aún en proceso"));
    }

    // ── TEST 5: Login correcto e incorrecto ────────────────────

    @Test
    void login_credencialesCorrectas_retornaUsuario() {
        Usuario u = usuarioService.login("pedro@test.com", "pass123");
        assertEquals("Pedro Op", u.getNombre());
        assertEquals(TipoRol.OPERADOR, u.getRol().getNombre());
    }

    @Test
    void login_passwordIncorrecta_lanzaError() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.login("pedro@test.com", "wrongpass"));
        assertEquals("Credenciales inválidas", ex.getMessage());
    }

    @Test
    void login_emailNoExiste_lanzaError() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.login("noexiste@test.com", "pass123"));
        assertEquals("Credenciales inválidas", ex.getMessage());
    }

    // ── TEST 6: Email duplicado ────────────────────────────────

    @Test
    void registro_emailDuplicado_lanzaError() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.crearUsuario("Otro Pedro", "pedro@test.com", "pass123", TipoRol.OPERADOR));
        assertTrue(ex.getMessage().contains("Ya existe un usuario"));
    }

    // ── TEST 7: Observación en actividad ──────────────────────

    @Test
    void observacion_enActividad_seGuarda() {
        CartaGantt carta = mantenimientoService.programarMantenimiento(operadorId, buildRequest());
        mantenimientoService.iniciarMantenimiento(carta.getId());

        Long actId = carta.getActividades().get(0).getId();
        mantenimientoService.iniciarActividad(actId);

        ObservacionRequest obs = new ObservacionRequest();
        obs.setTexto("Pieza desgastada encontrada");

        Observacion result = mantenimientoService.registrarObservacionEnActividad(actId, supervisorId, obs);
        assertNotNull(result.getId());
        assertEquals("Pieza desgastada encontrada", result.getTexto());
        assertNotNull(result.getFechaHora());
    }

    // ── TEST 8: Listar por estado ──────────────────────────────

    @Test
    void listar_porEstadoProgramado_retornaLista() {
        mantenimientoService.programarMantenimiento(operadorId, buildRequest());
        mantenimientoService.programarMantenimiento(operadorId, buildRequest());

        List<AvanceMantenimientoResponse> lista = mantenimientoService.listarPorEstado(EstadoMantenimiento.PROGRAMADO);
        assertEquals(2, lista.size());
        lista.forEach(a -> assertEquals(EstadoMantenimiento.PROGRAMADO, a.getEstado()));
    }

    // ── Helper ─────────────────────────────────────────────────

    private ProgramarMantenimientoRequest buildRequest() {
        ProgramarMantenimientoRequest req = new ProgramarMantenimientoRequest();
        req.setMaquinaId(maquinaId);
        req.setTurnoId(turnoId);
        req.setFechaProgramada(LocalDate.now().plusDays(5));

        ProgramarMantenimientoRequest.ActividadRequest a1 = new ProgramarMantenimientoRequest.ActividadRequest();
        a1.setDescripcion("Inspección visual del equipo");
        a1.setOrden(1);
        a1.setDuracionEstimadaMinutos(30);

        ProgramarMantenimientoRequest.ActividadRequest a2 = new ProgramarMantenimientoRequest.ActividadRequest();
        a2.setDescripcion("Limpieza de componentes");
        a2.setOrden(2);
        a2.setDuracionEstimadaMinutos(60);

        req.setActividades(List.of(a1, a2));
        return req;
    }
}
