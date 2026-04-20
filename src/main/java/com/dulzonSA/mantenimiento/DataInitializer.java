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
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

/**
 * Siembra los datos base al arrancar la aplicación.
 * Solo inserta si no existen (idempotente).
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RolRepository    rolRepository;
    private final TurnoRepository  turnoRepository;
    private final MaquinaRepository maquinaRepository;

    @Override
    public void run(String... args) {
        seedRoles();
        seedTurnos();
        seedMaquinas();
    }

    private void seedRoles() {
        for (TipoRol tipo : TipoRol.values()) {
            if (rolRepository.findByNombre(tipo).isEmpty()) {
                rolRepository.save(Rol.builder()
                        .nombre(tipo)
                        .descripcion(tipo.name())
                        .build());
            }
        }
    }

    private void seedTurnos() {
        List<Object[]> turnos = List.of(
            new Object[]{ TipoTurno.MAÑANA, LocalTime.of(6, 0),  LocalTime.of(14, 0) },
            new Object[]{ TipoTurno.TARDE,  LocalTime.of(14, 0), LocalTime.of(22, 0) },
            new Object[]{ TipoTurno.NOCHE,  LocalTime.of(22, 0), LocalTime.of(6, 0)  }
        );
        for (Object[] t : turnos) {
            if (turnoRepository.findByNombre((TipoTurno) t[0]).isEmpty()) {
                turnoRepository.save(Turno.builder()
                        .nombre((TipoTurno) t[0])
                        .horaInicio((LocalTime) t[1])
                        .horaFin((LocalTime) t[2])
                        .build());
            }
        }
    }

    private void seedMaquinas() {
        List<Object[]> maquinas = List.of(
            new Object[]{ "Deshuesadora 1",          TipoMaquina.DESHUESADORA,        "DH-001" },
            new Object[]{ "Deshuesadora 2",          TipoMaquina.DESHUESADORA,        "DH-002" },
            new Object[]{ "Prensa Hidráulica 1",     TipoMaquina.PRENSA,              "PR-001" },
            new Object[]{ "Marmita de Cocción 1",    TipoMaquina.MARMITA,             "MA-001" },
            new Object[]{ "Marmita de Cocción 2",    TipoMaquina.MARMITA,             "MA-002" },
            new Object[]{ "Bomba Centrífuga 1",      TipoMaquina.BOMBA,               "BO-001" },
            new Object[]{ "Mesa Enfriar/Envasar 1",  TipoMaquina.MESA_ENFRIAR_ENVASAR,"ME-001" },
            new Object[]{ "Extractor de Cocción 1",  TipoMaquina.EXTRACTOR_COCCION,   "EC-001" }
        );
        for (Object[] m : maquinas) {
            if (maquinaRepository.findByCodigoInterno((String) m[2]).isEmpty()) {
                maquinaRepository.save(Maquina.builder()
                        .nombre((String) m[0])
                        .tipo((TipoMaquina) m[1])
                        .codigoInterno((String) m[2])
                        .build());
            }
        }
    }
}
