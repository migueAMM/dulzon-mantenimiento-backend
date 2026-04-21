# 🔧 Dulzón Mantenimiento — Backend

Spring Boot 3.3.4 · Java 21 · MariaDB · Sin Lombok

---

## ✅ Qué cambió respecto al proyecto original

| Problema original | Solución aplicada |
|---|---|
| `package lombok does not exist` | Eliminado completamente — getters/setters manuales |
| Spring Boot 4.0.0 (beta inestable) | Cambiado a 3.3.4 (versión estable) |
| Driver `mysql-connector-j` | Reemplazado por `mariadb-java-client` |
| Java 26 (no disponible) | Configurado para Java 21 LTS |
| `@Builder` sin Lombok | Creación de objetos con `new + setters` |

---

## 📋 Requisitos para correr el proyecto

Necesitas tener instalado exactamente **3 cosas**:

### 1. Java 21 (o superior)

**Verificar si ya lo tienes:**
```bash
java -version
```
Debe mostrar algo como: `openjdk version "21.x.x"`

**Si no lo tienes — descargar Java 21 LTS:**
- Windows / Mac / Linux: https://adoptium.net/es/temurin/releases/?version=21
- Descarga el instalador `.msi` (Windows) o `.pkg` (Mac) y ejecútalo

**Verificar después de instalar:**
```bash
java -version   # Debe mostrar 21.x.x
```

---

### 2. Maven (opcional — el proyecto lo incluye)

El proyecto ya incluye **Maven Wrapper** (`mvnw` / `mvnw.cmd`), que descarga Maven automáticamente la primera vez. **No necesitas instalar Maven por separado.**

Si prefieres instalarlo globalmente:
- Descargar: https://maven.apache.org/download.cgi
- Guía de instalación: https://maven.apache.org/install.html

**Verificar:**
```bash
mvn --version    # Si está instalado globalmente
./mvnw --version # Usando el wrapper del proyecto (Linux/Mac)
mvnw.cmd --version # Usando el wrapper (Windows)
```

---

### 3. MariaDB

**Verificar si ya lo tienes corriendo:**
```bash
# Linux
sudo systemctl status mariadb

# Mac
brew services list | grep mariadb

# Windows — abrir Services (Win+R → services.msc) y buscar "MariaDB" o "MySQL80"
```

**Si no lo tienes — opciones de instalación:**

| Opción | Recomendado para | Link |
|---|---|---|
| MariaDB oficial | Producción | https://mariadb.org/download/ |
| XAMPP (incluye MariaDB) | Desarrollo fácil | https://www.apachefriends.org |
| Laragon (Windows) | Desarrollo Windows | https://laragon.org/download/ |

**Después de instalar, crear usuario y verificar conexión:**
```sql
-- Conectar a MariaDB como root
mysql -u root -p

-- Verificar que funciona (debe mostrar la versión)
SELECT VERSION();

-- La base de datos se crea sola al arrancar el proyecto
-- (gracias a createDatabaseIfNotExist=true en application.properties)
```

**Si tu contraseña de root NO es `1234`**, edita el archivo:
```
src/main/resources/application.properties
```
Cambia la línea:
```properties
spring.datasource.password=1234
```
Por tu contraseña real.

---

## 🚀 Cómo arrancar

### Paso 1 — Verificar requisitos automáticamente

```bash
# Linux / Mac
bash verificar-requisitos.sh

# Windows (doble clic o desde CMD)
verificar-requisitos.bat
```

### Paso 2 — Arrancar el servidor

```bash
# Linux / Mac
bash arrancar.sh

# Windows
arrancar.bat
```

O manualmente:
```bash
# Linux / Mac
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

### Paso 3 — Verificar que funciona

Abre el navegador en:
```
http://localhost:8080/api/mantenimiento/hoy
```
Debe devolver `[]` (lista vacía, normal al inicio).

---

## 🧪 Ejecutar los tests (no necesita MariaDB)

Los tests usan H2 (base de datos en memoria), así que **funcionan sin tener MariaDB instalado**.

```bash
# Linux / Mac
./mvnw test

# Windows
mvnw.cmd test

