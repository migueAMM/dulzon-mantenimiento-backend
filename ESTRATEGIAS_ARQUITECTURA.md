# 🏗️ ESTRATEGIAS DE ARQUITECTURA IMPLEMENTADAS

## Resumen Ejecutivo

Tu backend implementa **6 estrategias arquitectónicas principales** combinadas para crear una aplicación robusta, escalable y mantenible. A continuación detallo cada una.

---

## 1️⃣ ARQUITECTURA EN CAPAS (Layered Architecture)

### ¿Qué es?
Separación del código en capas horizontales donde cada capa tiene una responsabilidad específica.

### Dónde fue usada?
```
src/main/java/com/dulzonSA/mantenimiento/
├── controllers/        ← CAPA DE PRESENTACIÓN (API REST)
├── services/           ← CAPA DE LÓGICA DE NEGOCIO
├── repositories/       ← CAPA DE ACCESO A DATOS
├── models/             ← CAPA DE MODELOS (Entidades)
├── dto/                ← CAPA DE TRANSFERENCIA DE DATOS
├── exception/          ← CAPA DE MANEJO DE ERRORES
└── config/             ← CAPA DE CONFIGURACIÓN
```

### De qué forma la usamos?

```
Frontend HTTP Request
    ↓
┌─────────────────────────────────────┐
│ CAPA DE PRESENTACIÓN (Controllers)  │  ← Recibe requests
│ - AuthController                    │  ← Valida entrada
│ - MantenimientoController           │
│ - ReporteController                 │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│ CAPA DE LÓGICA (Services)           │  ← Procesa
│ - UsuarioService                    │  ← Orquesta
│ - MantenimientoService              │  ← Valida reglas negocio
│ - PdfService                        │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│ CAPA DE PERSISTENCIA (Repositories) │  ← Accede a BD
│ - UsuarioRepository                 │  ← Spring Data JPA
│ - CartaGanttRepository              │  ← Queries
│ - etc...                            │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│ Base de Datos (MariaDB)             │  ← Almacena
└─────────────────────────────────────┘
```

### Para qué sirve?
- **Separación de responsabilidades**: Cada capa hace una cosa
- **Escalabilidad**: Fácil agregar nuevas funciones
- **Testabilidad**: Cada capa se puede testear independientemente
- **Mantenibilidad**: Cambios en una capa no afectan otras

### Ejemplo de Flujo Completo

```java
// CAPA 1: Presentación (AuthController.java)
@PostMapping("/login")
public ResponseEntity<Usuario> login(@Valid @RequestBody LoginRequest request) {
    // Recibe y valida input
    return ResponseEntity.ok(usuarioService.login(request.getEmail(), request.getPassword()));
}

// CAPA 2: Lógica (UsuarioService.java)
@Transactional(readOnly = true)
public Usuario login(String email, String password) {
    Usuario usuario = usuarioRepository.findByEmail(email)  // Llama a capa 3
            .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
    
    if (!passwordEncoder.matches(password, usuario.getPassword())) {
        throw new RuntimeException("Credenciales inválidas");
    }
    return usuario;
}

// CAPA 3: Persistencia (UsuarioRepository.java)
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);  // Query a BD
}

// CAPA 4: Modelos (Usuario.java)
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // ... propiedades
}
```

---

## 2️⃣ PATRÓN REPOSITORY (Data Access Object - DAO)

### ¿Qué es?
Abstracción que encapsula toda la lógica de acceso a datos. Protege el resto de la aplicación de cambios en la BD.

### Dónde fue usada?
```
src/main/java/com/dulzonSA/mantenimiento/repositories/
├── UsuarioRepository.java
├── RolRepository.java
├── MaquinaRepository.java
├── TurnoRepository.java
├── CartaGanttRepository.java          ← Con queries custom
├── ActividadMantenimientoRepository.java
├── ObservacionRepository.java
└── InventarioRepository.java
```

### De qué forma la usamos?

```java
// CartaGanttRepository.java
public interface CartaGanttRepository extends JpaRepository<CartaGantt, Long> {
    
    // Spring Data genera automáticamente
    List<CartaGantt> findByEstado(EstadoMantenimiento estado);
    
    // Custom query (JPQL)
    @Query("SELECT c FROM CartaGantt c WHERE c.estado = 'EN_PROCESO'")
    List<CartaGantt> findEnProceso();
    
    // Custom query con parámetro
    @Query("SELECT c FROM CartaGantt c WHERE c.fechaProgramada = :fecha")
    List<CartaGantt> findProgramadasHoy(@Param("fecha") LocalDate fecha);
}
```

