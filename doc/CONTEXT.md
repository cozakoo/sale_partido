# 🧠 Contexto Persistente para IAs — Sale Partido

Guía para cualquier IA (Claude, Codex, Cursor, etc.) cómo cargar y usar el contexto del proyecto **Sale Partido**.

---

## 📋 Archivos Esenciales (Leer en Orden)

### 1. **CLAUDE.md** (Project Master)
**Ubicación**: `C:\Users\marti\Documents\Proyectos\sale_partido\CLAUDE.md`

Contiene:
- ✅ Stack tecnológico
- ✅ Estructura del proyecto
- ✅ 9 épicas DDD
- ✅ Arquitectura backend/frontend
- ✅ Docker & Kubernetes
- ✅ CI/CD pipeline
- ✅ Troubleshooting

**Tiempo de lectura**: 20-30 minutos
**Cuándo leer**: SIEMPRE al inicio de la sesión

---

### 2. **memory/MEMORY.md** (Compressed Core Facts)
**Ubicación**: `C:\Users\marti\.claude\projects\C--Users-marti-Documents-Proyectos-sale-partido\memory\MEMORY.md`

Contiene (máx 200 líneas):
- Project name & vision
- Key technologies
- 9 épicas en tabla
- Critical paths (auth, payments, reservations)
- Important decisions
- File structure resume

**Tiempo de lectura**: 5 minutos
**Cuándo leer**: Al inicio, proporciona overview rápido

---

### 3. **doc/AGENTS.md** (Agent Coordination)
**Ubicación**: `C:\Users\marti\Documents\Proyectos\sale_partido\doc\AGENTS.md`

Contiene:
- ✅ 6 agentes especializados
- ✅ Matriz de capacidades
- ✅ MCPs y skills disponibles
- ✅ Memoria persistente estructura
- ✅ Reglas de interacción

**Tiempo de lectura**: 10 minutos
**Cuándo leer**: Si trabajas con múltiples agentes

---

