# 🤖 Agentes Especializados — Sale Partido

Central de documentación de agentes IA especializados para desarrollo, testing, deployment y documentación del proyecto **Sale Partido**.

> **⚡ Antes de empezar:** Lee [Eficiencia de Contexto](../doc/AGENTS.md#regla-5-eficiencia-de-contexto-obligatorio-para-todos) en `doc/AGENTS.md`. Aplica para TODOS los agentes (Claude, GPT, Gemini, etc.)

---

## 📋 Agentes Disponibles

### 1. **BACKINATOR** — Backend Java/Spring Specialist
**Archivo**: `backinator.md`

Experto en:
- Spring Boot 3.x, Java 21
- Domain-Driven Design (DDD)
- JPA/Hibernate, PostgreSQL
- REST API design, Swagger/OpenAPI
- Security: JWT, Spring Security
- Caché con Redis
- Testing: JUnit 5, Mockito
- Database migrations: Flyway

**Cuándo contactar**: Cualquier cosa relacionada con backend, servicios, BD.

---

### 2. **ANGULAR-ARCHITECT** — Frontend Angular Specialist
**Archivo**: `angular-architect.md`

Experto en:
- Angular 20+, TypeScript 5.x
- Standalone components
- Signals (state management)
- Forms reactivos
- RxJS, observables
- Testing: Jasmine, Karma
- E2E: Playwright
- Accesibilidad: WCAG 2.2
- Design: TailwindCSS, responsivo

**Cuándo contactar**: Componentes, UX, estado, forms, styling.

---

### 3. **KUBEMASTER** — Kubernetes & DevOps Specialist
**Archivo**: `kubemaster.md`

Experto en:
- Kubernetes 1.24+
- Docker, Docker Compose
- Manifests YAML
- Deployments, Services, Ingress
- StatefulSets (DB, Redis)
- Auto-scaling (HPA)
- Networking, ConfigMaps, Secrets
- Monitoring, logging
- Helm charts

**Cuándo contactar**: Infraestructura, K8s, scaling, deployment.

---

### 4. **QA-SENTINEL** — Testing & Quality Specialist
**Archivo**: `qa-sentinel.md`

Experto en:
- Test-Driven Development (TDD)
- Unit tests (JUnit 5, Jasmine)
- Integration tests
- E2E tests (Playwright)
- Coverage tracking (JaCoCo)
- Performance testing
- Security testing (OWASP)
- Flaky tests detection

**Cuándo contactar**: Tests, coverage, quality, test strategy.

---

### 5. **DOCSMITH** — Documentation Specialist
**Archivo**: `docsmith.md`

Experto en:
- API documentation (Swagger/OpenAPI)
- Architectural Decision Records (ADR)
- Setup guides
- Code tours
- README + user guides
- Architecture diagrams
- Troubleshooting guides

**Cuándo contactar**: Documentación, guías, onboarding, API docs.

---

### 6. **INTEGRATOR** — CI/CD & Git Workflow Specialist
**Archivo**: `integrator.md`

Experto en:
- GitHub Actions workflows
- Git workflow, branching strategy
- Semantic versioning
- Commit conventions
- PR management
- Release management
- Automation

**Cuándo contactar**: Git, CI/CD, releases, workflows, versioning.

---

## 🚀 Cómo Usar Estos Agentes

### Opción 1: Consultar Archivos Directamente

```bash
# Leer la guía de BACKINATOR
cat agentes/backinator.md

# Leer la guía de ANGULAR-ARCHITECT
cat agentes/angular-architect.md

# Etc.
```

### Opción 2: Solicitar a Claude Code

**En sesión Claude Code:**
```
Actúa como BACKINATOR. Necesito [describir tarea].
Lee primero: agentes/backinator.md
```

### Opción 3: Proyectos Dedicados en claude.ai

Para máxima especialización, crear proyectos:
- `Sale Partido → Backend (BACKINATOR)`
- `Sale Partido → Frontend (ANGULAR-ARCHITECT)`
- `Sale Partido → Infrastructure (KUBEMASTER)`

Copiar el contenido de cada `.md` a las **Instrucciones** del proyecto.

---

## 🧠 Contexto Persistente

Todos los agentes comparten:

1. **`CLAUDE.md`** — Project overview, stack, structure
2. **`memory/MEMORY.md`** — Compressed core facts (max 200 lines)
3. **`doc/HISTORY.md`** — ADR and architectural decisions
4. **`doc/AGENTS.md`** — Central agent coordination
5. **Su archivo `.md`** — Instrucciones especializadas

### Flujo de Contexto

```
Sesión comienza
    ↓
Lee: CLAUDE.md (proyecto)
Lee: memory/MEMORY.md (core facts)
Lee: doc/AGENTS.md (roles)
Lee: agentes/[agente].md (instrucciones)
    ↓
Agente está listo para trabajar
    ↓
Durante trabajo:
  - Usa mem-search para encontrar contexto
  - Actualiza memory/agents/[agente].md si es necesario
  - Crea ADR en doc/HISTORY.md si es arquitectónico
    ↓
Sesión termina
    ↓
Nuevo agente comienza con el mismo contexto
```

---

## 📊 Matriz de Competencia

| Tarea | BACKINATOR | ANGULAR | KUBEMASTER | QA | DOCSMITH | INTEGRATOR |
|-------|-----------|---------|-----------|----|---------|----|
| Feature backend | ⭐⭐⭐ | ⭐ | ⭐ | ⭐⭐ | ⭐ | ⭐⭐ |
| Feature frontend | ⭐ | ⭐⭐⭐ | ⭐ | ⭐⭐ | ⭐ | ⭐⭐ |
| K8s deployment | ⭐ | ⭐ | ⭐⭐⭐ | ⭐ | ⭐ | ⭐⭐ |
| Testing | ⭐⭐ | ⭐⭐ | ⭐ | ⭐⭐⭐ | ⭐ | ⭐ |
| Documentation | ⭐⭐ | ⭐⭐ | ⭐⭐ | ⭐ | ⭐⭐⭐ | ⭐⭐ |
| CI/CD | ⭐⭐ | ⭐⭐ | ⭐⭐ | ⭐ | ⭐ | ⭐⭐⭐ |
| Code review | ⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐ | ⭐ | ⭐⭐ |
| Security | ⭐⭐⭐ | ⭐⭐ | ⭐⭐ | ⭐⭐⭐ | ⭐ | ⭐⭐ |

---

## 🔄 Coordinación Entre Agentes

### Ejemplo: Implementar Nueva Feature

1. **BACKINATOR** crea endpoint REST + service + tests
2. **ANGULAR-ARCHITECT** crea componente + consume endpoint
3. **QA-SENTINEL** valida cobertura de tests
4. **DOCSMITH** genera API docs + UI guide
5. **INTEGRATOR** maneja PR + merge
6. **KUBEMASTER** si requiere cambios de infra

### Cómo se Coordinan

- Actualizar `doc/HISTORY.md` con ADR relevante
- Comentar en GitHub PR con referencias
- Actualizar `memory/agents/[agente].md` con decisiones
- Usar `mem-search` para encontrar contexto de otros agentes

---

## 📞 Contacto Rápido

**Necesito...** → **Contacto**

- Backend feature → BACKINATOR
- Frontend feature → ANGULAR-ARCHITECT
- K8s deployment → KUBEMASTER
- Tests & QA → QA-SENTINEL
- API docs → DOCSMITH
- Git & release → INTEGRATOR

---

## 🎯 Próximos Pasos

- [ ] Implementar `/agentes/` como comando slash en Claude Code
- [ ] Setup GitHub labels automáticos (`BACKINATOR`, `ANGULAR-ARCHITECT`, etc.)
- [ ] Mejorar `memory/MEMORY.md` con contexto comprimido
- [ ] Crear archivos especializados en `memory/agents/`
- [ ] Documentar en `doc/CONTEXT.md` cómo usar el sistema

---

**Sale Partido — Agentes especializados coordinados** 🤖

*Creado: 2026-05-16*
