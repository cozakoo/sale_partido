# GitHub Projects — Gestión de Historias de Usuario

**Cómo organizamos el trabajo y cómo las IAs deben consultarlo.**

---

## 📊 Tablero

**URL:** https://github.com/users/cozakoo/projects/2/views/3

Este tablero contiene:
- **Historias de Usuario (HUs)** — Requisitos funcionales
- **Épicas (E1-E9)** — Features grandes
- **Tasks** — Trabajo técnico
- **Estados:** Backlog → Todo → In Progress → In Review → Done

---

## 🏷️ Naming Convention

Cada HU tiene identificador único: `E#-H##`

**Ejemplo:** `E4-H08` = Épica 4, Historia Usuario 08

```
E4-H08 — Crear local con validaciones

Épica: E4 (Gestión Espacios)
HU: 08 (octava historia de usuario en E4)
```

**Mapa actual:**

| Épica | Range | Ejemplos |
|-------|-------|----------|
| **E1** (Exploración) | E1-H01 a E1-H10 | E1-H03: Vista en mapa |
| **E2** (Participación) | E2-H01 a E2-H10 | E2-H05: Sistema reseñas |
| **E3** (Eventos) | E3-H01 a E3-H10 | E3-H02: Crear evento |
| **E4** (Espacios) | E4-H01 a E4-H15 | E4-H08: Crear local |
| **E5** (Reservas) | E5-H01 a E5-H10 | E5-H06: Cancelar reserva |
| **E6** (Notificaciones) | E6-H01 a E6-H10 | E6-H04: Email notification |
| **E7** (Pagos) | E7-H01 a E7-H10 | E7-H03: Registrar pago |
| **E8** (Competencias) | E8-H01 a E8-H10 | E8-H05: Ranking |
| **E9** (Analytics) | E9-H01 a E9-H10 | E9-H02: Reportes |

---

## 📋 Estructura de una HU

Cada HU en GitHub Projects tiene:

### Título
```
[E4-H08] Crear local con validaciones
```

### Descripción
```markdown
## Contexto
El sistema necesita permitir a propietarios crear espacios.

## Requisitos Funcionales
- POST /locales endpoint
- Validar campos: nombre, ubicación, ciudad
- Horarios predeterminados (8-22h)
- Retornar UUID del local creado

## Criterios de Aceptación
- [ ] Endpoint POST /locales retorna 201
- [ ] Validación de campos obligatorios
- [ ] Tests: ≥85% coverage
- [ ] SonarQube sin bloqueadores
- [ ] PR aprobada

## Technical Notes
- Usar Spring Data JPA
- Exception: LocalCreationException si falla
- Caché: invalidar lista de locales
```

### Fields
- **Epic:** E4
- **Status:** Todo / In Progress / In Review / Done
- **Priority:** High / Medium / Low
- **Owner:** Developer name
- **Linked PR:** #123

---

## 🔄 Flujo: HU → Código → PR → Done

### HU creada en GitHub Projects
```
Estado: Backlog (sin asignar)
```

### Developer la toma
```
Estado: Todo → In Progress
Owner: Asignarse
Checklist: Leer acceptance criteria
```

### Crear rama
```bash
# Naming: feature/[E#-H##]-descripcion-corta
git checkout -b feature/E4-H08-crear-local
```

**Link a HU:** Usar naming convention E#-H## en branch name

### Consultar agente IA
```
1. Abre /agentes/backend.md (o frontend.md)
2. Copia TODO el contenido
3. Pégalo en tu IA (Claude, ChatGPT, etc)
4. Pide que implemente la HU (dale contexto: nombre HU, descripción, acceptance criteria)
```

### IA codifica
- Respeta SOLID
- Tests: 80%+ coverage
- Sigue antipatrones prohibidos
- Usa `/agentes/API_CONTRACTS.md` para coordinar con frontend

### Tests local
```bash
./mvnw verify        # Backend
npm run test         # Frontend
```

**Si falla:** Pide a IA que corrija

### Commit
```bash
git commit -m "feat(E4-H08): crear local con validaciones

- POST /locales endpoint
- Validar campos obligatorios
- Tests: 85% coverage

Fixes: cozakoo/sale-partido#123"
```

**"Fixes:" link:** Cierra la HU automáticamente cuando PR merge

### PR en GitHub
```
Título: [E4-H08] Crear local con validaciones
Descripción:
- Qué cambió
- Cómo testear
- Acceptance criteria met: ✅
Body:
Fixes #123
```

**Link a HU:** Usar "Fixes #123" (123 es el issue number)

### Code Review
- Mínimo 2 aprobaciones
- CI/CD debe pasar ✅
- Acceptance criteria: ✅ completadas

### 🔟 Merge a dev
```
Estado: In Review → Done
HU se cierra automáticamente (por "Fixes")
```

---

