# Diseño de API — E2-H01: Participar de un evento

**Epica:** E2 — Participation
**Historia:** E2-H01 — Participar de un evento

---

## Modelos de API

| Modelos de API | |
| :---- | :---- |
| `EventoDetail` | `{ "uuid": "...", "nombre": "Partido de futbol 5", "tipo": "ABIERTO", "estado": "DISPONIBLE", "cupoMinimo": 6, "cupoMaximo": 10, "participantesConfirmados": 3, "nivelRequerido": { "uuid": "...", "nombre": "Intermedio", "orden": 2, "deporte": "Futbol" }, "turno": { "uuid": "...", "fecha": "2026-06-15", "horaInicio": "18:00:00", "horaFin": "19:00:00", "cancha": { "uuid": "...", "nombre": "Cancha A" }, "local": { "uuid": "...", "nombre": "Complejo Olimpico", "direccion": "Av. Roca 123, Puerto Madryn" } } }` |
| `ParticipacionResponse` | `{ "uuid": "...", "estado": "CONFIRMADO", "esInvitacion": false, "fechaEstado": "2026-06-08T15:30:00" }` |

**Enums:**
- `tipo`: `ABIERTO` \| `CON_CONFIRMACION` \| `CERRADO`
- `estado` (evento): `DISPONIBLE` \| `COMPLETO` \| `CANCELADO` \| `FINALIZADO`
- `estado` (participacion): `PENDIENTE` \| `CONFIRMADO` \| `RECHAZADO` \| `CANCELADO`

> `nivelRequerido` es nullable. Si es `null`, cualquier nivel puede participar.

---

## Endpoints

| Endpoint: /eventos/{uuid} | |
| :---- | :---- |
| **Objetivo** | Ver el detalle completo de un evento para decidir si el participante se une |
| **Metodo HTTP** | GET |
| **Ruta** | /eventos/{uuid} |
| **Parametros de ruta** | `uuid`: identificador del evento |
| **Peticion** | - |
| **Respuesta exitosa** | `200 OK  EventoDetail` |
| **Validacion: el evento debe existir** | `404 Not Found { "error": "EVENTO_NO_ENCONTRADO", "message": "El evento no existe" }` |

---

| Endpoint: /eventos/{uuid}/participaciones | |
| :---- | :---- |
| **Objetivo** | Unirse directamente a un evento de tipo `ABIERTO`. Crea una participacion con estado `CONFIRMADO` |
| **Metodo HTTP** | POST |
| **Ruta** | /eventos/{uuid}/participaciones |
| **Parametros de ruta** | `uuid`: identificador del evento |
| **Peticion** | `{ "usuarioUuid": "..." }` |
| **Respuesta exitosa** | `201 Created  ParticipacionResponse (estado: "CONFIRMADO")` |
| **Validacion: el evento debe existir** | `404 Not Found { "error": "EVENTO_NO_ENCONTRADO", "message": "El evento no existe" }` |
| **Validacion: el usuario debe existir** | `404 Not Found { "error": "USUARIO_NO_ENCONTRADO", "message": "El usuario no existe" }` |
| **Validacion: el evento debe estar disponible** | `409 Conflict { "error": "EVENTO_NO_DISPONIBLE", "message": "El evento no esta disponible para inscripciones" }` |
| **Validacion: el evento debe ser de tipo ABIERTO** | `409 Conflict { "error": "TIPO_EVENTO_INVALIDO", "message": "Este evento no permite unirse directamente. Solo los eventos ABIERTOS aceptan inscripcion directa" }` |
| **Validacion: el evento debe tener cupos disponibles** | `409 Conflict { "error": "EVENTO_SIN_CUPOS", "message": "El evento no tiene cupos disponibles" }` |
| **Validacion: el participante debe cumplir el nivel requerido** | `409 Conflict { "error": "NIVEL_INSUFICIENTE", "message": "No cumple con el nivel requerido para este evento" }` |
| **Validacion: el participante no debe estar ya registrado** | `409 Conflict { "error": "PARTICIPANTE_YA_REGISTRADO", "message": "Ya se encuentra registrado en este evento" }` |

---

