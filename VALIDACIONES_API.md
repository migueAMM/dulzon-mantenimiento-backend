# 📋 GUÍA DE VALIDACIONES - Para Desarrolladores Frontend

Este documento especifica exactamente qué validaciones se aplican en cada request.

---

## 🔐 AUTENTICACIÓN

### POST `/api/auth/login`

**Body:**
```json
{
  "email": "string",
  "password": "string"
}
```

**Validaciones:**
| Campo | Tipo | Requerido | Min | Max | Patrón | Ejemplo |
|-------|------|-----------|-----|-----|--------|---------|
| email | String | ✅ | - | - | RFC 5322 | carlos@dulzon.com |
| password | String | ✅ | 6 | - | - | Segura123 |

**Errores Posibles:**
```json
{
  "email": ["Email inválido", "Email es requerido"]
}
```
```json
{
  "password": ["Contraseña es requerida", "Contraseña debe tener mínimo 6 caracteres"]
}
```

**Respuesta 200:**
```json
{
  "id": 1,
  "nombre": "Carlos Mendez",
  "email": "carlos@dulzon.com",
  "password": "...(encriptada)...",
  "rol": {
    "id": 2,
    "nombre": "OPERADOR"
  },
  "activo": true
}
```

---

### POST `/api/auth/registro`

**Body:**
```json
{
  "nombre": "string",
  "email": "string",
  "password": "string",
  "rol": "string"
}
```

**Validaciones:**
| Campo | Tipo | Requerido | Min | Max | Patrón | Valores Válidos |
|-------|------|-----------|-----|-----|--------|-----------------|
| nombre | String | ✅ | 3 | 100 | - | "Juan García" |
| email | String | ✅ | - | - | RFC 5322 | "juan@dulzon.com" |
| password | String | ✅ | 6 | - | - | "Segura123" |
| rol | String | ✅ | - | - | - | ADMIN, OPERADOR, SUPERVISOR |

**Errores Posibles:**
```json
{
  "status": 400,
  "error": "Error de Validación",
  "message": "Nombre debe tener entre 3 y 100 caracteres"
}
```
```json
{
  "status": 409,
  "error": "Error",
  "message": "Ya existe un usuario con el email: juan@dulzon.com"
}
```

**Respuesta 200:**
```json
{
  "id": 2,
  "nombre": "Juan García",
  "email": "juan@dulzon.com",
  "password": "...(encriptada)...",
  "rol": {
    "id": 2,
    "nombre": "OPERADOR"
  },
  "activo": true
}
```

---

## 📋 MANTENIMIENTO

### POST `/api/mantenimiento/programar?operadorId={id}`

**Query Parameters:**
| Parámetro | Tipo | Requerido | Descripción |
|-----------|------|-----------|-------------|
| operadorId | Long | ✅ | ID del usuario OPERADOR |

**Body:**
```json
{
  "maquinaId": 1,
  "turnoId": 1,
  "fechaProgramada": "2024-04-25",
  "actividades": [
    {
      "descripcion": "Inspección inicial",
      "orden": 1,
      "duracionEstimadaMinutos": 30
    }
  ]
}
```

**Validaciones:**
| Campo | Tipo | Requerido | Min | Max | Patrón | Ejemplo |
|-------|------|-----------|-----|-----|--------|---------|
| maquinaId | Long | ✅ | - | - | > 0 | 1 |
| turnoId | Long | ✅ | - | - | > 0 | 1 |
| fechaProgramada | Date | ✅ | - | - | Futura | "2024-04-25" |
| actividades | Array | ✅ | 1+ | - | No vacío | [...] |
| actividades[].descripcion | String | ✅ | 3 | 500 | - | "Inspección visual" |
| actividades[].orden | Integer | ✅ | - | - | > 0 | 1, 2, 3 |
| actividades[].duracionEstimadaMinutos | Integer | ✅ | - | - | > 0 | 30, 60 |

**Errores Posibles:**
```json
{
  "status": 400,
  "message": "Máquina no encontrada: 999"
}
```
```json
{
  "status": 400,
  "message": "La fecha programada debe ser en el futuro"
}
```
```json
{
  "status": 400,
  "message": "La descripción de la actividad es requerida"
}
```

**Respuesta 200:**
```json
{
  "id": 1,
  "maquina": {
    "id": 1,
    "nombre": "Deshuesadora 1"
  },
  "operador": {
    "id": 1,
    "nombre": "Carlos Mendez"
  },
  "turno": {
    "id": 1,
    "nombre": "MAÑANA"
  },
  "fechaProgramada": "2024-04-25",
  "estado": "PROGRAMADO",
  "actividades": [...]
}
```

