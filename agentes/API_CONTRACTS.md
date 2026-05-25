# Contratos API - Backend ↔ Frontend

**Esto es referencia para Backend IA y Frontend IA.**

Cuando Backend IA cambia un endpoint, actualiza este archivo.
Frontend IA lo consulta para saber qué esperar.

---

## E4 — Locales (Espacios)

### GET /locales
Lista todos los locales.

**Respuesta:**
```json
[
  {
    "uuid": "550e8400-e29b-41d4-a716-446655440000",
    "nombre": "Cancha Central"
  }
]
```

**Status:** 200 OK

---

### GET /locales/{uuid}
Obtiene detalle completo de un local.

**Path:** `/locales/550e8400-e29b-41d4-a716-446655440000`

**Respuesta:**
```json
{
  "uuid": "550e8400-e29b-41d4-a716-446655440000",
  "nombre": "Cancha Central",
  "ubicacion": "Puerto Madryn",
  "telefono": "+54...",
  "email": "contacto@..."
}
```

**Status:** 200 OK | 404 Not Found

---

### GET /locales/{uuid}/canchas
Obtiene canchas dentro de un local.

**Query param:**
- `view` (opcional): `"resumen"` (default) o `"detalle"`

**Path:** `/locales/550e8400-e29b-41d4-a716-446655440000/canchas?view=resumen`

**Respuesta (resumen):**
```json
[
  {
    "uuid": "660e8400-e29b-41d4-a716-446655440001",
    "nombre": "Cancha A"
  }
]
```

**Respuesta (detalle):**
```json
[
  {
    "uuid": "660e8400-e29b-41d4-a716-446655440001",
    "nombre": "Cancha A",
    "configuracionesHorarios": [
      {
        "dia": "LUNES",
        "horaInicio": "08:00",
        "horaFin": "22:00"
      }
    ]
  }
]
```

**Status:** 200 OK | 404 Not Found

---

### POST /locales/{uuid}/configuraciones-horarios
Actualiza horarios de una local.

**Body:**
```json
{
  "configuraciones": [
    {
      "dia": "LUNES",
      "horaInicio": "08:00",
      "horaFin": "22:00"
    }
  ]
}
```

**Respuesta:**
```json
{
  "localId": "550e8400-e29b-41d4-a716-446655440000",
  "configuracionesGuardadas": 1,
  "mensaje": "Configuraciones guardadas correctamente"
}
```

**Status:** 200 OK | 404 Not Found | 400 Bad Request

---

## E4 — Canchas (Recintos)

### GET /canchas
Lista todas las canchas.

**Respuesta:**
```json
[
  {
    "uuid": "660e8400-e29b-41d4-a716-446655440001",
    "nombre": "Cancha A"
  }
]
```

**Status:** 200 OK

---

### GET /canchas/{uuid}
Obtiene detalle completo de una cancha.

**Path:** `/canchas/660e8400-e29b-41d4-a716-446655440001`

**Respuesta:**
```json
{
  "uuid": "660e8400-e29b-41d4-a716-446655440001",
  "nombre": "Cancha A",
  "configuracionesHorarios": [
    {
      "dia": "LUNES",
      "horaInicio": "08:00",
      "horaFin": "22:00"
    }
  ]
}
```

**Status:** 200 OK | 404 Not Found

---

### POST /canchas
Crea una nueva cancha.

**Body:**
```json
{
  "name": "Cancha A"
}
```

**Respuesta:**
```json
{
  "uuid": "660e8400-e29b-41d4-a716-446655440001",
  "nombre": "Cancha A"
}
```

**Status:** 201 Created | 400 Bad Request

---

### PUT /canchas/{uuid}
Actualiza una cancha existente.

**Path:** `/canchas/660e8400-e29b-41d4-a716-446655440001`

**Body:**
```json
{
  "name": "Cancha A Modificada"
}
```

**Respuesta:**
```json
{
  "uuid": "660e8400-e29b-41d4-a716-446655440001",
  "nombre": "Cancha A Modificada"
}
```

**Status:** 200 OK | 404 Not Found | 400 Bad Request

---

### DELETE /canchas/{uuid}
Elimina una cancha.

**Path:** `/canchas/660e8400-e29b-41d4-a716-446655440001`

**Status:** 204 No Content | 404 Not Found

---

## Convenciones Generales

**Status Codes:**
- `2xx` — Success
- `400` — Bad Request (validación)
- `404` — Not Found
- `500` — Internal Server Error

**UUIDs:**
- Tipo: `string` formato UUID v4
- Campo: `uuid` (no `id`)

**Timestamps (futuros):**
- Formato: ISO 8601 (`2026-05-24T10:30:00Z`)
- Campos: `createdAt`, `updatedAt`

**Errores (plantilla futura):**
```json
{
  "error": true,
  "code": "RESOURCE_NOT_FOUND",
  "message": "Local no encontrado",
  "timestamp": "2026-05-24T10:30:00Z"
}
```

---

## Próximos Endpoints (Planificado)

- E2 — Reviews (calificaciones)
- E3 — Eventos
- E5 — Reservas
- E6 — Notificaciones
- E7 — Pagos

Cuando se agreguen, actualizar este archivo.