### 4. **agentes/[agente].md** (Specialized Instructions)
**Ubicación**: `C:\Users\marti\Documents\Proyectos\sale_partido\agentes\`

Opciones:
- `backinator.md` — Backend specialist
- `angular-architect.md` — Frontend specialist
- `kubemaster.md` — DevOps specialist
- `qa-sentinel.md` — Testing specialist
- `docsmith.md` — Documentation specialist
- `integrator.md` — CI/CD specialist

**Tiempo de lectura**: 15 minutos cada uno
**Cuándo leer**: Si actúas como ese agente específico

---

### 5. **doc/HISTORY.md** (Architectural Decisions)
**Ubicación**: `C:\Users\marti\Documents\Proyectos\sale_partido\doc\HISTORY.md`

Contiene:
- ✅ ADR (Architectural Decision Records)
- ✅ Decisiones pasadas + contexto
- ✅ Trade-offs documentados
- ✅ Related decisions

**Tiempo de lectura**: 5 minutos
**Cuándo leer**: Si tomas decisiones arquitectónicas

---

## 🚀 Startup Checklist (Para Cualquier IA)

Al iniciar una sesión:

```markdown
- [ ] Leí CLAUDE.md (proyecto)
- [ ] Leí memory/MEMORY.md (overview rápido)
- [ ] Leí doc/AGENTS.md (si necesito coordinación)
- [ ] Leí agentes/[MI-AGENTE].md (si tengo rol específico)
- [ ] Revisé doc/HISTORY.md (decisiones recientes)
- [ ] Entiendo la estructura de 9 épicas DDD
- [ ] Sé dónde encontrar código relevante
- [ ] Entiendo tech stack
- [ ] Sé cuáles son las rutas críticas
```

---

## 🎯 Flujos Comunes

### Scenario 1: Implementar Feature Backend (BACKINATOR)

```
1. Leer: CLAUDE.md (Backend section)
2. Leer: memory/MEMORY.md
3. Leer: agentes/backinator.md
4. Leer: memory/agents/backinator.md (decisiones previas)
5. Localizar épica relevante en CLAUDE.md
6. Entender service layer architecture
7. Check HISTORY.md para ADRs relevantes
8. Implementar siguiendo springboot-patterns skill
9. Actualizar memory/agents/backinator.md
```

### Scenario 2: Implementar Feature Frontend (ANGULAR-ARCHITECT)

```
1. Leer: CLAUDE.md (Frontend section)
2. Leer: memory/MEMORY.md
3. Leer: agentes/angular-architect.md
4. Leer: memory/agents/angular.md
5. Localizar módulo en frontend/src/app/modules/
6. Entender standalone components + signals pattern
7. Revisar guía de accesibilidad (WCAG 2.2)
8. Implementar siguiendo frontend-patterns skill
9. Actualizar memory/agents/angular.md
```

### Scenario 3: Deploy en Kubernetes (KUBEMASTER)

```
1. Leer: CLAUDE.md (K8s section)
2. Leer: memory/MEMORY.md
3. Leer: agentes/kubemaster.md
4. Leer: memory/agents/kubemaster.md
5. Revisar k8s/*.yaml manifests
6. Entender namespaces: salepartido (prod), salepartido-preprod (staging)
7. Check HISTORY.md para ADRs sobre K8s
8. Actualizar manifests según cambios
9. Actualizar memory/agents/kubemaster.md
```

---

## 🧠 Memoria Persistente — Cómo Funciona

### Carpeta de Memoria

```
C:\Users\marti\.claude\projects\C--Users-marti-Documents-Proyectos-sale-partido\memory\
├── MEMORY.md                  # Core facts (master file)
├── agents/
│   ├── backinator.md         # Backend decisions
│   ├── angular.md            # Frontend decisions
│   ├── kubemaster.md         # K8s decisions
│   └── testing.md            # Test strategy
└── architecture/
    ├── ddd-structure.md      # 9 épicas
    ├── api-design.md         # REST patterns
    └── db-schema.md          # Entity relationships
```

### Cómo Leerla

**Opción 1: Desde Claude Code**
```bash
# Ver MEMORY.md
cat ~/.claude/projects/C--Users-marti-Documents-Proyectos-sale-partido/memory/MEMORY.md

# Buscar en memoria con mem-search skill
/mem-search pattern: "JWT authentication"
```

**Opción 2: Desde Claude Web (claude.ai)**
- Los archivos en `memory/` se cargan automáticamente
- Ver instrucciones del proyecto "Sale Partido"

---

## ✏️ Cómo Actualizar Memoria

### Después de Decisión Importante

1. **Leer** el archivo relevante en `memory/agents/[agente].md`
2. **Agregar** nueva entrada con formato:

```markdown
## [Decisión] Título

**Fecha**: 2026-05-16
**Agente**: BACKINATOR
**Contexto**: Por qué es importante
**Decisión**: Qué se decidió y por qué
**Impacto**: Qué código/módulos afecta
**ADR**: Link a doc/HISTORY.md si es architectural
```

3. **NUNCA** modificar `MEMORY.md` directamente sin coordinación
4. **Siempre** crear ADR si es arquitectónico
5. **Actualizar** al terminar la sesión

---

## 🔍 Búsqueda de Contexto

### Usar mem-search Skill

```bash
# Buscar decisiones sobre caché
/mem-search query: "Redis cache strategy"

# Buscar patterns sobre testing
/mem-search query: "TDD test pyramid"

# Buscar decisiones de un agente
/mem-search query: "BACKINATOR JWT"
```

### Usar Grep Manual

```bash
# En CLAUDE.md
grep -n "Redis\|Cache" CLAUDE.md

# En agentes
grep -r "decision\|Decision" agentes/

# En doc
grep -r "ADR\|Architectural" doc/
```

---

## 📝 Convenciones

### Nombres de Variables (Código)

```
Backend (Java):
- Classes: PascalCase (UserService)
- Methods: camelCase (getUser)
- Constants: UPPER_SNAKE_CASE (MAX_RETRIES)
- Packages: lowercase.with.dots (com.saleww.domain.exploration)

Frontend (TypeScript):
- Classes: PascalCase (UserComponent)
- Methods: camelCase (getUser)
- Constants: UPPER_SNAKE_CASE (MAX_RETRIES)
- Interfaces: PascalCase (User)
```

### Commits

```
Format: <type>(<scope>): <description>
Types: feat, fix, refactor, docs, test, chore, perf, ci
Scope: [E#-H##] (Epic-History)

Example:
feat(E1-H03): implement advanced search filters
fix(E5-H21): cancel reservation edge case
docs(E9): update analytics API
```

---

## 🚨 Critical Paths (Alta Prioridad)

Estos son críticos para el negocio:

| Path | Owner | Link |
|------|-------|------|
| **Authentication** | BACKINATOR | `domain/shared/security` |
| **Payments** | BACKINATOR | `domain/payments` |
| **Reservations** | BACKINATOR | `domain/reservations` |
| **User Dashboard** | ANGULAR-ARCHITECT | `modules/dashboard` |
| **K8s Deployment** | KUBEMASTER | `k8s/` |

---

## 📞 Contactos Rápidos

**Si trabajo en...**

- Backend: Leer `agentes/backinator.md` + `domain/[epic]/`
- Frontend: Leer `agentes/angular-architect.md` + `modules/[epic]/`
- K8s: Leer `agentes/kubemaster.md` + `k8s/`
- Testing: Leer `agentes/qa-sentinel.md` + `**/test/`
- Docs: Leer `agentes/docsmith.md` + `doc/`
- Git: Leer `agentes/integrator.md` + `doc/GITHUB.md`

---

## 🎯 Reglas de Oro para IAs

1. **Siempre comenzar con CLAUDE.md** — Fuente de verdad
2. **No modificar CLAUDE.md** — Contactar con Martin si necesario
3. **Actualizar memory/ después de decisiones** — Persistencia
4. **Crear ADR en doc/HISTORY.md** — Si es arquitectónico
5. **Consultar mem-search** — Antes de implementar
6. **Coordinar entre agentes** — Actualizar en doc/HISTORY.md
7. **Documentar** — Especialmente APIs y decisiones
8. **Testing primero** — TDD siempre

---

## 📚 Carpetas Clave

```
sale-partido/
├── CLAUDE.md                  # 👈 START HERE
├── doc/
│   ├── AGENTS.md             # Coordinación
│   ├── HISTORY.md            # ADRs
│   └── CONTEXT.md            # Este archivo
├── agentes/                   # Instrucciones por agente
│   ├── backinator.md
│   ├── angular-architect.md
│   ├── kubemaster.md
│   ├── qa-sentinel.md
│   ├── docsmith.md
│   └── integrator.md
├── backend/src/main/java/com/saleww/domain/
│   ├── exploration/           # E1
│   ├── reservations/          # E5
│   └── [8 more epics]/
├── frontend/src/app/modules/
│   ├── exploration/
│   ├── reservations/
│   └── [8 more epics]/
└── k8s/
    ├── 00-namespace.yaml
    ├── 05-backend.yaml
    └── [other manifests]/
```

---

## ✅ Session Startup Template

Copiar y usar al iniciar sesión:

```markdown
## 🤖 AI Agent Session Start — Sale Partido

### Contexto Cargado
- [x] CLAUDE.md (proyecto)
- [x] memory/MEMORY.md (facts)
- [x] doc/AGENTS.md (coordinación)
- [x] agentes/[ROLE].md (instrucciones)
- [x] doc/HISTORY.md (ADRs)

### Rol Actual
**Agente**: [BACKINATOR|ANGULAR-ARCHITECT|KUBEMASTER|QA-SENTINEL|DOCSMITH|INTEGRATOR]

### Tarea
[Describir qué voy a hacer]

### Criterios de Éxito
- [ ] Criterio 1
- [ ] Criterio 2
- [ ] Tests pasan (si aplica)
- [ ] Documentación actualizada
- [ ] memory/agents/[role].md actualizada
```

---

**Sale Partido — Contexto persistente para IAs coordinadas** 🧠

*Última actualización: 2026-05-16*