## 📖 Cómo las IAs deben leer una HU

### Paso 1: Copiar descripción
Desde GitHub Projects, ve a la HU y copia:
```
[E4-H08] Crear local con validaciones

**Contexto:** ...
**Requisitos Funcionales:**
- ...
**Criterios de Aceptación:**
- ...
```

### Paso 2: Dar contexto a IA
```
Necesito implementar esta Historia de Usuario:

[E4-H08] Crear local con validaciones
...

Aquí está el agente Backend:
[copiar TODO de /agentes/backend.md]

Implementa siguiendo:
- SOLID principles
- Tests: 85% coverage
- Acceptance criteria
```

### Paso 3: IA implementa
IA tendrá:
- Contexto de la HU ✅
- Contexto del proyecto (agente) ✅
- API contracts (si necesita Frontend) ✅

---

## 🎯 Aceptance Criteria Template

Usar este template para TODAS las HUs:

```markdown
## Criterios de Aceptación

### Funcional
- [ ] Endpoint responde con status correcto
- [ ] Validaciones funcionan
- [ ] Datos se guardan en BD
- [ ] Respuesta tiene estructura correcta

### Testing
- [ ] Tests unitarios: >80% coverage
- [ ] Tests integración: BD real
- [ ] E2E tests: flujo completo
- [ ] No hay console.log ni debug

### Calidad
- [ ] SonarQube: sin bloqueadores
- [ ] Lint: sin errores
- [ ] Build: exitoso
- [ ] Commit message: Conventional Commits

### Seguridad
- [ ] Input validation
- [ ] No hardcodeo de valores
- [ ] No secrets en código

### Documentation
- [ ] Tests tienen nombres descriptivos
- [ ] Código tiene comentarios si es complejo
- [ ] PR descripción clara
```

---

## 🔗 Integración GitHub Projects ↔ Branches ↔ PRs

```
GitHub Projects HU
    ↓
    Naming: E#-H##
    ↓
Branch name: feature/E#-H##-descripcion
    ↓
Commit: Conventional + referencia E#
    ↓
PR: Título con [E#-H##]
    ↓
"Fixes #<issue_number>" cierra HU
    ↓
GitHub Projects: automáticamente → Done
```

**Ejemplo:**

| Elemento | Valor |
|----------|-------|
| **GitHub Projects HU** | E4-H08 (issue #123) |
| **Branch** | `feature/E4-H08-crear-local` |
| **Commit** | `feat(E4-H08): crear local...` |
| **PR Title** | `[E4-H08] Crear local con validaciones` |
| **PR Body** | `Fixes #123` |
| **Resultado** | HU pasa a Done automáticamente |

---

## 📊 Estados en GitHub Projects

| Estado | Qué significa | Acción |
|--------|---------------|--------|
| **Backlog** | No empezado | Espera a ser asignado |
| **Todo** | Listo para empezar | Dev toma la HU |
| **In Progress** | Desarrollando | Dev abierto en rama feature |
| **In Review** | PR abierto | Esperando aprobaciones |
| **Done** | Completado | Merged a dev |

**Automático:** "Fixes #123" en PR → pasa a Done cuando mergea

---

## 🤖 Para IAs: Checklist antes de codificar

- [ ] Tengo la descripción completa de la HU
- [ ] Entiendo acceptance criteria
- [ ] He copiado el agente correspondiente (backend/frontend/infra)
- [ ] Entiendo la épica (E1-E9) y su contexto
- [ ] Sé qué módulo toco (domain/[epic]/ en backend)
- [ ] Consulté `/agentes/API_CONTRACTS.md` si hay coordinación
- [ ] Sé cuándo terminé: cuando acceptance criteria se marcan ✅

---

## 📌 Tips

**Para developers:**
1. Toma HU de GitHub Projects
2. Asígnate
3. Lee acceptance criteria
4. Copia agente IA
5. Pide que implemente HU
6. Tests locales
7. Commit + PR con "Fixes #<numero>"
8. 2 aprobaciones
9. Merge → HU pasa a Done automáticamente

**Para IAs:**
1. Pide que copien descripción de la HU
2. Lee acceptance criteria
3. Implementa respetando agente
4. Tests: coverage requerido
5. Commit message: "feat(E#): descripción"

---

## 🔍 Ver estado actual

**GitHub Projects:** https://github.com/users/cozakoo/projects/2/views/3

Columnas:
- **Backlog** — Sin asignar
- **Todo** — Listo, sin asignar
- **In Progress** — Asignado, desarrollando
- **In Review** — PR abierto
- **Done** — Completado

---

## Recursos

- **GitHub Projects:** https://github.com/users/cozakoo/projects/2
- **Issues (HUs):** https://github.com/cozakoo/sale-partido/issues
- **PRs:** https://github.com/cozakoo/sale-partido/pulls
- **Agentes:** `/agentes/`