---

### PUT `/api/mantenimiento/{id}/iniciar`

**Path Parameters:**
| Parámetro | Tipo | Requerido | Descripción |
|-----------|------|-----------|-------------|
| id | Long | ✅ | ID de CartaGantt |

**Body:** (ninguno)

**Validaciones:**
- CartaGantt existe
- Estado actual = PROGRAMADO

**Errores Posibles:**
```json
{
  "status": 400,
  "message": "Carta Gantt no encontrada: 999"
}
```
```json
{
  "status": 400,
  "message": "La mantención no está PROGRAMADA (estado actual: EN_PROCESO)"
}
```

**Respuesta 200:**
```json
{
  "id": 1,
  "estado": "EN_PROCESO",
  "fechaInicioReal": "2024-04-20T10:30:45.123456"
}
```

---

### PUT `/api/mantenimiento/actividad/{actividadId}/iniciar`

**Path Parameters:**
| Parámetro | Tipo | Requerido | Descripción |
|-----------|------|-----------|-------------|
| actividadId | Long | ✅ | ID de ActividadMantenimiento |

**Body:** (ninguno)

**Validaciones:**
- Actividad existe
- Carta Gantt padre está EN_PROCESO
- Estado actual de actividad = PENDIENTE

**Respuesta 200:**
```json
{
  "id": 1,
  "estado": "EN_PROCESO",
  "fechaInicioReal": "2024-04-20T10:35:00.123456"
}
```

---

### PUT `/api/mantenimiento/actividad/{actividadId}/cerrar`

**Path Parameters:**
| Parámetro | Tipo | Requerido | Descripción |
|-----------|------|-----------|-------------|
| actividadId | Long | ✅ | ID de ActividadMantenimiento |

**Body:** (ninguno)

**Validaciones:**
- Actividad existe
- Estado actual = EN_PROCESO

**Respuesta 200:**
```json
{
  "id": 1,
  "estado": "COMPLETADA",
  "fechaFinReal": "2024-04-20T10:50:00.123456",
  "desviacionMinutos": 5
}
```

---

### POST `/api/mantenimiento/actividad/{actividadId}/observacion?supervisorId={id}`

**Path Parameters:**
| Parámetro | Tipo | Requerido | Descripción |
|-----------|------|-----------|-------------|
| actividadId | Long | ✅ | ID de ActividadMantenimiento |

**Query Parameters:**
| Parámetro | Tipo | Requerido | Descripción |
|-----------|------|-----------|-------------|
| supervisorId | Long | ✅ | ID del usuario SUPERVISOR |

**Body:**
```json
{
  "texto": "Se encontró pieza desgastada"
}
```

**Validaciones:**
| Campo | Tipo | Requerido | Min | Max | Ejemplo |
|-------|------|-----------|-----|-----|---------|
| texto | String | ✅ | 3 | 2000 | "Se encontró..." |

**Errores Posibles:**
```json
{
  "status": 400,
  "message": "El texto de la observación no puede estar vacío"
}
```
```json
{
  "status": 400,
  "message": "Usuario no encontrado: 999"
}
```

**Respuesta 200:**
```json
{
  "id": 1,
  "texto": "Se encontró pieza desgastada",
  "supervisorNombre": "María Supervisora",
  "fechaHora": "2024-04-20T10:45:30.123456"
}
```

---

### PUT `/api/mantenimiento/{id}/terminar?supervisorId={id}`

**Path Parameters:**
| Parámetro | Tipo | Requerido | Descripción |
|-----------|------|-----------|-------------|
| id | Long | ✅ | ID de CartaGantt |

**Query Parameters:**
| Parámetro | Tipo | Requerido | Descripción |
|-----------|------|-----------|-------------|
| supervisorId | Long | ✅ | ID del usuario SUPERVISOR |

**Body:** (opcional)
```json
[
  {
    "texto": "Observación de cierre 1"
  },
  {
    "texto": "Observación de cierre 2"
  }
]
```

**Validaciones:**
- CartaGantt existe
- Estado actual = EN_PROCESO
- Todas las actividades completadas
- Cada observación: 3-2000 caracteres

**Errores Posibles:**
```json
{
  "status": 400,
  "message": "Existen actividades aún en proceso. Ciérrelas antes de terminar la mantención"
}
```

**Respuesta 200:**
```json
{
  "id": 1,
  "estado": "TERMINADO",
  "fechaFinReal": "2024-04-20T11:30:00.123456",
  "observacionesGenerales": [...]
}
```