### Cómo se usa en Servicios:

```java
// MantenimientoService.java - Inyección del Repository
@RequiredArgsConstructor
public class MantenimientoService {
    private final CartaGanttRepository cartaGanttRepository;  // ← Inyectado
    
    public CartaGantt iniciarMantenimiento(Long cartaGanttId) {
        CartaGantt carta = cartaGanttRepository.findById(cartaGanttId)
                .orElseThrow(() -> new RuntimeException("No encontrada"));
        // ... lógica
        return cartaGanttRepository.save(carta);  // ← Persiste
    }
}
```

### Para qué sirve?
- **Abstracción de BD**: Cambiar de BD no requiere cambiar servicios
- **Menos código**: Spring Data genera queries automáticamente
- **Consistency**: Una forma estándar de acceder a datos
- **Testing**: Fácil mockear para tests

---

## 3️⃣ PATRÓN SERVICE LOCATOR / INYECCIÓN DE DEPENDENCIAS

### ¿Qué es?
Spring inyecta las dependencias automáticamente. No instanciamos manualmente.

### Dónde fue usada?
```
@RequiredArgsConstructor  ← Generado por Lombok
public class MantenimientoController {
    private final MantenimientoService mantenimientoService;  ← Inyectado
    private final UsuarioService usuarioService;              ← Inyectado
    
    // Spring pasa automáticamente estas instancias
}

@RequiredArgsConstructor
public class MantenimientoService {
    private final CartaGanttRepository cartaGanttRepository;      ← Inyectado
    private final ActividadMantenimientoRepository actividadRepository;  ← Inyectado
    private final UsuarioRepository usuarioRepository;            ← Inyectado
}
```

### De qué forma la usamos?

```java
// ❌ SIN INYECCIÓN (tradicional, acoplado)
public class MantenimientoController {
    private MantenimientoService service = new MantenimientoService(); // Tight coupling
}

// ✅ CON INYECCIÓN (desacoplado)
public class MantenimientoController {
    private final MantenimientoService service;
    
    public MantenimientoController(MantenimientoService service) {
        this.service = service;  // Spring lo pasa
    }
}

// ✅ CON LOMBOK + SPRING (más limpio)
@RequiredArgsConstructor  // Lombok genera constructor
public class MantenimientoController {
    private final MantenimientoService service;  // Spring lo inyecta
}
```

### Para qué sirve?
- **Desacoplamiento**: No dependes de implementaciones concretas
- **Testabilidad**: Puedes pasar mocks en tests
- **Flexibilidad**: Cambiar implementación sin cambiar código
- **Mantenibilidad**: Menos código boilerplate

---

## 4️⃣ PATRÓN DTO (Data Transfer Object)

### ¿Qué es?
Objetos intermedios que transportan datos entre capas. Protege las entidades.

### Dónde fue usada?
```
src/main/java/com/dulzonSA/mantenimiento/dto/
├── request/
│   ├── LoginRequest.java
│   ├── RegistroRequest.java
│   ├── ProgramarMantenimientoRequest.java
│   └── ObservacionRequest.java
└── response/
    ├── AvanceMantenimientoResponse.java
    ├── ActividadResponse.java
    └── ObservacionResponse.java
```

### De qué forma la usamos?

```java
// DTO Request - Lo que recibe del Frontend
@Data
public class ProgramarMantenimientoRequest {
    @NotNull
    private Long maquinaId;
    @NotNull
    private Long turnoId;
    @NotNull
    @Future
    private LocalDate fechaProgramada;
    
    @NotEmpty
    @Valid
    private List<ActividadRequest> actividades;  // Nested validation
}

// Controller recibe DTO
@PostMapping("/programar")
public ResponseEntity<CartaGantt> programar(
        @RequestParam Long operadorId,
        @Valid @RequestBody ProgramarMantenimientoRequest request) {  // ← DTO
    return ResponseEntity.ok(mantenimientoService.programarMantenimiento(operadorId, request));
}

// Service convierte DTO → Entity
@Transactional
public CartaGantt programarMantenimiento(Long operadorId, ProgramarMantenimientoRequest request) {
    // Extrae datos del DTO
    CartaGantt carta = CartaGantt.builder()
            .maquina(maquinaRepository.findById(request.getMaquinaId()).orElseThrow())
            .operador(usuarioRepository.findById(operadorId).orElseThrow())
            .turno(turnoRepository.findById(request.getTurnoId()).orElseThrow())
            .fechaProgramada(request.getFechaProgramada())  // ← De DTO
            .build();
    
    return cartaGanttRepository.save(carta);
}

// DTO Response - Lo que retorna al Frontend
@Builder
public class AvanceMantenimientoResponse {
    private Long cartaGanttId;
    private String maquinaNombre;
    private String estado;
    private Integer porcentajeAvance;
    private List<ActividadResponse> actividades;
}
```