| Endpoint: /eventos/{uuid}/solicitudes | |
| :---- | :---- |
| **Objetivo** | Solicitar participacion en un evento de tipo `CON_CONFIRMACION`. Crea una participacion con estado `PENDIENTE` hasta que el organizador apruebe |
| **Metodo HTTP** | POST |
| **Ruta** | /eventos/{uuid}/solicitudes |
| **Parametros de ruta** | `uuid`: identificador del evento |
| **Peticion** | `{ "usuarioUuid": "..." }` |
| **Respuesta exitosa** | `201 Created  ParticipacionResponse (estado: "PENDIENTE")` |
| **Validacion: el evento debe existir** | `404 Not Found { "error": "EVENTO_NO_ENCONTRADO", "message": "El evento no existe" }` |
| **Validacion: el usuario debe existir** | `404 Not Found { "error": "USUARIO_NO_ENCONTRADO", "message": "El usuario no existe" }` |
| **Validacion: el evento debe estar disponible** | `409 Conflict { "error": "EVENTO_NO_DISPONIBLE", "message": "El evento no esta disponible para inscripciones" }` |
| **Validacion: el evento debe ser de tipo CON_CONFIRMACION** | `409 Conflict { "error": "TIPO_EVENTO_INVALIDO", "message": "Este evento no permite solicitudes de participacion. Solo los eventos CON_CONFIRMACION aceptan solicitudes" }` |
| **Validacion: el evento debe tener cupos disponibles** | `409 Conflict { "error": "EVENTO_SIN_CUPOS", "message": "El evento no tiene cupos disponibles" }` |
| **Validacion: el participante debe cumplir el nivel requerido** | `409 Conflict { "error": "NIVEL_INSUFICIENTE", "message": "No cumple con el nivel requerido para este evento" }` |
| **Validacion: el participante no debe estar ya registrado** | `409 Conflict { "error": "PARTICIPANTE_YA_REGISTRADO", "message": "Ya se encuentra registrado en este evento" }` |

---

| Endpoint: /participaciones/{uuid} | |
| :---- | :---- |
| **Objetivo** | Responder a una invitacion recibida: aceptarla (`CONFIRMADO`) o rechazarla (`RECHAZADO`). Solo aplica a participaciones creadas por el organizador (`esInvitacion = true`) con estado `PENDIENTE` |
| **Metodo HTTP** | PATCH |
| **Ruta** | /participaciones/{uuid} |
| **Parametros de ruta** | `uuid`: identificador de la participacion |
| **Peticion** | `{ "estado": "CONFIRMADO" }` o `{ "estado": "RECHAZADO" }` |
| **Respuesta exitosa** | `200 OK  ParticipacionResponse (estado: "CONFIRMADO" o "RECHAZADO")` |
| **Validacion: la participacion debe existir** | `404 Not Found { "error": "PARTICIPACION_NO_ENCONTRADA", "message": "La participacion no existe" }` |
| **Validacion: debe ser una invitacion** | `409 Conflict { "error": "NO_ES_INVITACION", "message": "Esta operacion solo aplica a invitaciones recibidas" }` |
| **Validacion: la invitacion no debe haber sido respondida** | `409 Conflict { "error": "INVITACION_YA_RESPONDIDA", "message": "La invitacion ya fue procesada anteriormente" }` |
| **Validacion: el estado debe ser CONFIRMADO o RECHAZADO** | `400 Bad Request { "error": "ESTADO_INVALIDO", "message": "El estado proporcionado no es valido para esta operacion" }` |

---

## Notas de diseno

- **Dos endpoints de participacion separados** (`/participaciones` y `/solicitudes`): aunque ambos crean una `Participacion`, la accion semantica es distinta segun el tipo de evento. El frontend muestra botones distintos ("Unirse" vs "Solicitar participacion") y llama al endpoint correspondiente sin necesidad de conocer la logica interna del backend.

- **409 para reglas de negocio**: se usa `409 Conflict` cuando el request es tecnicamente valido pero viola una regla de negocio (cupo lleno, nivel insuficiente, etc.). El `400 Bad Request` aplica solo cuando el formato del request en si es incorrecto.

- **usuarioUuid en el body**: campo provisional. Una vez implementada la autenticacion JWT, el backend obtendra el identificador del usuario del token y este campo sera eliminado del body.

- **Eventos CERRADO**: no tienen endpoint de union ni solicitud. El organizador invita directamente (fuera del alcance de E2-H01). El participante solo responde via `PATCH /participaciones/{uuid}`.
