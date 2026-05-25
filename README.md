# Sale Partido

**Plataforma de gestión y reserva de espacios deportivos con participación comunitaria**

> Trabajo práctico integrador de Ingeniería de Software (IF015 — UNPSJB)

---

## 📋 Contenido

1. [Quick Start)](#-quick-start-5-min)
2. [Descripción](#descripción)
3. [Comenzar](#comenzar)
4. [Estructura](#estructura)
5. [Agentes (IAs)](#agentes--ias)
6. [Épicas](#épicas)
7. [Documentación](#documentación)

---

## Quick Start (5 min)

**¿Primero aquí?** → Lee `QUICK_START.md`

```bash
./scripts/init.sh                                      # Setup automático
node scripts/sync-github-projects.js                  # Descarga HUs
./scripts/create-branch.sh E4-H08 mi-feature          # Crea rama
# [Codifica con IA usando agentes/backend.md o frontend.md]
git commit -m "feat(E4-H08): descripcion"              # Git hook valida
```

---

## Descripción

Conecta jugadores, propietarios de espacios y organizadores de eventos. Permite descubrir espacios, reservar, participar en eventos, todo integrado con notificaciones y pagos.

**Características:**
- Exploración + búsqueda con mapa
- Reservas, confirmación, cancelación
- Eventos deportivos
- Participación, reseñas, calificaciones
- Competencias, rankings
- Notificaciones push/email
- Pagos integrados
- Analytics

---

## Comenzar

### Setup Inicial (UNA sola vez)

```bash
# Instala git hooks, configura .env, e instala dependencias
./scripts/init.sh

# Luego: edita .env y agrega tu GITHUB_TOKEN
# Referencia: ./scripts/README.md
```

---

### Opción 1: Docker (recomendado)

```bash
git clone https://github.com/cozakoo/sale-partido.git
cd sale-partido
docker-compose up -d
```

Accede:
- Backend: http://localhost:8080
- Frontend: http://localhost:4200
- Swagger: http://localhost:8080/swagger-ui.html

### Opción 2: Local

**Backend:**
```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
# http://localhost:8080
```

**Frontend:**
```bash
cd frontend
npm install
ng serve
# http://localhost:4200
```

---

## Estructura

```
sale-partido/
├── backend/                    # Spring Boot (Java 21)
│   └── domain/[epic]/         # DDD por épica
├── frontend/                   # Angular 20 (TypeScript)
│   └── src/app/features/      # Features por épica
├── k8s/                        # Kubernetes manifests
├── agentes/                    # Instrucciones para IAs
│   ├── backend.md             # Guía Backend IA
│   ├── frontend.md            # Guía Frontend IA
│   ├── infra.md               # Guía DevOps IA
│   ├── API_CONTRACTS.md       # Contrato Backend ↔ Frontend
│   └── ONBOARDING.md          # Cómo empezar a codificar
├── doc/                        # Documentación técnica
│   ├── Decisiones.md          # Decisiones arquitectónicas
│   ├── Convenciones_de_branching.md
│   ├── STACK.md               # Stack técnico detallado
│   ├── TESTING.md             # Estrategia de testing
│   └── TROUBLESHOOTING.md     # Errores comunes
├── SETUP.md                    # Setup rápido
└── docker-compose.yml         # Local stack
```

---

## 🤖 Agentes (IAs)

**Copia los agentes en tu IA (Claude, ChatGPT, Gemini) antes de codificar.**

| Agente | Archivo | Especialidad |
|--------|---------|--------------|
| **Backineitor** ⚙️ | `/agentes/backend.md` | Backend: Java 21, Spring Boot |
| **Frontalyx** 🎨 | `/agentes/frontend.md` | Frontend: Angular 20, TypeScript |
| **Kuberator** ☸️ | `/agentes/infra.md` | DevOps: Kubernetes, CI/CD |
| **Testeador** ✅ | `/agentes/qa.md` | QA: Testing, cobertura |

**Workflow:**
1. Abre agente correspondiente
2. Copia TODO el contenido
3. Pégalo en tu IA
4. Pide que implemente tu feature
5. Corre tests localmente
6. Commit + PR

Ver: `/agentes/ONBOARDING.md`

---

## Épicas

| # | Nombre | Módulo | Qué incluye |
|---|--------|--------|------------|
| **E1** | Exploración | exploration | Búsqueda, filtros, mapa |
| **E2** | Participación | participation | Inscripción, reseñas |
| **E3** | Eventos | events | Crear, editar eventos |
| **E4** | Espacios | locales | ABM, configuración |
| **E5** | Reservas | reservations | Reserva, cancelación |
| **E6** | Notificaciones | notifications | Push, email |
| **E7** | Pagos | payments | Cobros, transferencias |
| **E8** | Competencias | competitions | Torneos, rankings |
| **E9** | Analytics | analytics | Reportes |

---

## Documentación

### 🚀 Para empezar
- **[SETUP.md](SETUP.md)** — Setup rápido (1 minuto)
- **[/agentes/ONBOARDING.md](/agentes/ONBOARDING.md)** — Cómo desarrollar

### 🏗️ Arquitectura y decisiones
- **[/doc/architecture/Decisiones.md](/doc/architecture/Decisiones.md)** — Decisiones técnicas tomadas
- **[/doc/architecture/STACK.md](/doc/architecture/STACK.md)** — Stack técnico (Java 21, Angular 20, PostgreSQL, K8s)
- **[/agentes/API_CONTRACTS.md](/agentes/API_CONTRACTS.md)** — API endpoints y DTOs

### 💻 Desarrollo
- **[/agentes/backend.md](/agentes/backend.md)** — Guía Backend (copia en tu IA)
- **[/agentes/frontend.md](/agentes/frontend.md)** — Guía Frontend (copia en tu IA)
- **[/doc/workflow/Convenciones_de_branching.md](/doc/workflow/Convenciones_de_branching.md)** — Git workflow, commits
- **[/doc/workflow/GITHUB_PROJECTS.md](/doc/workflow/GITHUB_PROJECTS.md)** — GitHub Projects, HUs, aceptance criteria

### ✅ Testing
- **[/doc/workflow/TESTING.md](/doc/workflow/TESTING.md)** — Unit, integration, E2E tests
  - JUnit 5 + Mockito (backend)
  - Jasmine + Playwright (frontend)
  - Cobertura: 80% (backend), 75% (frontend)

### 🔧 DevOps e Infraestructura
- **[/agentes/infra.md](/agentes/infra.md)** — Kubernetes, containerd, CI/CD
- **[/doc/guides/TROUBLESHOOTING.md](/doc/guides/TROUBLESHOOTING.md)** — Errores y soluciones

### 📊 Métricas y Calidad
- Coverage: ≥80% (backend), ≥75% (frontend)
- Code duplication: <3%
- API latency P95: <200ms
- Frontend bundle: <300KB gzip
- Uptime: >99.5% producción

---

## Quick Commands

### Backend
```bash
cd backend
./mvnw clean install      # Instalar deps
./mvnw spring-boot:run    # Ejecutar
./mvnw verify             # Tests + SonarQube
./mvnw test jacoco:report # Coverage
```

### Frontend
```bash
cd frontend
npm install        # Instalar deps
ng serve           # Ejecutar
npm run test       # Tests
npm run e2e        # E2E tests (Playwright)
npm run lint       # Linting
```

### Docker
```bash
docker-compose up -d   # Levantar stack
docker-compose ps      # Ver servicios
docker-compose logs    # Ver logs
```

### Git
```bash
git checkout -b feature/E4-H08-crear-local
git add .
git commit -m "feat(E4): crear local con validaciones"
git push origin feature/E4-H08-crear-local
```

---

## Stack (Resumen)

| Capa | Tech |
|------|------|
| **Backend** | Java 21 + Spring Boot 4.0.6 + Maven |
| **Frontend** | TypeScript 5.9 + Angular 20.3 + Bootstrap 5 |
| **DB** | PostgreSQL 15+ |
| **Cache** | Redis 7+ |
| **Auth** | JWT (HS256) + Spring Security |
| **Testing** | JUnit 5 + Mockito + Jasmine + Playwright |
| **CI/CD** | GitHub Actions |
| **Infra** | Docker + Kubernetes 1.31.14 + containerd |

Detalles: ver **[/doc/architecture/STACK.md](/doc/architecture/STACK.md)**

---

## Checklist antes de hacer push

- [ ] Leí `/agentes/ONBOARDING.md`
- [ ] Usé el agente correspondiente (backend/frontend/infra)
- [ ] Tests pasan: `./mvnw verify` o `npm run test`
- [ ] Coverage ≥80% (líneas nuevas)
- [ ] Sin linting errors
- [ ] Branch: `feature/E[#]-H[##]-descripcion`
- [ ] Commit: Conventional Commits + épica
- [ ] PR con descripción clara

---

## Troubleshooting

**¿Algo no funciona?** Ver:
- **[/doc/guides/TROUBLESHOOTING.md](/doc/guides/TROUBLESHOOTING.md)** — Errores comunes
- **[/doc/workflow/TESTING.md](/doc/workflow/TESTING.md)** — Fallos en tests
- **[/agentes/infra.md](/agentes/infra.md)** — Problemas DevOps

---

## ¿Quién hace qué?

| Role | Tarea | Referencia |
|------|-------|-----------|
| **Backend Dev** | Codificar servicio | `/agentes/backend.md` |
| **Frontend Dev** | Codificar componente | `/agentes/frontend.md` |
| **DevOps** | Deploy, CI/CD | `/agentes/infra.md` |
| **QA** | Testing, cobertura | `/doc/workflow/TESTING.md` |
| **Tech Lead** | Decisiones, arquitectura | `/doc/architecture/Decisiones.md` |

---

## Estado

- **Stack:** Java 21, Spring Boot 4.0.6, Angular 20.3.0
- **MVP:** En desarrollo (E1-E4)
- **Última actualización:** 24 de mayo 2026
- **CI/CD:** GitHub Actions + self-hosted K8s

---

**Sale Partido** — Ingeniería de software profesional