### Para qué sirve?
- **Validación**: Valida datos antes de procesarlos
- **Seguridad**: No expones IDs internos innecesariamente
- **Contrato API**: Define exactamente qué esperas
- **Flexibilidad**: Cambia estructura API sin tocar entities

---

## 5️⃣ PATRÓN TRANSACCIONAL (ACID)

### ¿Qué es?
Garantiza que operaciones de BD se completen totalmente o no se ejecuten.

### Dónde fue usada?
```
@Transactional             ← READ-WRITE (default)
public CartaGantt programarMantenimiento(...) {
    // Si falla aquí, TODO se revierte
    CartaGantt carta = new CartaGantt(...);
    List<Actividad> actividades = request.getActividades().stream()...;
    carta.setActividades(actividades);
    return cartaGanttRepository.save(carta);  // TODO o NADA
}

@Transactional(readOnly = true)  ← READ-ONLY (optimizado)
public Usuario login(String email, String password) {
    Usuario usuario = usuarioRepository.findByEmail(email)...;
    return usuario;
}
```

### De qué forma la usamos?

**Scenario 1: Programar mantención (múltiples inserts)**
```
Inicio de transacción ─┐
    ↓                  │
Insert CartaGantt     │
    ↓                  │ TRANSACTION
Insert Actividad 1    │
    ↓                  │
Insert Actividad 2    │
    ↓                  │
Commit o Rollback ────┘

Si alguno falla → ROLLBACK (ninguno se guarda)
Si todos OK → COMMIT (todos se guardan)
```

### Para qué sirve?
- **Integridad**: BD siempre en estado consistente
- **Rollback automático**: Si algo falla, revierte todo
- **Isolamiento**: Otros no ven datos parciales
- **Performance**: `readOnly=true` optimiza queries

---

## 6️⃣ PATRÓN SINGLETON + FACTORY (Spring Beans)

### ¿Qué es?
Spring crea UNA sola instancia de cada componente (Controllers, Services, Repositories).

### Dónde fue usada?
```java
// SecurityConfig.java - Factory Bean
@Configuration
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);  // ← Una sola instancia
    }
}

// Spring automáticamente crea Beans de:
// - Controllers (anotados con @RestController)
// - Services (anotados con @Service)
// - Repositories (heredan de JpaRepository)
// - Configurations (anotadas con @Configuration)
```

### De qué forma la usamos?

```java
// Spring crea la instancia UNA sola vez
@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final BCryptPasswordEncoder passwordEncoder;  // ← Inyecta singleton
    
    public Usuario crearUsuario(...) {
        String encriptado = passwordEncoder.encode(password);
        // ... TODA la aplicación usa el MISMO BCryptPasswordEncoder
    }
}

// Todas las inyecciones apuntan al MISMO objeto
@RestController
public class AuthController {
    private final UsuarioService service;  // ← Mismo Bean que en otros Controllers
}

@RestController
public class MantenimientoController {
    private final UsuarioService service;  // ← MISMA instancia
}
```

### Para qué sirve?
- **Eficiencia**: Una sola instancia para toda la aplicación
- **Consistencia**: Todos usan el mismo estado
- **Facilidad**: Spring gestiona el ciclo de vida
- **Testabilidad**: Fácil de mockear

---

## 7️⃣ PATRÓN BUILDER

### ¿Qué es?
Construcción de objetos complejos paso a paso.