---

## 📊 CONSULTAS (GET)

### GET `/api/mantenimiento/{id}/avance`

**Path Parameters:**
| Parámetro | Tipo | Requerido |
|-----------|------|-----------|
| id | Long | ✅ |

**Respuesta 200:**
```json
{
  "cartaGanttId": 1,
  "maquinaNombre": "Deshuesadora 1",
  "maquinaTipo": "DESHUESADORA",
  "codigoInternoMaquina": "DH-001",
  "turnoNombre": "MAÑANA",
  "fechaProgramada": "2024-04-25",
  "operadorNombre": "Carlos",
  "fechaInicioReal": "2024-04-20T10:30:00",
  "fechaFinReal": null,
  "estado": "EN_PROCESO",
  "totalActividades": 3,
  "actividadesCompletadas": 1,
  "porcentajeAvance": 33,
  "desviacionTotalMinutos": 5,
  "actividades": [...],
  "observacionesGenerales": [...]
}
```

---

### GET `/api/mantenimiento/en-proceso`

**Respuesta 200:**
```json
[
  {
    "cartaGanttId": 1,
    "estado": "EN_PROCESO",
    "porcentajeAvance": 33,
    ...
  }
]
```

---

### GET `/api/mantenimiento?estado={estado}`

**Query Parameters:**
| Parámetro | Tipo | Requerido | Valores |
|-----------|------|-----------|---------|
| estado | String | No (default: PROGRAMADO) | PROGRAMADO, EN_PROCESO, TERMINADO |

**Ejemplo:** `GET /api/mantenimiento?estado=TERMINADO`

**Respuesta 200:**
```json
[
  {
    "cartaGanttId": 1,
    "estado": "TERMINADO",
    ...
  }
]
```

---

### GET `/api/mantenimiento/hoy`

**Respuesta 200:**
```json
[
  {
    "cartaGanttId": 1,
    "estado": "PROGRAMADO",
    "fechaProgramada": "2024-04-20",
    ...
  }
]
```

---

## 🔄 ESTADOS

### Estados de CartaGantt (Mantención):
```
PROGRAMADO → EN_PROCESO → TERMINADO
```

### Estados de ActividadMantenimiento:
```
PENDIENTE → EN_PROCESO → COMPLETADA
```

---

## ⏱️ FORMATOS

### Fechas
- Formato: `YYYY-MM-DD` (ISO 8601)
- Ejemplo: `2024-04-25`
- Validación: Debe ser fecha futura

### DateTime
- Formato: `YYYY-MM-DDTHH:mm:ss.SSSSSS`
- Ejemplo: `2024-04-20T10:30:45.123456`
- Se establece automáticamente en servidor

### Integers
- Minutos: número positivo (> 0)
- IDs: número positivo (> 0)
- Orden de actividad: número positivo (> 0)
- Porcentaje: 0-100

---

## 🎯 RESUMEN DE ERRORES COMUNES

| Error | Causa | Solución |
|-------|-------|----------|
| 400 Bad Request | Validación falla | Revisar tipos de datos y rangos |
| 409 Conflict | Email duplicado / Recurso conflictivo | Usar email único o revisar estado |
| 404 Not Found | ID no existe | Verificar que el ID es válido |
| 500 Internal Server Error | Error en servidor | Revisar logs, reintentar |

---

## 📱 FLUJO PARA FRONTEND

```typescript
// 1. Registro
POST /api/auth/registro
  → Usuario registrado (id, nombre, email, rol)

// 2. Login
POST /api/auth/login
  → Usuario autenticado

// 3. Operador programa
POST /api/mantenimiento/programar?operadorId=X
  → CartaGantt creada (estado: PROGRAMADO)

// 4. Supervisor inicia
PUT /api/mantenimiento/{cartaGanttId}/iniciar
  → CartaGantt (estado: EN_PROCESO)

// 5. Supervisor ejecuta actividades
PUT /api/mantenimiento/actividad/{actividadId}/iniciar
PUT /api/mantenimiento/actividad/{actividadId}/cerrar
POST /api/mantenimiento/actividad/{actividadId}/observacion?supervisorId=X

// 6. Supervisor termina
PUT /api/mantenimiento/{cartaGanttId}/terminar?supervisorId=X
  → CartaGantt (estado: TERMINADO)

// 7. Cualquiera consulta
GET /api/mantenimiento/{cartaGanttId}/avance
  → Detalles completos con porcentaje
```

---

**Última actualización**: 2024-04-20

