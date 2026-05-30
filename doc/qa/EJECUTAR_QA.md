# Ejecutar Pruebas de QA - E5-H01

## Inicio Rápido (5 minutos)

```bash
# 1. Setup automático
bash ./doc/qa/scripts/setup-local.sh

# 2. Abrir navegador
open http://localhost:4200

# 3. Navegar a disponibilidad
# Ir a: Locales → Cancha 1 → Disponibilidad
```

---

## Ejecución Manual Paso a Paso

### Fase 1: Verificación del Entorno (10 min)

#### 1.1 Verificar Backend

```bash
# Terminal 1: Backend
cd backend
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Esperar hasta:
# "Started Application in X.XXX seconds"
```

**Validación:**
```bash
curl http://localhost:8080/actuator/health
# Esperado: {"status":"UP"}

curl http://localhost:8080/locales
# Debe retornar lista de locales
```

#### 1.2 Verificar Frontend

```bash
# Terminal 2: Frontend
cd frontend
npm start

# Esperar hasta:
# "Local:   http://localhost:4200/"
```

**Validación:**
- Abrir http://localhost:4200 en navegador
- Debe cargar sin errores
- Ver lista de locales en página principal

---

### Fase 2: Pruebas Funcionales (30 min)

#### Escenario 1: Ver disponibilidad semanal

1. **Navegar a disponibilidad**
   - Ir a cualquier local
   - Click en "Cancha 1"
   - Click en "Ver disponibilidad"

2. **Verificar calendario semanal**
   - [ ] Se muestra semana actual (Lunes-Domingo)
   - [ ] Cada día muestra fecha
   - [ ] Hay bloques de horarios

3. **Verificar turnos libres**
   - [ ] Bloques verdes = LIBRE
   - [ ] Al pasar mouse: muestra "Disponible"
   - [ ] Cantidad coincide con datos

4. **Verificar turnos ocupados**
   - [ ] Bloques rojos = OCUPADO
   - [ ] Al pasar mouse: muestra info del evento
   - [ ] Nombre organizador visible

5. **Navegación semanal**
   - [ ] Click "Siguiente semana" → actualiza calendario
   - [ ] Click "Semana anterior" → actualiza calendario
   - [ ] No permite ir a fecha pasada

**Registro:**
```
Escenario 1 - Ver disponibilidad semanal: [ ] PASS [ ] FAIL
Notas: _____________________________________________
```

---

#### Escenario 2: Ver disponibilidad diaria

1. **Cambiar a vista diaria**
   - Click en un día específico del calendario
   - O click en "Ver hoy"

2. **Verificar vista diaria**
   - [ ] Se muestra solo 1 día
   - [ ] Bloques separados por hora (09:00, 10:00, etc.)
   - [ ] Duración de cada bloque = 1 hora

3. **Hover sobre turnos**
   - [ ] Muestra tooltip con hora
   - [ ] Muestra tooltip con estado (LIBRE/OCUPADO)
   - [ ] Si OCUPADO: muestra nombre organizador

4. **Horarios fuera de servicio**
   - [ ] 00:00-08:59 → gris/deshabilitado
   - [ ] 23:00-23:59 → gris/deshabilitado
   - [ ] Razonable según config local

**Registro:**
```
Escenario 2 - Ver disponibilidad diaria: [ ] PASS [ ] FAIL
Notas: _____________________________________________
```

---

#### Escenario 3: Ver detalle de turno

1. **Hacer clic en turno ocupado**
   - Click en bloque rojo (OCUPADO)

2. **Verificar modal de detalles**
   - [ ] Modal se abre sin errores
   - [ ] Muestra "Detalles del turno"

3. **Validar campos:**
   - [ ] Nombre organizador: visible y correcto
   - [ ] Deporte: ej. "Fútbol", "Tenis", etc.
   - [ ] Participantes confirmados: número >= 0
   - [ ] Estado del evento: "CONFIRMADO", "PENDIENTE", etc.
   - [ ] Hora: HH:MM correctamente formateada
   - [ ] Fecha: DD/MM/YYYY correctamente formateada

4. **Cerrar modal**
   - [ ] Click "Cerrar" o "X" cierra modal
   - [ ] Retorna a calendario normal

**Registro:**
```
Escenario 3 - Ver detalle de turno: [ ] PASS [ ] FAIL
Datos capturados:
- Organizador: _____________________________
- Deporte: _________________________________
- Participantes: ____________________________
- Estado: _________________________________
Notas: _____________________________________________
```

---

#### Escenario 4: Historial de turnos

1. **Navegar a historial**
   - Click en "Ver historial" (si existe)
   - O ir a `/locales/{id}/historial`

2. **Verificar lista de turnos históricos**
   - [ ] Se muestra lista de turnos pasados
   - [ ] Ordenados por fecha (más reciente primero)
   - [ ] Al menos 10 turnos visibles

3. **Hacer clic en un turno**
   - Click en turno finalizado

4. **Verificar detalles históricos**
   - [ ] Nombre organizador visible
   - [ ] Deporte mostrado
   - [ ] Cantidad de asistentes visible
   - [ ] Fecha es anterior a hoy

5. **Validar paginación** (si hay >10 turnos)
   - [ ] "Siguiente página" disponible
   - [ ] "Página anterior" funciona
   - [ ] Total de turnos mostrado

**Registro:**
```
Escenario 4 - Historial de turnos: [ ] PASS [ ] FAIL
Turnos visualizados: _____
Rango de fechas: _____ a _____
Notas: _____________________________________________
```

---

### Fase 3: Validaciones Técnicas (20 min)

#### 3.1 Backend API