### Dónde fue usada?
```java
// Generado por Lombok @Builder
@Entity
@Builder
public class CartaGantt {
    @Id
    private Long id;
    private Maquina maquina;
    private Usuario operador;
    private Turno turno;
    // ...
}

// Se usa en ServiceData
CartaGantt carta = CartaGantt.builder()
        .maquina(maquina)
        .operador(operador)
        .turno(turno)
        .fechaProgramada(request.getFechaProgramada())
        .estado(EstadoMantenimiento.PROGRAMADO)
        .build();  // ← Construye el objeto

// También en respuesta
AvanceMantenimientoResponse response = AvanceMantenimientoResponse.builder()
        .cartaGanttId(carta.getId())
        .maquinaNombre(carta.getMaquina().getNombre())
        .estado(carta.getEstado())
        .porcentajeAvance(porcentaje)
        .actividades(actividades)
        .build();
```

### Para qué sirve?
- **Legibilidad**: Código claro y expresivo
- **Flexibilidad**: Puedes setear solo lo que necesitas
- **Validación**: Setter puede validar
- **Inmutabilidad**: Construyes en una línea

---

## 8️⃣ PATRÓN STRATEGY (Enums con comportamiento)

### ¿Qué es?
Diferentes estrategias encapsuladas en enums.

### Dónde fue usada?
```java
// Enums en models/enums/
public enum EstadoMantenimiento {
    PROGRAMADO,      // Estrategia 1: Aún no inicia
    EN_PROCESO,      // Estrategia 2: En ejecución
    TERMINADO        // Estrategia 3: Finalizado
}

public enum EstadoActividad {
    PENDIENTE,       // Aún no inicia
    EN_PROCESO,      // En ejecución
    COMPLETADA       // Terminada
}

public enum TipoRol {
    ADMIN,           // Acceso total
    OPERADOR,        // Solo programa
    SUPERVISOR       // Solo ejecuta
}
```

### De qué forma la usamos?

```java
// En Service - Valida según estrategia (estado actual)
@Transactional
public ActividadMantenimiento iniciarActividad(Long actividadId) {
    ActividadMantenimiento actividad = actividadRepository.findById(actividadId)
            .orElseThrow();
    
    // STRATEGY: Solo si está PENDIENTE
    if (actividad.getEstado() != EstadoActividad.PENDIENTE) {
        throw new RuntimeException("No está pendiente");
    }
    
    actividad.setEstado(EstadoActividad.EN_PROCESO);  // Cambia estrategia
    return actividadRepository.save(actividad);
}

// En Controlador - Filtra por estrategia
@GetMapping
public ResponseEntity<List<AvanceMantenimientoResponse>> listar(
        @RequestParam(defaultValue = "PROGRAMADO") String estado) {
    
    // STRATEGY: Retorna diferentes resultados según estado
    return ResponseEntity.ok(
            mantenimientoService.listarPorEstado(
                EstadoMantenimiento.valueOf(estado.toUpperCase())));
}
```

### Para qué sirve?
- **Polimorfismo**: Comportamiento diferente por estado
- **Claridad**: Código expresa explícitamente el estado
- **Type-safety**: Compiler previene valores inválidos
- **Validación**: El estado determina qué operaciones son válidas

---

## 9️⃣ PATRÓN OBSERVER / LISTENER

### ¿Qué es?
Cambios automáticos en respuesta a eventos (@PrePersist, etc.).

### Dónde fue usada?
```java
// Observacion.java - Auto-setea timestamp
@Entity
public class Observacion {
    @Column(nullable = false)
    private LocalDateTime fechaHora;
    
    @PrePersist  // ← Listener: antes de insertar
    public void prePersist() {
        if (this.fechaHora == null) {
            this.fechaHora = LocalDateTime.now();  // Auto-setea
        }
    }
}

// CartaGantt.java - Estado por defecto
@Entity
public class CartaGantt {
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoMantenimiento estado = EstadoMantenimiento.PROGRAMADO;
    
    // ← Automáticamente inicia en PROGRAMADO
}
```

