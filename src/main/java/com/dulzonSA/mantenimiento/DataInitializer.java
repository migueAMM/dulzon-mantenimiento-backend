package com.dulzonSA.mantenimiento;

import com.dulzonSA.mantenimiento.models.Maquina;
import com.dulzonSA.mantenimiento.models.Rol;
import com.dulzonSA.mantenimiento.models.Turno;
import com.dulzonSA.mantenimiento.models.enums.TipoMaquina;
import com.dulzonSA.mantenimiento.models.enums.TipoRol;
import com.dulzonSA.mantenimiento.models.enums.TipoTurno;
import com.dulzonSA.mantenimiento.repositories.MaquinaRepository;
import com.dulzonSA.mantenimiento.repositories.RolRepository;
import com.dulzonSA.mantenimiento.repositories.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * Siembra datos base al arrancar. Solo inserta si no existen (idempotente).
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private RolRepository    rolRepository;
    @Autowired private TurnoRepository  turnoRepository;
    @Autowired private MaquinaRepository maquinaRepository;

    @Override
    public void run(String... args) {
        seedRoles();
        seedTurnos();
        seedMaquinas();
    }

    private void seedRoles() {
        for (TipoRol tipo : TipoRol.values()) {
            if (rolRepository.findByNombre(tipo).isEmpty()) {
                Rol rol = new Rol();
                rol.setNombre(tipo);
                rol.setDescripcion(tipo.name());
                rolRepository.save(rol);
            }
        }
    }

    private void seedTurnos() {
        Object[][] datos = {
            { TipoTurno.MAÑANA, LocalTime.of(6,  0), LocalTime.of(14, 0) },
            { TipoTurno.TARDE,  LocalTime.of(14, 0), LocalTime.of(22, 0) },
            { TipoTurno.NOCHE,  LocalTime.of(22, 0), LocalTime.of(6,  0) }
        };
        for (Object[] d : datos) {
            if (turnoRepository.findByNombre((TipoTurno) d[0]).isEmpty()) {
                Turno t = new Turno();
                t.setNombre((TipoTurno) d[0]);
                t.setHoraInicio((LocalTime) d[1]);
                t.setHoraFin((LocalTime) d[2]);
                turnoRepository.save(t);
            }
        }
    }

    private void seedMaquinas() {
        Object[][] datos = {
            { "Deshuesadora 1",         TipoMaquina.DESHUESADORA,         "DH-001" },
            { "Deshuesadora 2",         TipoMaquina.DESHUESADORA,         "DH-002" },
            { "Prensa Hidráulica 1",    TipoMaquina.PRENSA,               "PR-001" },
            { "Marmita de Cocción 1",   TipoMaquina.MARMITA,              "MA-001" },
            { "Marmita de Cocción 2",   TipoMaquina.MARMITA,              "MA-002" },
            { "Bomba Centrífuga 1",     TipoMaquina.BOMBA,                "BO-001" },
            { "Mesa Enfriar/Envasar 1", TipoMaquina.MESA_ENFRIAR_ENVASAR, "ME-001" },
            { "Extractor de Cocción 1", TipoMaquina.EXTRACTOR_COCCION,    "EC-001" }
        };
        for (Object[] d : datos) {
            if (maquinaRepository.findByCodigoInterno((String) d[2]).isEmpty()) {
                Maquina m = new Maquina();
                m.setNombre((String) d[0]);
                m.setTipo((TipoMaquina) d[1]);
                m.setCodigoInterno((String) d[2]);
                maquinaRepository.save(m);
            }
        }
    }
}
