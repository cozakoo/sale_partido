# Contratos API - Backend ↔ Frontend

**Esto es referencia para Backend IA y Frontend IA.**

Cuando Backend IA cambia un endpoint, actualiza este archivo.
Frontend IA lo consulta para saber qué esperar.

---

## E4 — Locales (Espacios)

### GET /locales
Lista todos los locales. Soporta filtros opcionales por query parameters.

**Query params (opcionales):**
- `ubicacion` (String): Búsqueda parcial de texto en la dirección o nombre de localidad.
- `fecha` (String): Búsqueda por fecha (ej: `"hoy"`, `"mañana"` o fecha ISO `"YYYY-MM-DD"`).
- `tipoDeporte` (String): Nombre del deporte (ej: `"Fútbol"`).
- `horarioDesde` (String): Horario de inicio (ej: `"08:00"` o `"08:00 hs"`).
- `horarioHasta` (String): Horario de fin (ej: `"22:00"` o `"22:00 hs"`).

**Respuesta:**
```json
[
  {
    "uuid": "550e8400-e29b-41d4-a716-446655440000",
    "nombre": "Cancha Central",
    "ubicacion": "Av. Roca 123, Puerto Madryn",
    "deportes": ["Fútbol", "Tenis"],
    "telefono": "+54 280 411-1001",
    "descripcion": "Complejo deportivo con césped sintético y canchas techadas.",
    "horario": [
      {
        "uuid": "440e8400-e29b-41d4-a716-446655440003",
        "dia": "MONDAY",
        "horarioApertura": "08:00:00",
        "horarioCierre": "22:00:00"
      }
    ],
    "deportesDisponibles": ["Fútbol", "Tenis"],
    "canchas": [
      {
        "uuid": "660e8400-e29b-41d4-a716-446655440001",
        "nombre": "Cancha A",
        "deporte": "Fútbol",
        "capacidad": 10,
        "configuracionesHorarios": [
          {
            "activo": true,
            "duracionTurno": 60,
            "configuracionesDias": [
              {
                "diaSemana": "MONDAY",
                "horaInicio": "08:00:00",
                "horaFin": "22:00:00"
              }
            ]
          }
        ]
      }
    ]
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
  "ubicacion": "Av. Roca 123, Puerto Madryn",
  "deportes": ["Fútbol", "Tenis"],
  "telefono": "+54 280 411-1001",
  "descripcion": "Complejo deportivo con césped sintético y canchas techadas.",
  "horario": [
    {
      "uuid": "440e8400-e29b-41d4-a716-446655440003",
      "dia": "MONDAY",
      "horarioApertura": "08:00:00",
      "horarioCierre": "22:00:00"
    }
  ],
  "canchas": [
    {
      "uuid": "660e8400-e29b-41d4-a716-446655440001",
      "nombre": "Cancha A",
      "deporte": "Fútbol",
      "capacidad": 10,
      "configuracionesHorarios": [
        {
          "activo": true,
          "duracionTurno": 60,
          "configuracionesDias": [
            {
              "diaSemana": "MONDAY",
              "horaInicio": "08:00:00",
              "horaFin": "22:00:00"
            }
          ]
        }
      ]
    }
  ]
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
    "nombre": "Cancha A",
    "capacidad": 10
  }
]
```

**Respuesta (detalle):**
```json
[
  {
    "uuid": "660e8400-e29b-41d4-a716-446655440001",
    "nombre": "Cancha A",
    "capacidad": 10,
    "configuracionesHorarios": [
      {
        "activo": true,
        "duracionTurno": 60,
        "configuracionesDias": [
          {
            "diaSemana": "MONDAY",
            "horaInicio": "08:00:00",
            "horaFin": "22:00:00"
          }
        ]
      }
    ]
  }
]
```

**Status:** 200 OK | 404 Not Found

---

### POST /locales/{uuid}/configuraciones-horarios
Actualiza horarios de un local.

**Body:**
```json
{
  "canchas": [
    {
      "canchaUuid": "660e8400-e29b-41d4-a716-446655440001",
      "duracionTurno": 60,
      "configuracionesDias": [
        {
          "diaSemana": "MONDAY",
          "horaInicio": "08:00",
          "horaFin": "22:00"
        }
      ]
    }
  ]
}
```

**Respuesta:**
```json
{
  "canchas": [
    {
      "canchaUuid": "660e8400-e29b-41d4-a716-446655440001",
      "duracionTurno": 60,
      "configuracionesDias": [
        {
          "diaSemana": "MONDAY",
          "horaInicio": "08:00:00",
          "horaFin": "22:00:00"
        }
      ]
    }
  ]
}
```

**Status:** 200 OK | 404 Not Found | 400 Bad Request

---

### GET /locales/{uuid}/disponibilidad
Obtiene la disponibilidad (turnos libres y ocupados) de todas las canchas de un local en un rango de fechas.

**Query params:**
- `fechaInicio` (requerido): Fecha de inicio ISO 8601 (`2026-06-01`)
- `fechaFin` (requerido): Fecha de fin ISO 8601 (`2026-06-07`)
- Rango máximo: 31 días