### Para qué sirve?
- **Automatización**: No olvidas setear timestamps
- **Consistencia**: Siempre hay un valor por defecto
- **DRY (Don't Repeat Yourself)**: No repites lógica

---

## 🔟 PATRÓN EXCEPTION HANDLING CENTRALIZADO

### ¿Qué es?
Una clase central maneja TODAS las excepciones de la aplicación.

### Dónde fue usada?
```
exception/GlobalExceptionHandler.java
```

### De qué forma la usamos?

```java
// GlobalExceptionHandler.java
@RestControllerAdvice  // ← Intercepta TODAS las excepciones
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Error de Validación");
        body.put("message", ex.getMessage());
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(
            Exception ex, WebRequest request) {
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Error Interno del Servidor");
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

// Controller - Solo lanza excepciones, no maneja
@PostMapping("/login")
public ResponseEntity<Usuario> login(@Valid @RequestBody LoginRequest request) {
    // Si falla validación → Spring lo captura → GlobalExceptionHandler
    return ResponseEntity.ok(usuarioService.login(request.getEmail(), request.getPassword()));
    // Si falla lógica → RuntimeException → GlobalExceptionHandler
}
```

### Para qué sirve?
- **Consistencia**: Todos los errores tienen el mismo formato
- **Mantenibilidad**: Cambias formato en UN lugar
- **Seguridad**: No expones stack traces
- **DRY**: No repites código de manejo de errores

---

## 1️⃣1️⃣ PATRÓN VALIDATION (Jakarta Bean Validation)

### ¿Qué es?
Valida datos de entrada usando anotaciones declarativas.

### Dónde fue usada?
```java
// DTOs con validaciones
@Data
public class ProgramarMantenimientoRequest {
    
    @NotNull(message = "El ID de máquina es requerido")
    private Long maquinaId;
    
    @Future(message = "La fecha debe ser futura")
    @NotNull
    private LocalDate fechaProgramada;
    
    @NotEmpty(message = "Las actividades no pueden estar vacías")
    @Valid  // Valida elementos anidados
    private List<ActividadRequest> actividades;
    
    @Data
    public static class ActividadRequest {
        @NotBlank
        @Size(min = 3, max = 500)
        private String descripcion;
        
        @Positive  // > 0
        private Integer orden;
    }
}

// Controller - Valida automáticamente
@PostMapping("/programar")
public ResponseEntity<CartaGantt> programar(
        @Valid @RequestBody ProgramarMantenimientoRequest request) {  // ← @Valid
    // Si validación falla → Exception → GlobalExceptionHandler
    // Si validación OK → Ejecuta método
    return ResponseEntity.ok(mantenimientoService.programarMantenimiento(...));
}
```

### Para qué sirve?
- **Declarativo**: Validaciones en el DTO, no en el código
- **Reutilizable**: Mismas validaciones en múltiples lugares
- **Automático**: Spring valida antes de llamar al método
- **Claro**: Mensajes descriptivos de error

---

## 1️⃣2️⃣ PATRÓN LAZY LOADING

### ¿Qué es?
Carga datos relacionados solo cuando se acceden (no al inicio).

### Dónde fue usada?
```java
// Todas las relaciones ManyToOne/OneToMany usan LAZY
@Entity
public class CartaGantt {
    
    @ManyToOne(fetch = FetchType.LAZY)  // ← No carga inmediatamente
    @JoinColumn(name = "maquina_id")
    private Maquina maquina;  // Se carga cuando accedes a .getMaquina()
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operador_id")
    private Usuario operador;
    
    @OneToMany(mappedBy = "cartaGantt", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC")
    @Builder.Default
    private List<ActividadMantenimiento> actividades = new ArrayList<>();
    // Actividades se cargan cuando accedes a .getActividades()
}
```

### De qué forma la usamos?

```java
// ❌ SIN LAZY (EAGER) - Ineficiente
SELECT * FROM cartas_gantt;        // Carga CartaGantt
SELECT * FROM maquinas;            // Carga Maquina
SELECT * FROM usuarios;            // Carga Usuario
SELECT * FROM turnos;              // Carga Turno
SELECT * FROM actividades;         // Carga actividades

// ✅ CON LAZY - Eficiente
SELECT * FROM cartas_gantt;  // Solo carga carta
// Maquina, Usuario, Turno se cargan SOLO si los accedes
```

### Para qué sirve?
- **Performance**: No carga datos innecesarios
- **Eficiencia**: Consultas más rápidas
- **Escalabilidad**: Funciona bien con BD grandes
- **Control**: Tú decides qué cargar

---

## 1️⃣3️⃣ PATRÓN CASCADE (Cascadas en relaciones)

### ¿Qué es?
Cambios en padre afectan automáticamente a hijos.

### Dónde fue usada?
```java
// CartaGantt.java
@Entity
public class CartaGantt {
    
    @OneToMany(
        mappedBy = "cartaGantt",
        cascade = CascadeType.ALL,      // ← Cascada
        orphanRemoval = true             // ← Elimina huérfanos
    )
    @Builder.Default
    private List<ActividadMantenimiento> actividades = new ArrayList<>();
    
    @OneToMany(
        mappedBy = "cartaGantt",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @Builder.Default
    private List<Observacion> observacionesGenerales = new ArrayList<>();
}
```

### De qué forma la usamos?

```java
// Cuando GUARDAS CartaGantt
CartaGantt carta = CartaGantt.builder()
        .maquina(maquina)
        .operador(operador)
        .turno(turno)
        .build();

// Las actividades se guardan automáticamente
List<ActividadMantenimiento> actividades = request.getActividades().stream()
        .map(a -> ActividadMantenimiento.builder()
                .cartaGantt(carta)  // Referencia padre
                .descripcion(a.getDescripcion())
                .orden(a.getOrden())
                .build())
        .collect(Collectors.toList());

carta.setActividades(actividades);
cartaGanttRepository.save(carta);  // ← SALVA actividades también

// Cuando ELIMINAS CartaGantt
cartaGanttRepository.deleteById(id);  // ← ELIMINA actividades también
```

### Para qué sirve?
- **Automatización**: No necesitas guardar hijos manualmente
- **Integridad**: Padre e hijos siempre sincronizados
- **Limpieza**: Eliminas padre → eliminan hijos
- **DRY**: No repites lógica de guardado

---

## MATRIZ RESUMEN

| Patrón | Ubicación | Propósito | Beneficio |
|--------|-----------|----------|-----------|
| **Arquitectura en Capas** | Estructura general | Separar responsabilidades | Mantenibilidad |
| **Repository** | repositories/ | Abstracción de BD | Flexibilidad |
| **Inyección Dependencias** | @RequiredArgsConstructor | Desacoplamiento | Testabilidad |
| **DTO** | dto/ | Transferencia segura de datos | Validación + Seguridad |
| **@Transactional** | services/ | Integridad ACID | Consistencia |
| **Singleton/Bean** | config/ | Una instancia por app | Eficiencia |
| **Builder** | models/ | Construcción flexible | Legibilidad |
| **Strategy** | enums/ | Diferentes comportamientos | Polimorfismo |
| **Listener** | @PrePersist | Eventos automáticos | Automatización |
| **Exception Handler** | exception/ | Manejo centralizado | Consistencia |
| **Validation** | dto/ | Validación declarativa | Automatización |
| **Lazy Loading** | @ManyToOne LAZY | Carga bajo demanda | Performance |
| **Cascade** | @OneToMany | Cambios en cascada | Automatización |

---

## EJEMPLO COMPLETO: FLUJO DE PROGRAMAR MANTENCIÓN

Aquí ves TODOS los patrones trabajando juntos:

```java
// 1. PRESENTATION LAYER (Controllers)
@RestController
@RequestMapping("/api/mantenimiento")
@RequiredArgsConstructor  // ← INYECCIÓN DE DEPENDENCIAS
public class MantenimientoController {
    
    private final MantenimientoService mantenimientoService;  // ← SINGLETON
    
    @PostMapping("/programar")
    public ResponseEntity<CartaGantt> programar(
            @RequestParam Long operadorId,
            @Valid @RequestBody ProgramarMantenimientoRequest request) {  // ← DTO + VALIDATION
        
        return ResponseEntity.ok(
            mantenimientoService.programarMantenimiento(operadorId, request));
    }
}

// 2. BUSINESS LOGIC LAYER (Services)
@Service
@RequiredArgsConstructor
public class MantenimientoService {
    
    private final CartaGanttRepository cartaGanttRepository;  // ← REPOSITORY PATTERN
    private final MaquinaRepository maquinaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TurnoRepository turnoRepository;
    
    @Transactional  // ← TRANSACTIONAL (ACID)
    public CartaGantt programarMantenimiento(
            Long operadorId,
            ProgramarMantenimientoRequest request) {  // ← DTO como input
        
        // Validaciones de negocio
        Usuario operador = usuarioRepository.findById(operadorId)
                .orElseThrow(() -> new RuntimeException("Operador no encontrado"));  // ← EXCEPTION HANDLING
        
        Maquina maquina = maquinaRepository.findById(request.getMaquinaId())
                .orElseThrow(() -> new RuntimeException("Máquina no encontrada"));
        
        Turno turno = turnoRepository.findById(request.getTurnoId())
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
        
        // BUILDER PATTERN - Construir CartaGantt
        CartaGantt carta = CartaGantt.builder()
                .maquina(maquina)
                .operador(operador)
                .turno(turno)
                .fechaProgramada(request.getFechaProgramada())
                .estado(EstadoMantenimiento.PROGRAMADO)  // ← STRATEGY (Enum)
                .build();
        
        // BUILDER PATTERN - Construir Actividades
        List<ActividadMantenimiento> actividades = request.getActividades().stream()
                .map(a -> ActividadMantenimiento.builder()
                        .cartaGantt(carta)
                        .descripcion(a.getDescripcion())
                        .orden(a.getOrden())
                        .duracionEstimadaMinutos(a.getDuracionEstimadaMinutos())
                        .estado(EstadoActividad.PENDIENTE)  // ← STRATEGY
                        .build())
                .collect(Collectors.toList());
        
        carta.setActividades(actividades);  // ← CASCADE: se guardan automáticamente
        
        return cartaGanttRepository.save(carta);  // ← REPOSITORY Pattern: persist
        // Si falla → @Transactional lo revierte todo
    }
}

// 3. PERSISTENCE LAYER (Repositories)
public interface CartaGanttRepository extends JpaRepository<CartaGantt, Long> {
    // ← REPOSITORY PATTERN: abstrae BD
}

// 4. MODEL LAYER (Entities)
@Entity
@Table(name = "cartas_gantt")
@Builder
@NoArgsConstructor  // ← Lombok
@AllArgsConstructor
public class CartaGantt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)  // ← LAZY LOADING
    @JoinColumn(name = "maquina_id")
    private Maquina maquina;
    
    @OneToMany(
        mappedBy = "cartaGantt",
        cascade = CascadeType.ALL,  // ← CASCADE
        orphanRemoval = true
    )
    @OrderBy("orden ASC")
    @Builder.Default
    private List<ActividadMantenimiento> actividades = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoMantenimiento estado = EstadoMantenimiento.PROGRAMADO;
    
    // @PrePersist ← LISTENER PATTERN
    @PrePersist
    public void prePersist() {
        if (this.estado == null) {
            this.estado = EstadoMantenimiento.PROGRAMADO;
        }
    }
}

// 5. DTO LAYER (Transfer Objects)
@Data
public class ProgramarMantenimientoRequest {  // ← DTO REQUEST
    
    @NotNull
    private Long maquinaId;  // ← VALIDATION
    
    @NotNull
    private Long turnoId;
    
    @NotNull
    @Future  // ← VALIDATION
    private LocalDate fechaProgramada;
    
    @NotEmpty  // ← VALIDATION
    @Valid
    private List<ActividadRequest> actividades;
    
    @Data
    public static class ActividadRequest {
        @NotBlank  // ← VALIDATION
        @Size(min = 3, max = 500)
        private String descripcion;
        
        @Positive  // ← VALIDATION
        private Integer orden;
        
        @Positive
        private Integer duracionEstimadaMinutos;
    }
}

// 6. EXCEPTION HANDLING
@RestControllerAdvice
public class GlobalExceptionHandler {  // ← CENTRALIZED EXCEPTION HANDLING
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
```

---

## BENEFICIOS COMBINADOS

Cuando usamos TODOS estos patrones juntos:

✅ **Mantenibilidad**: Cambios localizados en una capa
✅ **Testabilidad**: Cada componente testeable independientemente
✅ **Escalabilidad**: Fácil agregar nuevos features
✅ **Robustez**: Validaciones y transacciones en múltiples niveles
✅ **Performance**: Lazy loading evita sobre-carga de BD
✅ **Seguridad**: DTOs protegen la integridad
✅ **Consistencia**: Excepciones manejadas uniformemente
✅ **Eficiencia**: Código limpio y reutilizable (Lombok, Spring)

---

**Conclusión**: Tu backend no es solo código, es una **arquitectura profesional** que usa patrones probados de la industria.

---

Última actualización: 2024-04-20