```bash
# Terminal 3: Tests API

# Test 1: Disponibilidad válida
echo "=== Test 1: Disponibilidad válida ==="
curl -s "http://localhost:8080/locales/550e8400-e29b-41d4-a716-446655440000/disponibilidad?fechaInicio=2026-06-01&fechaFin=2026-06-07" | jq .

# Test 2: Local no encontrado
echo "=== Test 2: Local inválido ==="
curl -s "http://localhost:8080/locales/999999-invalid/disponibilidad?fechaInicio=2026-06-01&fechaFin=2026-06-07" | jq .

# Test 3: Fecha inválida
echo "=== Test 3: Fecha inválida ==="
curl -s "http://localhost:8080/locales/550e8400-e29b-41d4-a716-446655440000/disponibilidad?fechaInicio=invalid&fechaFin=2026-06-07" | jq .

# Test 4: Rango > 31 días
echo "=== Test 4: Rango excede 31 días ==="
curl -s "http://localhost:8080/locales/550e8400-e29b-41d4-a716-446655440000/disponibilidad?fechaInicio=2026-01-01&fechaFin=2026-12-31" | jq .
```

**Checklist API:**
- [ ] Test 1: HTTP 200, retorna datos válidos
- [ ] Test 2: HTTP 404 con mensaje descriptivo
- [ ] Test 3: HTTP 400 con mensaje descriptivo
- [ ] Test 4: HTTP 400 con mensaje sobre límite de días

#### 3.2 Frontend Console

1. **Abrir DevTools:** F12 o Cmd+Option+I
2. **Ir a pestaña "Console"**
3. **Buscar errores:**
   - [ ] No hay errores rojos (console.error)
   - [ ] No hay warnings críticos
   - [ ] Manejo de datos correcto

**Registro:**
```
Errores encontrados: ___________________
Warnings: ____________________________
Estado: [ ] PASS [ ] FAIL
```

#### 3.3 Performance

1. **DevTools → Performance tab**
2. **Grabar mientras:**
   - Cargar página disponibilidad
   - Cambiar de semana
   - Hacer clic en turno

**Métricas esperadas:**
- [ ] First Contentful Paint (FCP) < 2s
- [ ] Largest Contentful Paint (LCP) < 3s
- [ ] Interaction to Next Paint (INP) < 200ms

---

### Fase 4: Responsive Design (15 min)

#### Escritorio (1920px)
1. Abrir Chrome DevTools
2. Click en device toggle (Ctrl+Shift+M)
3. Seleccionar "Desktop"

**Validación:**
- [ ] Calendario ocupa ~80% de ancho
- [ ] Botones accesibles
- [ ] No hay scroll horizontal

#### Tablet (768px)
1. Device toggle → "iPad"

**Validación:**
- [ ] Calendario adapta a ancho
- [ ] Bloque de turnos legible
- [ ] Touch targets >= 44px

#### Móvil (375px)
1. Device toggle → "iPhone SE"

**Validación:**
- [ ] Calendario es scrolleable verticalmente
- [ ] Una columna de días
- [ ] Botones accesibles con dedo

**Registro:**
```
Escritorio (1920px): [ ] PASS [ ] FAIL
Tablet (768px):      [ ] PASS [ ] FAIL
Móvil (375px):       [ ] PASS [ ] FAIL
Notas: _____________________________________________
```

---

### Fase 5: Accesibilidad (10 min)

1. **Navegación con teclado**
   - [ ] Tab navega por elementos
   - [ ] Enter abre detalles de turno
   - [ ] Escape cierra modales
   - [ ] Arrows cambian de día/semana

2. **Colores y contraste**
   - [ ] Verde (LIBRE) vs blanco: contraste suficiente
   - [ ] Rojo (OCUPADO) vs blanco: contraste suficiente
   - [ ] Texto legible en todos los tamaños

3. **Lectores de pantalla**
   - [ ] Botones tienen aria-label
   - [ ] Turnos tienen descripción de contenido
   - [ ] Modales anunciados correctamente

**Registro:**
```
Navegación teclado: [ ] PASS [ ] FAIL
Contraste colores:  [ ] PASS [ ] FAIL
Lectores pantalla:  [ ] PASS [ ] FAIL
Notas: _____________________________________________
```

---

## Registro de Bugs Encontrados

```markdown
### Bug #1: [Título del bug]

**Severidad:** [ ] CRÍTICO [ ] ALTO [ ] MEDIO [ ] BAJO

**Pasos para reproducir:**
1. ...
2. ...
3. ...

**Resultado actual:**
[Descripción]

**Resultado esperado:**
[Descripción]

**Evidencia:**
- Screenshot: [ruta]
- Video: [ruta]
- Logs: [ruta]

**Asignado a:** [nombre]
**Estado:** [ ] ABIERTO [ ] EN PROGRESO [ ] CERRADO
```

---

## Resumen de Ejecución

| Escenario | Estado | Bugs | Notas |
|-----------|--------|------|-------|
| 1. Disponibilidad semanal | [ ] | [ ] | |
| 2. Disponibilidad diaria | [ ] | [ ] | |
| 3. Detalle de turno | [ ] | [ ] | |
| 4. Historial | [ ] | [ ] | |
| API Backend | [ ] | [ ] | |
| Frontend Console | [ ] | [ ] | |
| Performance | [ ] | [ ] | |
| Responsive | [ ] | [ ] | |
| Accesibilidad | [ ] | [ ] | |

**Resultado Final:** [ ] PASS [ ] FAIL CON BUGS [ ] FAIL CRÍTICO

**Fecha ejecución:** _______________
**Ejecutado por:** _______________
**Duración total:** _______________
