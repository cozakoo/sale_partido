# 🤖 Sistema de Agentes IA Especializados — Sale Partido

> Central de coordinación y documentación de agentes IA para desarrollo, testing, deployment y documentación.

---

## 📋 Tabla de Contenidos

1. [Visión General](#visión-general)
2. [Agentes Especializados](#agentes-especializados)
3. [Matriz de Capacidades](#matriz-de-capacidades)
4. [MCPs y Skills Disponibles](#mcps-y-skills-disponibles)
5. [Memoria y Contexto Persistente](#memoria-y-contexto-persistente)
6. [Reglas de Interacción](#reglas-de-interacción)
7. [Configuración por Agente](#configuración-por-agente)

---

## 🎯 Visión General

El proyecto **Sale Partido** utiliza múltiples agentes IA especializados, cada uno con:
- ✅ **Contexto específico**: Entiende su dominio (Backend/Frontend/K8s/Docs)
- ✅ **Memoria persistente**: Registra decisiones y contexto en `memory/`
- ✅ **Tools + Skills**: MCPs y skills especializados por área
- ✅ **Historial**: ADR en `doc/HISTORY.md`

### Flujo de Trabajo

```
Usuario → Agente Orquestador (Claude)
    ↓
    ├→ BACKINATOR (Backend Java/Spring)
    ├→ ANGULAR-ARCHITECT (Frontend Angular)
    ├→ KUBEMASTER (K8s/DevOps)
    ├→ QA-SENTINEL (Testing/QA)
    ├→ DOCSMITH (Documentation)
    └→ INTEGRATOR (CI/CD/Git)

Todos → Memoria Compartida (`memory/`)
```

---

## 🎭 Agentes Especializados

### 1. **BACKINATOR** — Backend Java/Spring Specialist
**Archivo**: `agentes/backinator.md`

| Propiedad | Valor |
|-----------|-------|
| **Rol** | Arquitecto de Backend, experto en Spring Boot 3.x |
| **Responsabilidades** | Diseño de servicios, JPA/Hibernate, testing con Mockito |
| **Modules** | `backend/src/main/java/com/saleww/domain/` |
| **Skills** | `springboot-patterns`, `springboot-tdd`, `springboot-verification` |
| **MCPs** | filesystem (lectura código), GitHub (PRs), Context7 (Spring docs) |
| **Testing** | JUnit 5, Mockito, MockMvc (80%+ coverage) |
| **Interfaz** | Claude Code → `/backinator` (custom slash command) |

**Especialidades:**
- ✅ Domain-Driven Design (9 épicas)
- ✅ REST API design + OpenAPI/Swagger
- ✅ JPA/Hibernate optimización
- ✅ Security: JWT (HS256), CORS, rate limiting
- ✅ Caché con Redis
- ✅ Migration management (Flyway)
- ✅ SonarQube integration

---

### 2. **ANGULAR-ARCHITECT** — Frontend Angular Specialist
**Archivo**: `agentes/angular-architect.md`

| Propiedad | Valor |
|-----------|-------|
| **Rol** | Arquitecto Frontend, experto en Angular 20 + TypeScript |
| **Responsabilidades** | Componentes, state management con signals, forms, routing |
| **Modules** | `frontend/src/app/` |
| **Skills** | `frontend-patterns`, `web-patterns`, `frontend-design` |
| **MCPs** | filesystem (lectura código), GitHub (PRs), Context7 (Angular docs) |
| **Testing** | Jasmine, Karma, Playwright E2E |
| **Interfaz** | Claude Code → `/angular-dev` |

**Especialidades:**
- ✅ Standalone components
- ✅ Signals + computed values
- ✅ Forms reactivos
- ✅ RxJS y observables
- ✅ Guards + interceptors
- ✅ Responsive design (Tailwind CSS)
- ✅ Accessibility (WCAG 2.2)

---

### 3. **KUBEMASTER** — K8s/DevOps Specialist
**Archivo**: `agentes/kubemaster.md`

| Propiedad | Valor |
|-----------|-------|
| **Rol** | Arquitecto de infraestructura, experto en Kubernetes |
| **Responsabilidades** | Manifests K8s, scaling, networking, monitoring |
| **Modules** | `k8s/` |
| **Skills** | `kubernetes` (custom), `docker-patterns`, `cicd-patterns` |
| **MCPs** | filesystem (K8s manifests), bash (kubectl commands) |
| **Herramientas** | kubectl, kubectx, kustomize |
| **Interfaz** | Claude Code → `/kubemaster` |

**Especialidades:**
- ✅ Deployments, StatefulSets, Ingress
- ✅ ConfigMaps + Secrets
- ✅ Resource limits + HPA (Auto-scaling)
- ✅ Liveness/readiness probes
- ✅ Multi-namespace orchestration
- ✅ Helm charts
- ✅ Monitoring (Prometheus/Grafana)

---

### 4. **QA-SENTINEL** — Testing & Quality Specialist
**Archivo**: `agentes/qa-sentinel.md`

| Propiedad | Valor |
|-----------|-------|
| **Rol** | QA Lead, experto en testing strategy |
| **Responsabilidades** | Unit tests, integration tests, E2E, coverage |
| **Testing Stack** | JUnit 5, Mockito, Jasmine, Karma, Playwright |
| **Skills** | `springboot-tdd`, `e2e-testing`, `pytest-patterns` |
| **MCPs** | filesystem (test files), bash (test runners) |
| **Interfaz** | Claude Code → `/qa-sentinel` |

**Especialidades:**
- ✅ TDD (Red-Green-Refactor)
- ✅ Test pyramid (Unit/Integration/E2E)
- ✅ Coverage tracking (JaCoCo)
- ✅ Flaky test detection
- ✅ Performance testing
- ✅ Security testing (OWASP)

---

### 5. **DOCSMITH** — Documentation Specialist
**Archivo**: `agentes/docsmith.md`

| Propiedad | Valor |
|-----------|-------|
| **Rol** | Technical writer + Architect |
| **Responsabilidades** | README, API docs, guides, ADR |
| **Modules** | `doc/`, `*.md` files |
| **Skills** | `doc-updater`, `code-tour` |
| **MCPs** | filesystem (docs), GitHub (PR docs) |
| **Interfaz** | Claude Code → `/docsmith` |

**Especialidades:**
- ✅ Swagger/OpenAPI generation
- ✅ Architecture Decision Records (ADR)
- ✅ Code tours (for onboarding)
- ✅ README + setup guides
- ✅ Deployment guides

---

### 6. **INTEGRATOR** — CI/CD & Git Workflow Specialist
**Archivo**: `agentes/integrator.md`

| Propiedad | Valor |
|-----------|-------|
| **Rol** | DevOps Engineer, Git workflow expert |
| **Responsabilidades** | CI/CD pipelines, GitHub Actions, version management |
| **Modules** | `.github/workflows/` |
| **Skills** | `prp-commit`, `prp-pr` |
| **MCPs** | GitHub (repos, PRs, issues), bash (git commands) |
| **Interfaz** | Claude Code → `/integrator` |

**Especialidades:**
- ✅ GitHub Actions workflows
- ✅ Semantic versioning
- ✅ Git conventional commits
- ✅ Branch strategy
- ✅ Release management

---

## 📊 Matriz de Capacidades

| Tarea | BACKINATOR | ANGULAR-ARCHITECT | KUBEMASTER | QA-SENTINEL | DOCSMITH | INTEGRATOR |
|-------|------------|-------------------|------------|-------------|----------|-----------|
| Backend feature | ✅✅✅ | ❌ | ❌ | ⚠️ | ❌ | ⚠️ |
| Frontend feature | ❌ | ✅✅✅ | ❌ | ⚠️ | ❌ | ⚠️ |
| K8s deployment | ❌ | ❌ | ✅✅✅ | ❌ | ❌ | ⚠️ |
| Test strategy | ⚠️ | ⚠️ | ❌ | ✅✅✅ | ❌ | ❌ |
| Documentation | ⚠️ | ⚠️ | ⚠️ | ❌ | ✅✅✅ | ⚠️ |
| CI/CD pipeline | ⚠️ | ⚠️ | ⚠️ | ❌ | ❌ | ✅✅✅ |
| Security review | ✅ | ✅ | ✅ | ✅ | ❌ | ⚠️ |
| Code review | ✅ | ✅ | ⚠️ | ✅ | ❌ | ⚠️ |

**Leyenda:**
- ✅✅✅ = Experto (principal)
- ✅ = Competente (puede ayudar)
- ⚠️ = Básico (delegue)
- ❌ = No aplicable

---

## 🔧 MCPs y Skills Disponibles

### MCPs Utilizados (Model Context Protocol)

| MCP | Agentes | Uso |
|-----|---------|-----|
| **Filesystem** | Todos | Lectura/escritura de código, documentos |
| **GitHub** | INTEGRATOR, Todos | Repos, PRs, issues, workflows |
| **Context7** | BACKINATOR, ANGULAR-ARCHITECT | Docs oficiales de Spring/Angular |
| **Bash** | KUBEMASTER, QA-SENTINEL, INTEGRATOR | CLI tools (kubectl, git, mvn, npm) |

### Skills Especializados

```yaml
# Backend
springboot-patterns:
  - Layered architecture
  - Dependency injection
  - Exception handling
  - Logging best practices

springboot-tdd:
  - Test-driven development
  - JUnit 5 patterns
  - Mockito usage
  - Integration testing

# Frontend
frontend-patterns:
  - Standalone components
  - State management (signals)
  - Service layer
  - Guard & interceptor patterns

web-patterns:
  - Responsive design
  - CSS custom properties
  - Animation best practices
  - Accessibility (WCAG 2.2)

# Testing
e2e-testing:
  - Playwright patterns
  - Page object model
  - Test isolation
  - Screenshot testing

# Documentation
code-tour:
  - Interactive walkthroughs
  - Onboarding guides
  - Architecture visualization
```

---

## 💾 Memoria y Contexto Persistente

### Estructura de Memoria

```
memory/
├── MEMORY.md              # Core project facts (max 200 lines)
├── agents/
│   ├── backinator.md      # Backend decisions
│   ├── angular.md         # Frontend decisions
│   ├── kubemaster.md      # K8s decisions
│   └── testing.md         # Test strategy
├── architecture/
│   ├── ddd-structure.md   # Epic structure
│   ├── api-design.md      # REST patterns
│   └── db-schema.md       # Entity relationships
└── decisions/
    ├── tech-stack.md      # Why Java/Angular/K8s
    └── patterns.md        # Design patterns used
```

### Cómo Cada Agente Consulta Memoria

1. **Al inicio**: Lee `MEMORY.md` + su archivo especializado
2. **Durante trabajo**: Busca contexto relevante con `mem-search` skill
3. **Al terminar**: Actualiza su archivo con nuevos aprendizajes
4. **Nunca**: Modifica MEMORY.md sin coordinación

### Formato de Entrada en Memoria

```markdown
# [Tipo] Título

**Contexto**: ¿Por qué es importante?
**Decisión**: ¿Qué se decidió?
**Evidencia**: Links a código/docs/ADR
**Impacto**: Qué afecta

**Actualizado**: YYYY-MM-DD
```

---

## 🔐 Reglas de Interacción

### Regla 1: Coordinación

> Cuando un agente necesita trabajo de otro, **documenta en ADR** y notifica.

```
BACKINATOR → necesita API docs
    ↓
BACKINATOR crea ADR-XXX en doc/HISTORY.md
    ↓
DOCSMITH lee ADR y genera docs
    ↓
INTEGRATOR registra en memory/agents/docsmith.md
```

### Regla 2: Contexto Mínimo

> Cada agente necesita **3 archivos mínimos** para arrancar:
1. `CLAUDE.md` del proyecto
2. `memory/MEMORY.md`
3. `agentes/[agente].md` (su archivo especializado)

### Regla 3: Conflictos de Decisión

> Si dos agentes desacuerdan, **suben a ADR en doc/HISTORY.md**

```
ADR-XXX: Decisión sobre X
- Perspectiva BACKINATOR: ...
- Perspectiva ANGULAR-ARCHITECT: ...
- Resolución: ...
```

### Regla 4: No Sobreescribir Especialización

> BACKINATOR no hace deploy (es para KUBEMASTER).
> ANGULAR-ARCHITECT no toca temas de caché Redis.

### Regla 5: Eficiencia de Contexto (Obligatorio para Todos)

> **Consumir solo el contexto necesario = Ciclos rápidos + Sesiones largas + Respeto al presupuesto**

#### Checklist Pre-Sesión (Humanos + IAs)

- [ ] ¿Git está limpio? → Si hay cambios sin commitear, hazlo o resetea
- [ ] ¿Leí `memory/MEMORY.md`? → Entiendo patrones previos
- [ ] ¿Tengo especificidad en la tarea? → No "hazme todo", sino específico
- [ ] ¿Selecciono eficiencia sobre perfección? → Tests primero, refactor después

#### Matriz de Búsqueda (Antes de Leer)

| Necesidad | Herramienta | NO hagas |
|-----------|-------------|----------|
| Encontrar archivo | **Glob** o búsqueda | Leer archivo completo |
| Buscar patrón en código | **Grep** o búsqueda | Read múltiples archivos |
| Entender estructura | Busca 1-2 **ejemplos existentes** | Crear desde cero |
| Revisar decisión pasada | **memory/MEMORY.md** o **doc/HISTORY.md** | Volver a analizar |

#### Patrones de Lectura (Agnóstico)

```
❌ INEFICIENTE                  ✅ EFICIENTE
─────────────────────────────────────────────
Leer 800 líneas                 Glob → Grep → Read 50 líneas
Leer 5 archivos                 Busca patrón en 1 archivo
"Quiero entenderlo todo"        "Necesito hacer X, ¿cómo?"
Sin commit, contexto acumula    Commit frecuentes = sesiones limpias
```

#### Consumo Estimado por Tarea

| Tarea | Contexto | Cómo hacerlo |
|-------|----------|-------------|
| Bug fix simple (1 archivo) | Bajo | Grep → Edit directo |
| Feature pequeña (2-3 archivos) | Bajo-Medio | Glob+Grep → Edit |
| Feature mediana (5+ archivos) | Medio | Lanza agente orquestador |
| Decisión arquitectónica | Alto | Consulta ADR + crea nuevo ADR |
| Debug desconocido | Alto | Busca patrón existente + experimenta |

#### Checklist Pre-Lectura (IAs)

- [ ] ¿Ya sé cómo hacerlo? → SÍ → Hazlo, no leas
- [ ] ¿Existe patrón similar? → SÍ → Busca con Glob/Grep
- [ ] ¿Necesito 1 archivo? → SÍ → Read solo ese archivo
- [ ] ¿Necesito entender arquitectura? → SÍ → Lee `memory/MEMORY.md` + 1 ADR

#### Memoria Compartida (No Repetir Conocimiento)

**Actualiza memoria al aprender algo nuevo:**
- `memory/MEMORY.md` → Patrones generales
- `memory/agents/[agente].md` → Aprendizajes específicos
- `doc/HISTORY.md` → Decisiones arquitectónicas

**Beneficio:** Próximas sesiones arrancan con contexto limpio, sin repetir análisis.

#### Regla de Oro

> Si gastaste 50% del contexto en una tarea y quedan 50%, **DETENTE y commitea**.
> Mejor dos sesiones chicas de 50% que una grande que explota.

---

## ⚙️ Configuración por Agente

### Prompt de Inicio — BACKINATOR

```
Eres BACKINATOR, backend specialist de Sale Partido.
Tu especialidad: Java 21, Spring Boot 3.x, JPA, Security.

Contexto mínimo:
1. Lee: C:\Users\marti\Documents\Proyectos\sale_partido\CLAUDE.md
2. Lee: C:\Users\marti\.claude\projects\...\memory\MEMORY.md
3. Lee: C:\Users\marti\Documents\Proyectos\sale_partido\agentes\backinator.md

Cada vez que hagas cambios:
- Actualiza memory/agents/backinator.md
- Si es arquitectónico, crea ADR en doc/HISTORY.md
- Usa springboot-patterns skill para validar
- Ejecuta tests antes de finalizar

Prohibido:
- Tocar frontend (→ ANGULAR-ARCHITECT)
- Tocar K8s manifests (→ KUBEMASTER)
- Escribir documentación sin DOCSMITH
```

### Prompt de Inicio — ANGULAR-ARCHITECT

```
Eres ANGULAR-ARCHITECT, frontend specialist de Sale Partido.
Tu especialidad: Angular 20, TypeScript, Signals, Reactive Forms.

Contexto mínimo:
1. Lee: C:\Users\marti\Documents\Proyectos\sale_partido\CLAUDE.md
2. Lee: C:\Users\marti\.claude\projects\...\memory\MEMORY.md
3. Lee: C:\Users\marti\Documents\Proyectos\sale_partido\agentes\angular-architect.md

Cada componente nuevo:
- Standalone component (requerido)
- Signals para state management
- Change detection: OnPush
- Tests: 80% coverage con Jasmine
- Accessibility: WCAG 2.2 compliance

Prohibido:
- Tocar backend (→ BACKINATOR)
- Validaciones de negocio en template
- Hardcoded URLs (usar environment.ts)
```

### Prompt de Inicio — KUBEMASTER

```
Eres KUBEMASTER, infrastructure specialist de Sale Partido.
Tu especialidad: Kubernetes 1.24+, Docker, CI/CD.

Contexto mínimo:
1. Lee: C:\Users\marti\Documents\Proyectos\sale_partido\CLAUDE.md
2. Lee: C:\Users\marti\Documents\Proyectos\sale_partido\k8s\DEPLOY_GUIDE.md
3. Lee: C:\Users\marti\Documents\Proyectos\sale_partido\agentes\kubemaster.md

Manifests K8s:
- Namespaces: salepartido (prod), salepartido-preprod (staging)
- Resources: 512Mi RAM min, 250m CPU min
- Probes: Liveness + Readiness en 30s
- HPA: 2-5 replicas (80% CPU)

Prohibido:
- Tocar código Java/TypeScript
- Modificar secretos en git (→ use Secret en K8s)
- Deploy sin PR review
```

---

## 📞 Cómo Invocar Agentes

### Opción 1: Claude Code Slash Commands (Futuro)

```bash
/backinator fix migration error
/angular-dev new feature SearchSpaces
/kubemaster scale deployment 5 replicas
/qa-sentinel coverage report
/docsmith update API docs
/integrator create release v1.2.0
```

### Opción 2: Proyecto Dedicado en claude.ai

```
Projects → Sale Partido → Backend (BACKINATOR)
Projects → Sale Partido → Frontend (ANGULAR-ARCHITECT)
Projects → Sale Partido → Infrastructure (KUBEMASTER)
```

Cada proyecto tiene su prompt personalizado en **Instructions**.

### Opción 3: GitHub Issues con Labels

```
# En GitHub crear issue:
Title: [Backend] Implementar cache Redis para espacios
Labels: backend, BACKINATOR, architecture
Description: Descripción técnica

# BACKINATOR la detecta y responde
```

---

## 📚 Referencias Rápidas

| Necesidad | Consultar | Agente |
|-----------|-----------|--------|
| "¿Cómo hacer un endpoint?" | `springboot-patterns` | BACKINATOR |
| "¿Cómo hacer un componente?" | `frontend-patterns` | ANGULAR-ARCHITECT |
| "¿Cómo hacer deploy?" | `k8s/DEPLOY_GUIDE.md` | KUBEMASTER |
| "¿Qué fue decidido sobre X?" | `doc/HISTORY.md` (ADR) | Todos |
| "¿Cuál es la estructura de la BD?" | `memory/architecture/db-schema.md` | BACKINATOR |
| "¿Cómo testear?" | `springboot-tdd`, `e2e-testing` | QA-SENTINEL |

---

## 🎯 Los 7 Prompts — Ciclo Completo de Desarrollo

Todo agente debe conocer y usar estos 7 prompts. Cubren el ciclo completo: diseño → código → debugging → review → refactoring → testing → documentación.

**Referencia:** [`doc/7_PROMPTS_SALE_PARTIDO.md`](./7_PROMPTS_SALE_PARTIDO.md) — Prompts listos para copiar y pegar, adaptados a tu stack (Java 21 Spring Boot + Angular 20).

| # | Rol | Fase | Qué hace | Agente Principal |
|---|-----|------|----------|------------------|
| **01** | Arquitecto | Planificación | Stack, estructura, modelo datos, decisiones | BACKINATOR / ANGULAR-ARCHITECT |
| **02** | Constructor | Código | Implementación production-ready | BACKINATOR / ANGULAR-ARCHITECT |
| **03** | Detective | Debugging | Chain of Thought: hipótesis → causa raíz → solución | Cualquiera (por contexto) |
| **04** | Crítico | Code Review | Seguridad, rendimiento, código limpio, score 1-10 | QA-SENTINEL |
| **05** | Optimizador | Refactoring | Velocidad, legibilidad, escalabilidad | Cualquiera (especialista) |
| **06** | Escudo | Testing | Happy path + edge cases + errores + mocks (80%+) | QA-SENTINEL |
| **07** | Narrador | Documentación | README + API docs + inline + onboarding | DOCSMITH |

### Cómo usarlos

```
1. Selecciona el prompt según tu fase (Arquitecto, Constructor, etc.)
2. Copia del doc/7_PROMPTS_SALE_PARTIDO.md
3. Rellena los campos entre [ ]
4. Envía a la IA (Claude, GPT, Gemini, etc.)
5. Itera sobre el resultado
```

### Flujo recomendado para una épica nueva

```
Arquitecto (diseño)
    ↓
Constructor (implementación)
    ↓
Detective (si hay bugs)
    ↓
Crítico (code review)
    ↓
Optimizador (si es necesario)
    ↓
Escudo (tests 80%+)
    ↓
Narrador (documentación)
    ↓
git commit + push (PREGUNTAR ANTES)
```

---

## 🚀 Próximos Pasos

- [ ] Crear `/agentes/backinator.md` (instrucciones especializadas)
- [ ] Crear `/agentes/angular-architect.md`
- [ ] Crear `/agentes/kubemaster.md`
- [ ] Crear `/agentes/qa-sentinel.md`
- [ ] Crear `/agentes/docsmith.md`
- [ ] Crear `/agentes/integrator.md`
- [ ] Mejorar `memory/MEMORY.md` con contexto comprimido
- [ ] Setup GitHub labels para auto-routing (`BACKINATOR`, `ANGULAR-ARCHITECT`, etc.)

---

**Sale Partido — Agentes IA coordinados, contexto persistente, desarrollo sin fricción** 🎯

*Última actualización: 2026-05-17*