**Path:** `/locales/550e8400-e29b-41d4-a716-446655440000/disponibilidad?fechaInicio=2026-06-01&fechaFin=2026-06-07`

**Respuesta:**
```json
[
  {
    "canchaUuid": "660e8400-e29b-41d4-a716-446655440001",
    "canchaNombre": "Cancha 1",
    "turnos": [
      {
        "fecha": "2026-06-01",
        "horaInicio": "09:00:00",
        "horaFin": "10:00:00",
        "espacioNombre": "Cancha 1",
        "deporte": null,
        "estado": "LIBRE",
        "turno": null
      },
      {
        "fecha": "2026-06-01",
        "horaInicio": "10:00:00",
        "horaFin": "11:00:00",
        "espacioNombre": "Cancha 1",
        "deporte": "Fútbol",
        "estado": "OCUPADO",
        "turno": {
          "uuid": "770e8400-e29b-41d4-a716-446655440002",
          "nombreOrganizador": "Juan Pérez",
          "deporte": "Fútbol",
          "capacidad": 10,
          "cantidadParticipantesConfirmados": 10,
          "estadoEvento": "CONFIRMADO"
        }
      }
    ]
  }
]
```

**Status:** 200 OK | 400 Bad Request | 404 Not Found

---

## E4 — Canchas (Recintos)

### GET /canchas
Lista todas las canchas.

**Respuesta:**
```json
[
  {
    "uuid": "660e8400-e29b-41d4-a716-446655440001",
    "nombre": "Cancha A",
    "capacidad": 10
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
  "capacidad": 10,
  "configuracionesHorarios": [
    {
      "activo": true,
      "duracionTurno": 60,
      "configuracionesDias": [
        {
          "diaSemana": "MONDAY",
          "horaInicio": "08:00:00",
          "horaFin": "22:00:00"
        }
      ]
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
  "name": "Cancha A",
  "capacidad": 10
}
```

**Respuesta:**
```json
{
  "uuid": "660e8400-e29b-41d4-a716-446655440001",
  "nombre": "Cancha A",
  "capacidad": 10
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
  "name": "Cancha A Modificada",
  "capacidad": 12
}
```

**Respuesta:**
```json
{
  "uuid": "660e8400-e29b-41d4-a716-446655440001",
  "nombre": "Cancha A Modificada",
  "capacidad": 12
}
```

**Status:** 200 OK | 404 Not Found | 400 Bad Request

---

### DELETE /canchas/{uuid}
Elimina una cancha.

**Path:** `/canchas/660e8400-e29b-41d4-a716-446655440001`

**Status:** 204 No Content | 404 Not Found

---

## E3 — Eventos

### POST /eventos
Crea un nuevo evento a partir de un turno reservado.

**Body:**
```json
{
  "turno": {
    "fecha": "2026-06-15",
    "horaInicio": "18:00:00",
    "horaFin": "19:00:00",
    "canchaUuid": "660e8400-e29b-41d4-a716-446655440001"
  },
  "organizadorUuid": "880e8400-e29b-41d4-a716-446655440003",
  "nombre": "Evento en Cancha A",
  "cupoMinimo": 8,
  "cupoMaximo": 10,
  "tipo": "CERRADO",
  "limiteCancelacionParticipacion": 60,
  "nivelRequeridoUuid": "990e8400-e29b-41d4-a716-446655440004"
}
```

**Respuesta:**
```json
{
  "uuid": "aa0e8400-e29b-41d4-a716-446655440005",
  "nombre": "Evento en Cancha A",
  "deporte": "Fútbol",
  "descripcion": null,
  "tipo": "CERRADO",
  "estado": "DISPONIBLE",
  "cupoMinimo": 8,
  "cupoMaximo": 10,
  "participantesConfirmados": 0,
  "participantes": [],
  "nivelRequerido": {
    "uuid": "990e8400-e29b-41d4-a716-446655440004",
    "nombre": "Intermedio",
    "orden": 2,
    "deporte": "Fútbol"
  },
  "turno": {
    "uuid": "770e8400-e29b-41d4-a716-446655440002",
    "fecha": "2026-06-15",
    "horaInicio": "18:00:00",
    "horaFin": "19:00:00",
    "cancha": {
      "uuid": "660e8400-e29b-41d4-a716-446655440001",
      "nombre": "Cancha A"
    },
    "local": {
      "uuid": "550e8400-e29b-41d4-a716-446655440000",
      "nombre": "Cancha Central",
      "direccion": "Av. Roca 123, Puerto Madryn"
    }
  }
}
```

**Status:** 201 Created | 400 Bad Request

---

### GET /eventos
Lista todos los eventos.

**Respuesta:** Array de `EventoDetailDTO` (ver formato en POST /eventos).

**Status:** 200 OK

---

### GET /eventos/{uuid}
Obtiene el detalle de un evento específico.

**Respuesta:** Objeto `EventoDetailDTO` (ver formato en POST /eventos).

**Status:** 200 OK | 404 Not Found

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
- E5 — Reservas
- E6 — Notificaciones
- E7 — Pagos

Cuando se agreguen, actualizar este archivo.