# O con el script incluido
bash arrancar.sh test       # Linux/Mac
arrancar.bat test           # Windows
```

**Tests incluidos (8 en total):**
- ✅ Flujo completo: programar → iniciar → actividades → terminar
- ✅ Cálculo correcto de porcentaje de avance
- ✅ Error al iniciar una mantención ya en proceso
- ✅ Error al terminar con actividades abiertas
- ✅ Login con credenciales correctas
- ✅ Login con password incorrecta → error
- ✅ Login con email inexistente → error
- ✅ Registro con email duplicado → error
- ✅ Observación guardada en actividad
- ✅ Listar por estado devuelve lista correcta

---

## 📡 Endpoints disponibles

### Autenticación
```
POST /api/auth/registro
POST /api/auth/login
```

### Operador (programar)
```
POST /api/mantenimiento/programar?operadorId={id}
```

### Supervisor (ejecutar)
```
PUT  /api/mantenimiento/{id}/iniciar
PUT  /api/mantenimiento/actividad/{id}/iniciar
PUT  /api/mantenimiento/actividad/{id}/cerrar
POST /api/mantenimiento/actividad/{id}/observacion?supervisorId={id}
PUT  /api/mantenimiento/{id}/terminar?supervisorId={id}
```

### Consultas (todos)
```
GET /api/mantenimiento/hoy
GET /api/mantenimiento/en-proceso
GET /api/mantenimiento?estado=PROGRAMADO
GET /api/mantenimiento/{id}/avance
GET /api/reportes/dashboard
GET /api/reportes/historial
GET /api/reportes/{id}
```

---

## 🗂️ Estructura del proyecto

```
dulzon-mantenimiento-backend/
├── src/
│   ├── main/
│   │   ├── java/com/dulzonSA/mantenimiento/
│   │   │   ├── MantenimientoApplication.java   ← Punto de entrada
│   │   │   ├── DataInitializer.java            ← Siembra roles, turnos, máquinas
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java         ← CORS configurado
│   │   │   ├── controllers/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── MantenimientoController.java
│   │   │   │   └── ReporteController.java
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   │   ├── ProgramarMantenimientoRequest.java
│   │   │   │   │   └── ObservacionRequest.java
│   │   │   │   └── response/
│   │   │   │       ├── AvanceMantenimientoResponse.java
│   │   │   │       ├── ActividadResponse.java
│   │   │   │       └── ObservacionResponse.java
│   │   │   ├── exception/
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── models/
│   │   │   │   ├── enums/                      ← EstadoMantenimiento, TipoRol, etc.
│   │   │   │   ├── ActividadMantenimiento.java
│   │   │   │   ├── CartaGantt.java
│   │   │   │   ├── Inventario.java
│   │   │   │   ├── Maquina.java
│   │   │   │   ├── Observacion.java
│   │   │   │   ├── Rol.java
│   │   │   │   ├── Turno.java
│   │   │   │   └── Usuario.java
│   │   │   ├── repositories/                   ← 8 repositorios JPA
│   │   │   └── services/
│   │   │       ├── MantenimientoService.java   ← Toda la lógica de negocio
│   │   │       └── UsuarioService.java
│   │   └── resources/
│   │       └── application.properties          ← Configuración MariaDB
│   └── test/
│       ├── java/                               ← 8 tests de lógica
│       └── resources/
│           └── application-test.properties     ← Configuración H2 para tests
├── pom.xml                                     ← Sin Lombok, con MariaDB
├── verificar-requisitos.sh / .bat              ← Verificador automático
└── arrancar.sh / .bat                          ← Script de arranque
```

---

## 🐛 Solución de problemas frecuentes

**`Connection refused` a MariaDB**
```bash
# Linux
sudo systemctl start mariadb
# Mac
brew services start mariadb
# Windows: Win+R → services.msc → iniciar MySQL80 o MariaDB
```

**`Access denied for user 'root'`**
Edita `src/main/resources/application.properties` y cambia la contraseña.

**`Port 8080 already in use`**
Cambia en `application.properties`: `server.port=8081`

**`java: cannot find symbol` al compilar**
Verifica que `JAVA_HOME` apunta a Java 21:
```bash
echo $JAVA_HOME    # Linux/Mac
echo %JAVA_HOME%   # Windows
```

**Tests fallan con `H2 not found`**
```bash
./mvnw clean test   # Limpiar cache y re-ejecutar
```
