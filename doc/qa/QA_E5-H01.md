# Plan de Pruebas - E5-H01: Calendario de Disponibilidad

**Historia de Usuario:** Visualización de disponibilidad y gestión de turnos

**Objetivo:** Validar que la funcionalidad de calendario de disponibilidad cumple con los requisitos y proporciona una experiencia de usuario consistente.

---

## Escenarios a Validar

### Escenario 1: Ver disponibilidad semanal

**Descripción:** El propietario visualiza la disponibilidad y ocupación de una semana completa

**Precondiciones:**
- Usuario autenticado como propietario
- Cancha "Cancha 1" tiene horarios configurados
- Existen turnos tanto disponibles como ocupados

**Pasos:**

| # | Acción | Resultado esperado |
|---|--------|-------------------|
| 1 | Navegar a `/locales/{localId}/disponibilidad` | Página carga sin errores |
| 2 | Seleccionar vista semanal | Calendario muestra 7 días (lunes-domingo) |
| 3 | Visualizar turnos | Se muestran bloques horarios con estado (LIBRE/OCUPADO) |
| 4 | Turnos libres mostrados en verde | Horarios disponibles destacados |
| 5 | Turnos ocupados mostrados en rojo | Horarios reservados destacados |
| 6 | Cambiar semana (siguiente) | Calendario actualiza datos correctamente |
| 7 | Cambiar semana (anterior) | Calendario no permite ir a fecha pasada |

**Datos de prueba:**
- Local ID: `550e8400-e29b-41d4-a716-446655440000`
- Cancha: "Cancha 1"
- Turnos: 2 LIBRES, 2 OCUPADOS en la semana de prueba

**Criterio de éxito:** ✅ Todos los pasos completados sin errores

---

### Escenario 2: Ver disponibilidad diaria

**Descripción:** El propietario visualiza la disponibilidad de un día específico

**Precondiciones:**
- Usuario autenticado como propietario
- Cancha configurada con horarios

**Pasos:**

| # | Acción | Resultado esperado |
|---|--------|-------------------|
| 1 | Hacer clic en un día del calendario | Calendario cambia a vista diaria |
| 2 | Se muestran horarios por horas | Bloques de 1 hora cada uno |
| 3 | Tooltip al pasar sobre turno | Muestra resumen: hora, estado, organizador (si ocupado) |
| 4 | Horarios fuera de operación | Se muestran grises/deshabilitados |
| 5 | Seleccionar día anterior | No permite (pasado) |
| 6 | Seleccionar día futuro | Actualiza vista correctamente |

**Datos de prueba:**
- Día de prueba: Hoy o próximos 2 días
- Horarios operativos: 09:00-22:00

**Criterio de éxito:** ✅ Vista diaria funciona, restricciones aplicadas

---

### Escenario 3: Ver detalle de un turno

**Descripción:** El propietario visualiza detalles de un turno ocupado

**Precondiciones:**
- Calendario en vista diaria
- Existe al menos un turno ocupado

**Pasos:**

| # | Acción | Resultado esperado |
|---|--------|-------------------|
| 1 | Hacer clic en turno ocupado | Modal/panel se abre |
| 2 | Verificar nombre del organizador | Se muestra correctamente |
| 3 | Verificar deporte | Se muestra tipo de deporte |
| 4 | Verificar participantes confirmados | Número mostrado es >= 0 |
| 5 | Verificar estado del evento | Muestra CONFIRMADO/PENDIENTE |
| 6 | Cerrar modal | Retorna a calendario |

**Datos de prueba:**
- Turno ocupado: Fútbol, 4 participantes, CONFIRMADO

**Criterio de éxito:** ✅ Todos los datos mostrados son correctos

---

### Escenario 4: Historial de turnos finalizados

**Descripción:** El propietario consulta turnos completados en fechas pasadas

**Precondiciones:**
- Cancha cuenta con al menos 10 turnos finalizados
- Turnos en fechas pasadas (mínimo 7 días atrás)

**Pasos:**

