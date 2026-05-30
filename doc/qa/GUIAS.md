# Guías - QA E5-H01

## Tabla de Contenidos

1. [Guía de Navegación](#navegación)
2. [Guía de Datos de Prueba](#datos-de-prueba)
3. [Guía de Debugging](#debugging)
4. [Guía de Reportes](#reportes)

---

## Navegación

### Ruta hacia Calendario de Disponibilidad

```
Página Principal
  ↓
Listar Locales
  ↓
Seleccionar Local (ej. "Cancha Central")
  ↓
Listar Canchas del Local
  ↓
Seleccionar Cancha (ej. "Cancha 1")
  ↓
Panel de Acciones
  ├─ Ver Disponibilidad ← AQUÍ
  ├─ Configurar Horarios
  └─ Ver Historial
```

### URLs Directas

```
# Listar locales
http://localhost:4200/locales

# Detalles de local
http://localhost:4200/locales/{localId}

# Disponibilidad de cancha
http://localhost:4200/locales/{localId}/canchas/{canchaId}/disponibilidad

# Historial
http://localhost:4200/locales/{localId}/canchas/{canchaId}/historial
```

### Parámetros de Query (Frontend)

```
?semana=2026-06-01        # Filtrar por semana
?dia=2026-06-02           # Cambiar a vista diaria
?view=semana|dia          # Cambiar vista
&cancha=660e8400...       # Filtrar cancha específica
```

---

## Datos de Prueba

### Crear Local de Prueba

```bash
# API request
curl -X POST http://localhost:8080/locales \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Test Local",
    "ubicacion": "Puerto Madryn",
    "telefono": "+54 9 280 XXX-XXXX",
    "email": "test@test.com"
  }'
```

### Crear Cancha de Prueba

```bash
curl -X POST http://localhost:8080/canchas \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Test Cancha",
    "localId": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

### Crear Turnos de Prueba

```bash
# SQL directo (desarrollo)
psql -d sale_partido -c "
INSERT INTO reservas (uuid, cancha_id, fecha_inicio, fecha_fin, estado, deporte, participantes, organizador)
VALUES
  ('770e8400-e29b-41d4-a716-446655440001', 1, '2026-06-01 09:00:00', '2026-06-01 10:00:00', 'CONFIRMADO', 'Fútbol', 4, 'Juan Pérez'),
  ('770e8400-e29b-41d4-a716-446655440002', 1, '2026-06-01 10:00:00', '2026-06-01 11:00:00', 'CONFIRMADO', 'Tenis', 2, 'María López'),
  ('770e8400-e29b-41d4-a716-446655440003', 1, '2026-06-02 11:00:00', '2026-06-02 12:00:00', 'PENDIENTE', 'Basketball', 6, 'Carlos García');
"
```

### Script de Reset de Datos

```bash
#!/bin/bash
# scripts/reset-test-data.sh

set -e

echo "Borrando datos de prueba..."
psql -d sale_partido -c "TRUNCATE TABLE reservas CASCADE;"
psql -d sale_partido -c "TRUNCATE TABLE canchas CASCADE;"
psql -d sale_partido -c "TRUNCATE TABLE locales CASCADE;"

echo "Recreando datos iniciales..."
./mvnw flyway:clean
./mvnw flyway:migrate

echo "✓ Datos de prueba reseteados"
```

---

## Debugging

### Problemas Comunes

#### Problema: "Local no encontrado" (HTTP 404)

**Causa:** Local UUID es incorrecto o no existe

**Solución:**
```bash
# Obtener lista de locales válidos
curl http://localhost:8080/locales | jq '.[] | {uuid, nombre}'

# Usar UUID correcto
```

#### Problema: Calendario no muestra datos

**Causa 1:** Backend no conectado
```bash
# Verificar
curl http://localhost:8080/actuator/health

# Si falla, reiniciar backend
./mvnw spring-boot:run
```

**Causa 2:** Datos de prueba vacíos
```bash
# Verificar si existen canchas
curl http://localhost:8080/canchas | jq length

# Si es 0, crear datos de prueba
bash scripts/reset-test-data.sh
```

**Causa 3:** Rango de fechas inválido
```bash
# Asegurarse de usar formato ISO 8601
# CORRECTO:   2026-06-01
# INCORRECTO: 06/01/2026

# Verificar en URL
curl "http://localhost:8080/locales/.../disponibilidad?fechaInicio=2026-06-01&fechaFin=2026-06-07"
```

#### Problema: Errores JavaScript en console

**Solución:**

1. Abrir DevTools (F12)
2. Ir a "Console" tab
3. Copiar error completo
4. Buscar en logs del backend:
   ```bash
   # Ver logs en tiempo real
   ./mvnw spring-boot:run | grep -i error
   ```

#### Problema: Datos desactualizados en calendar

**Causa:** Cache del navegador o Redux store

**Solución:**
```bash
# Hard refresh
Ctrl+Shift+R (Windows/Linux)
Cmd+Shift+R (Mac)

# O limpiar cache
DevTools → Application → Cache → Clear All

# O reiniciar servidor frontend
npm start
```

### Herramientas de Debugging

#### Redux DevTools (si usa Redux)

```bash
# Instalar extensión en Chrome
# Ver estado global: DevTools → Redux

# Buscar acciones relacionadas a disponibilidad
# Filtrar por: "FETCH_DISPONIBILIDAD"
```

#### Network Tab (DevTools)

```
1. F12 → Network tab
2. Hacer acción (cambiar semana, etc.)
3. Buscar request a /disponibilidad
4. Verificar:
   - Status: 200 ✓
   - Response: JSON válido
   - Time: < 500ms
```

#### Performance Profiling

```bash
# Terminal
# Ver logs con timestamps
npm start 2>&1 | grep -E "Time|Performance|ms"
```

---

## Reportes

### Estructura de Reporte

```markdown
# Reporte QA E5-H01

**Fecha:** [DD/MM/YYYY]
**Ejecutado por:** [Nombre]
**Duración:** [X horas Y minutos]

## Resumen Ejecutivo

- **Escenarios ejecutados:** X
- **Escenarios pasados:** Y
- **Bugs encontrados:** Z
- **Severity:** [ ] CRÍTICO [ ] ALTO [ ] MEDIO

## Resultados Detallados

### Escenario 1: Ver disponibilidad semanal
- **Estado:** ✅ PASS / ❌ FAIL
- **Duración:** X min
- **Bugs:** [Listado]

### Escenario 2: Ver disponibilidad diaria
- **Estado:** ✅ PASS / ❌ FAIL
- **Duración:** X min
- **Bugs:** [Listado]

... (continuar para cada escenario)

## Bugs Identificados

### Bug #1: [Descripción corta]
- **Severity:** CRÍTICO
- **Pasos:** 1. Hacer X, 2. Resultado Y
- **Esperado:** Resultado Z
- **Actual:** Resultado incorrecto
- **Estado:** ABIERTO / CERRADO

## Métricas de Calidad

| Métrica | Valor | Target | Estado |
|---------|-------|--------|--------|
| Cobertura de escenarios | 100% | 100% | ✅ |
| Bugs críticos | 0 | 0 | ✅ |
| Performance (API) | 150ms | <500ms | ✅ |
| Performance (Frontend) | 1.2s | <3s | ✅ |
| Responsive | Funcional | - | ✅ |
| Accesibilidad | Parcial | - | ⚠️ |

## Conclusiones

- Funcionalidad implementada correctamente
- Performance dentro de límites aceptables
- Algunos items de accesibilidad por mejorar

## Próximas Acciones

1. [ ] Corregir bugs críticos
2. [ ] Mejorar accesibilidad
3. [ ] Re-ejecutar pruebas
4. [ ] Preparar para producción
```

### Exportar Reporte

```bash
# Guardar como markdown
cp REPORTE_QA_E5-H01_FINAL.md reporte-ejecutado-2026-05-30.md

# Exportar como PDF (si tienes pandoc)
pandoc reporte-ejecutado-2026-05-30.md -o reporte-ejecutado-2026-05-30.pdf

# Publicar en repo
git add doc/qa/reportes/
git commit -m "docs(QA): Reporte ejecutado 2026-05-30"
git push
```

---

## Contacto y Soporte

**QA Lead:** [nombre]
**Responsable Backend:** [nombre]
**Responsable Frontend:** [nombre]

**Slack:** #qa-e5-h01
**Email:** qa@saleww.com
