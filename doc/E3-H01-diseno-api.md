# Diseño de API — E3-H01: Crear un evento

**Epica:** E3 — Events
**Historia:** E3-H01 — Crear un evento

---

## Modelos de API

| Modelos de API | |
| :---- | :---- |
| `EventoResponse` | `{ "uuid": "...", "nombre": "Partido de futbol 5", "tipo": "ABIERTO", "estado": "DISPONIBLE", "cupoMinimo": 6, "cupoMaximo": 10, "participantesConfirmados": 0, "limiteCancelacionParticipacion": 60, "nivelRequerido": { "uuid": "...", "nombre": "Intermedio", "orden": 2, "deporte": "Futbol" }, "turno": { "uuid": "...", "fecha": "2026-06-15", "horaInicio": "18:00:00", "horaFin": "19:00:00", "cancha": { "uuid": "...", "nombre": "Cancha A" }, "local": { "uuid": "...", "nombre": "Complejo Olimpico", "direccion": "Av. Roca 123, Puerto Madryn" } }, "organizador": { "uuid": "...", "nombre": "Juan Perez" } }` |
| `CrearEventoRequest` | `{ "turnoUuid": "...", "organizadorUuid": "...", "nombre": "Partido de futbol 5", "tipo": "ABIERTO", "cupoMinimo": 6, "cupoMaximo": 10, "limiteCancelacionParticipacion": 60, "nivelRequeridoUuid": "..." }` |

**Enums:**
- `tipo`: `ABIERTO` \| `CON_CONFIRMACION` \| `CERRADO` (default: `CERRADO`)
- `estado` (evento): `DISPONIBLE` \| `COMPLETO` \| `CANCELADO` \| `FINALIZADO`

> `nivelRequeridoUuid` es opcional. Si es `null`, cualquier nivel puede participar.

> `limiteCancelacionParticipacion` se expresa en minutos. Es opcional; si no se envía, el backend aplica el default de 60 minutos.

> `tipo` es opcional. Si no se envía, el backend aplica el default `CERRADO`.

---

## Endpoints

| Endpoint: /eventos | |
| :---- | :---- |
| **Objetivo** | Crear un nuevo evento a partir de un turno reservado previamente. El evento se crea con estado `DISPONIBLE` |
| **Metodo HTTP** | POST |
| **Ruta** | /eventos |
| **Parametros de ruta** | - |
| **Peticion** | `CrearEventoRequest` |
| **Respuesta exitosa** | `201 Created  EventoResponse` |
| **Validacion: el turno debe existir** | `404 Not Found { "error": "TURNO_NO_ENCONTRADO", "message": "El turno no existe" }` |
| **Validacion: el organizador debe existir** | `404 Not Found { "error": "USUARIO_NO_ENCONTRADO", "message": "El usuario no existe" }` |
| **Validacion: el nivel requerido debe existir (si se proporciona)** | `404 Not Found { "error": "NIVEL_NO_ENCONTRADO", "message": "El nivel de deporte no existe" }` |
| **Validacion: el cupo minimo debe ser mayor a cero** | `400 Bad Request { "error": "CUPO_MINIMO_INVALIDO", "message": "El cupo minimo debe ser mayor a cero" }` |
| **Validacion: el cupo minimo no puede superar al maximo** | `400 Bad Request { "error": "CUPO_MINIMO_MAYOR_MAXIMO", "message": "El cupo minimo no puede ser mayor al cupo maximo" }` |
| **Validacion: el cupo maximo no puede superar la capacidad de la cancha** | `409 Conflict { "error": "CUPO_MAXIMO_SUPERA_CAPACIDAD", "message": "El cupo maximo supera la capacidad de la cancha" }` |
| **Validacion: el limite de cancelacion debe estar entre 60 y 1440 minutos** | `400 Bad Request { "error": "TIEMPO_CANCELACION_INVALIDO", "message": "El limite de cancelacion debe estar entre 60 y 1440 minutos" }` |

---

## Notas de diseno

- **`limiteCancelacionParticipacion` en minutos**: se usa un entero simple (ej: `60` = 1 hora, `1440` = 24 horas) en lugar de ISO 8601 Duration para simplificar el consumo desde el frontend. El rango valido es 60–1440.

- **`tipo` con default**: si no se envía en el request, el backend asigna `CERRADO`. El frontend puede omitir el campo si el organizador no elige explicitamente.

- **`organizadorUuid` en el body**: campo provisional. Una vez implementada la autenticacion JWT, el backend obtendrá el identificador del organizador del token y este campo sera eliminado del body.

- **409 para reglas de negocio de datos externos**: se usa `409 Conflict` cuando la violacion depende del estado de otro recurso (la capacidad de la cancha). Los `400 Bad Request` aplican a invariantes del request en si (cupos, tiempo de cancelacion).

- **`EventoDetailDTO` en el backend**: el DTO existente (`EventoDetailDTO`) es actualmente minimo y fue suficiente para E2-H01. La implementacion de E3-H01 debera expandirlo (o crear un DTO nuevo) para incluir los campos de turno, cancha, local, organizador y nivel requerido que define este contrato.

- **`participantesConfirmados` como entero**: en la respuesta de creacion, el conteo siempre sera `0`. El campo se incluye para que el frontend pueda usar el mismo modelo en listados y en la respuesta de creacion sin transformaciones.