| # | Acción | Resultado esperado |
|---|--------|-------------------|
| 1 | Hacer clic en "Ver historial" | Navega a `/locales/{id}/historial` |
| 2 | Se muestra lista de turnos pasados | Ordenado por fecha descendente (más reciente primero) |
| 3 | Seleccionar turno finalizado | Modal muestra detalles |
| 4 | Verificar nombre del organizador | Campo visible y correcto |
| 5 | Verificar deporte | Campo visible y correcto |
| 6 | Verificar cantidad de asistentes | Número >= 0 mostrado |
| 7 | Pagination funciona | Si hay >10 turnos, se muestra paginación |

**Datos de prueba:**
- Crear 15 turnos de prueba con fechas variadas (últimos 30 días)

**Criterio de éxito:** ✅ Historial accesible y datos correctos

---

## Validaciones Técnicas

### Backend API

```bash
# GET /locales/{uuid}/disponibilidad
curl -X GET "http://localhost:8080/locales/550e8400-e29b-41d4-a716-446655440000/disponibilidad?fechaInicio=2026-06-01&fechaFin=2026-06-07"

# Respuesta esperada:
# [
#   {
#     "canchaUuid": "...",
#     "canchaNombre": "Cancha 1",
#     "turnos": [
#       {
#         "fecha": "2026-06-01",
#         "horaInicio": "09:00",
#         "horaFin": "10:00",
#         "estado": "LIBRE",
#         "reserva": null
#       }
#     ]
#   }
# ]
```

### Frontend Rendering

- [ ] Calendario renderiza sin errores JavaScript
- [ ] No hay memory leaks (DevTools)
- [ ] Responsive en móvil (320px, 768px, 1024px)
- [ ] Accesibilidad: teclas de navegación funciona
- [ ] Colores contrastan correctamente (WCAG AA)

### Performance

- [ ] API responde en < 500ms
- [ ] Frontend render inicial < 3s
- [ ] Cambiar semana/día: < 300ms
- [ ] Modal detalle: < 200ms

---

## Casos de Error

### Error 1: Local no encontrado

```bash
curl http://localhost:8080/locales/999999-invalid/disponibilidad
```

**Esperado:** HTTP 404 con mensaje "Local no encontrado"

### Error 2: Fechas inválidas

```bash
curl "http://localhost:8080/locales/.../disponibilidad?fechaInicio=invalid&fechaFin=2026-06-07"
```

**Esperado:** HTTP 400 con mensaje descriptivo

### Error 3: Rango de fechas > 31 días

```bash
curl "http://localhost:8080/locales/.../disponibilidad?fechaInicio=2026-01-01&fechaFin=2026-12-31"
```

**Esperado:** HTTP 400 "Rango máximo 31 días"

---

## Checklist de QA

- [ ] Escenario 1: Ver disponibilidad semanal ✓
- [ ] Escenario 2: Ver disponibilidad diaria ✓
- [ ] Escenario 3: Ver detalle de turno ✓
- [ ] Escenario 4: Historial de turnos ✓
- [ ] Validaciones técnicas Backend ✓
- [ ] Validaciones técnicas Frontend ✓
- [ ] Performance aceptable ✓
- [ ] Manejo de errores correcto ✓
- [ ] Responsive design funciona ✓
- [ ] Accesibilidad verificada ✓
- [ ] No hay errores en console del navegador ✓
- [ ] No hay errores en logs del backend ✓

---

## Datos de Prueba Requeridos

### Locales
```json
[
  {
    "uuid": "550e8400-e29b-41d4-a716-446655440000",
    "nombre": "Cancha Central",
    "ubicacion": "Puerto Madryn"
  }
]
```

### Canchas
```json
[
  {
    "uuid": "660e8400-e29b-41d4-a716-446655440001",
    "nombre": "Cancha 1",
    "localId": "550e8400-e29b-41d4-a716-446655440000"
  }
]
```

### Horarios Configurados
- Lunes a Viernes: 09:00 - 22:00
- Sábado: 08:00 - 23:00
- Domingo: 10:00 - 20:00

### Turnos de Prueba
- 2 turnos LIBRES (próxima semana)
- 2 turnos OCUPADOS (próxima semana)
- 15 turnos históricos (últimos 30 días)
