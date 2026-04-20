# 🔧 Sistema de Mantenimiento Dulzón S.A. - Backend

Backend REST API para el sistema de control de mantenimiento preventivo de máquinas en la planta Dulzón S.A.

## 📋 Tabla de Contenidos
- [Características](#características)
- [Requisitos](#requisitos)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Ejecución](#ejecución)
- [API Endpoints](#api-endpoints)
- [Base de Datos](#base-de-datos)

---

## ✨ Características

✅ **Autenticación y Autorización**
- Login/Registro de usuarios
- Roles: ADMIN, OPERADOR, SUPERVISOR
- Encriptación de contraseñas con BCrypt

✅ **Gestión de Mantenimiento**
- Programación de mantenciones (OPERADOR)
- Inicio/cierre de actividades (SUPERVISOR)
- Registro de observaciones en tiempo real
- Cálculo automático de desviaciones (estimado vs real)

✅ **Inventario de Máquinas**
- Registro de máquinas por tipo
- Turnos y horarios de trabajo
- Historial de mantenciones por máquina

✅ **Reportes**
- Avance de mantenciones (porcentaje, desviaciones)
- Listado de mantenciones en proceso
- Filtrado por estado y fecha

---

## 🔧 Requisitos

- **Java 26** o superior
- **MariaDB 10.x** o superior
- **Maven 3.6+** (incluido en el proyecto con mvnw.cmd)
- **Windows PowerShell** (para ejecutar scripts)

---

## 📦 Instalación

### 1. Clonar o descargar el proyecto
```bash
cd C:\Users\USUARIO\IdeaProjects\mantenimientoDulzonBackend
```

### 2. Asegurar que MariaDB está instalado
```bash
# Verificar que MariaDB está corriendo
# Abrir Services (Win + R → services.msc) y buscar "MySQL80" o similar
```

### 3. Crear la base de datos (opcional, se crea automáticamente)
```sql
CREATE DATABASE IF NOT EXISTS dulzon_mantenimiento DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

---

## ⚙️ Configuración

### application.properties
```ini
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/dulzon_mantenimiento?createDatabaseIfNotExist=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=1234
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect

# Puerto del servidor
server.port=8080
```

**⚠️ IMPORTANTE:**
- Cambiar `spring.datasource.password` con la contraseña real de MariaDB
- Cambiar `spring.datasource.username` si no es "root"
- En producción: cambiar `ddl-auto` a `validate`

---

## 🚀 Ejecución

### Opción 1: Ejecutar con Maven (recomendado para desarrollo)
```bash
cd C:\Users\USUARIO\IdeaProjects\mantenimientoDulzonBackend

# Descargar dependencias, compilar y ejecutar
.\mvnw.cmd spring-boot:run
```

### Opción 2: Compilar y ejecutar el JAR
```bash
# Compilar y empaquetar
.\mvnw.cmd clean package -DskipTests

# Ejecutar
java -jar target/mantenimiento-0.0.1-SNAPSHOT.jar
```

### ✅ Verificar que está corriendo
```bash
# Abrir en el navegador:
http://localhost:8080/api/auth/login

# Debería retornar error (esperado, ya que falta el body)
# Si ves un JSON con error, ¡está funcionando!
```

---

## 📡 API Endpoints

### 🔐 Autenticación

**POST** `/api/auth/login`
```json
{
  "email": "usuario@dulzon.com",
  "password": "contraseña"
}
```
Response: Usuario autenticado

**POST** `/api/auth/registro`
```json
{
  "nombre": "Juan García",
  "email": "juan@dulzon.com",
  "password": "contraseña",
  "rol": "SUPERVISOR"
}
```
Response: Usuario creado

---

### 📋 Mantenimiento

#### OPERADOR: Programar una mantención
**POST** `/api/mantenimiento/programar?operadorId=1`
```json
{
  "maquinaId": 1,
  "turnoId": 1,
  "fechaProgramada": "2024-04-25",
  "actividades": [
    {
      "descripcion": "Inspección visual",
      "orden": 1,
      "duracionEstimadaMinutos": 30
    },
    {
      "descripcion": "Limpieza de componentes",
      "orden": 2,
      "duracionEstimadaMinutos": 60
    },
    {
      "descripcion": "Lubricación",
      "orden": 3,
      "duracionEstimadaMinutos": 20
    }
  ]
}
```

#### SUPERVISOR: Iniciar mantención
**PUT** `/api/mantenimiento/{id}/iniciar`
- La cuadrilla llegó → registra inicio exacto
- Response: CartaGantt con estado EN_PROCESO

#### SUPERVISOR: Iniciar actividad
**PUT** `/api/mantenimiento/actividad/{actividadId}/iniciar`
- Response: ActividadMantenimiento con estado EN_PROCESO

#### SUPERVISOR: Cerrar actividad
**PUT** `/api/mantenimiento/actividad/{actividadId}/cerrar`
- Response: ActividadMantenimiento con estado COMPLETADA

#### SUPERVISOR: Registrar observación
**POST** `/api/mantenimiento/actividad/{actividadId}/observacion?supervisorId=2`
```json
{
  "texto": "Pieza desgastada encontrada - requiere reemplazo"
}
```

#### SUPERVISOR: Terminar mantención
**PUT** `/api/mantenimiento/{id}/terminar?supervisorId=2`
```json
[
  {
    "texto": "Mantención completada exitosamente"
  }
]
```

#### TODOS: Ver avance
**GET** `/api/mantenimiento/{id}/avance`
Response: Incluye porcentaje de avance, desviaciones, actividades completadas

#### TODOS: Listar en proceso
**GET** `/api/mantenimiento/en-proceso`
Response: Todas las mantenciones actualmente en ejecución

#### TODOS: Listar por estado
**GET** `/api/mantenimiento?estado=PROGRAMADO`
Estados disponibles: PROGRAMADO, EN_PROCESO, TERMINADO

#### TODOS: Hoy
**GET** `/api/mantenimiento/hoy`
Response: Mantenciones programadas para hoy

---

## 🗄️ Base de Datos

### Tablas Principales

| Tabla | Descripción |
|-------|-------------|
| `usuarios` | Usuarios del sistema (ADMIN, OPERADOR, SUPERVISOR) |
| `roles` | Roles disponibles |
| `maquinas` | Máquinas de la planta |
| `turnos` | Turnos de trabajo (MAÑANA, TARDE, NOCHE) |
| `cartas_gantt` | Órdenes de mantenimiento |
| `actividades_mantenimiento` | Actividades dentro de una orden |
| `observaciones` | Notas registradas durante mantención |
| `inventario` | Insumos y materiales |

### Diagrama ER Simplificado
```
USUARIO
  ├─ rol_id → ROLES
  └─ cartas_gantt (operador_id)

CARTAS_GANTT
  ├─ maquina_id → MAQUINAS
  ├─ operador_id → USUARIOS
  ├─ turno_id → TURNOS
  ├─ actividades_mantenimiento (carta_gantt_id)
  └─ observaciones (carta_gantt_id)

ACTIVIDADES_MANTENIMIENTO
  └─ observaciones (actividad_id)
```

### Data Initializer (Seed Data)

Al iniciar, se crean automáticamente:

**Roles:**
- ADMIN
- OPERADOR
- SUPERVISOR

**Turnos:**
- MAÑANA: 06:00 - 14:00
- TARDE: 14:00 - 22:00
- NOCHE: 22:00 - 06:00

**Máquinas (8):**
- 2x Deshuesadoras
- 1x Prensa Hidráulica
- 2x Marmita de Cocción
- 1x Bomba Centrífuga
- 1x Mesa Enfriar/Envasar
- 1x Extractor de Cocción

---

## 🔒 Seguridad

### ✅ Implementado
- Encriptación de contraseñas con BCrypt (strength=10)
- Validación de datos de entrada (Jakarta Bean Validation)
- Manejo centralizado de excepciones

### ⚠️ Pendiente para Producción
- Implementar JWT para autenticación stateless
- Agregar CORS si es necesario
- HTTPS/SSL en producción
- Rate limiting
- Auditoría de cambios

---

## 📊 Logging

```ini
# Nivel de logs configurados en application.properties

logging.level.root=INFO
logging.level.com.dulzonSA.mantenimiento=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

En desarrollo, verás:
- SQL ejecutado (hibernate.SQL)
- Valores de parámetros (BasicBinder)
- Logs de la aplicación (DEBUG level)

---

## 🧪 Testing

```bash
# Ejecutar tests unitarios
.\mvnw.cmd test

# Ejecutar sin tests
.\mvnw.cmd clean package -DskipTests
```

---

## 📝 Estructura del Proyecto

```
mantenimientoDulzonBackend/
├── src/
│   ├── main/
│   │   ├── java/com/dulzonSA/mantenimiento/
│   │   │   ├── controllers/         # REST Controllers
│   │   │   ├── services/            # Lógica de negocio
│   │   │   ├── models/              # Entidades JPA
│   │   │   ├── repositories/        # Data Access (Spring Data JPA)
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── exception/           # Manejo de excepciones
│   │   │   ├── config/              # Configuración (Security, etc.)
│   │   │   └── MantenimientoApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml                          # Dependencias Maven
├── mvnw.cmd                         # Maven Wrapper (Windows)
└── README.md                        # Este archivo
```

---

## 🐛 Troubleshooting

### Error: "Connection refused" a MariaDB
```
✓ Verificar que MariaDB está corriendo
✓ Verificar credenciales en application.properties
✓ Verificar que el puerto 3306 es accesible
```

### Error: "Tabla no existe"
```
✓ Es normal, Spring crea las tablas automáticamente (ddl-auto=update)
✓ Reiniciar la aplicación si es necesario
```

### Error: "Port 8080 already in use"
```bash
# Cambiar puerto en application.properties
server.port=8081
```

### Error al compilar: "Java 26 not found"
```bash
# Instalar Java 26 o cambiar versión en pom.xml
# Cambiar en pom.xml: <java.version>26</java.version>
```

---

## 📞 Soporte

Para reportar problemas o sugerencias:
1. Revisar los logs en la consola
2. Verificar la configuración de application.properties
3. Asegurar que MariaDB está corriendo correctamente

---

## 📄 Licencia

Proyecto privado - Dulzón S.A.

---

**Última actualización:** 2024-04-20
**Estado:** ✅ Listo para conectar con Frontend

