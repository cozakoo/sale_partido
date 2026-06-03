# Sale Partido

**Plataforma de gestión y reserva de espacios deportivos con participación comunitaria**

> Trabajo práctico integrador de Ingeniería de Software (IF015 — UNPSJB)

---

## 📋 Contenido

1. [Quick Start](#-quick-start-5-min)
2. [Descripción](#descripción)
3. [Comenzar](#comenzar)
4. [Estructura](#estructura)
5. [Agentes (IAs)](#-agentes--ias)
6. [Épicas](#épicas)
7. [Documentación](#documentación)
8. [Cambios Recientes](#-cambios-recientes)

---

## Quick Start (5 min)

**¿Primero aquí?** → Lee `docs/QUICK_START.md`

```bash
./scripts/setup/init.sh                                      # Setup automático
node scripts/github/sync-github-projects.js                  # Descarga HUs
./scripts/git/create-branch.sh E4-H08 mi-feature             # Crea rama
# [Codifica con IA usando agentes/backend.md o frontend.md]
git commit -m "feat(E4-H08): descripcion"                    # Git hook valida
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
./scripts/setup/init.sh

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
├── config/                     # Archivos de configuración
│   └── sonar-project.properties
├── scripts/                    # Scripts de utilidad
│   ├── init.sh                # Setup automático
│   ├── create-branch.sh       # Crear rama con formato
│   ├── sync-github-projects.js # Sincronizar HUs
│   ├── *.sh                   # Otros scripts
│   └── sql/
│       └── seed-locales.sql   # Script de datos iniciales
├── agentes/                    # Instrucciones para IAs
│   ├── backend.md             # Guía Backend IA
│   ├── frontend.md            # Guía Frontend IA
│   ├── infra.md               # Guía DevOps IA
│   ├── API_CONTRACTS.md       # Contrato Backend ↔ Frontend
│   └── ONBOARDING.md          # Cómo empezar a codificar
├── docs/                       # Documentación técnica y general
│   ├── decisions/
│   │   └── Decisiones.md      # Decisiones arquitectónicas
│   ├── architecture/
│   │   └── STACK.md           # Stack técnico detallado
│   ├── workflow/
│   │   ├── Convenciones_de_branching.md
│   │   ├── GITHUB_PROJECTS.md
│   │   └── TESTING.md         # Estrategia de testing
│   ├── guides/
│   │   └── TROUBLESHOOTING.md # Errores comunes
│   ├── SETUP.md               # Setup rápido
│   ├── QUICK_START.md         # Quick start
│   └── CHANGELOG.md           # Historial de cambios
└── docker-compose.yml         # Local stack
```

---

## 🤖 Agentes (IAs)

**Copia los agentes en tu IA (Claude, ChatGPT, Gemini) antes de codificar.**

| Agente | Archivo | Especialidad |
|--------|---------|--------------|
| **Backineitor** ⚙️ | `/agents/backend.md` | Backend: Java 21, Spring Boot |
| **Frontalyx** 🎨 | `/agents/frontend.md` | Frontend: Angular 20, TypeScript |
| **Kuberator** ☸️ | `/agents/infra.md` | DevOps: Kubernetes, CI/CD |
| **Testeador** ✅ | `/agents/qa.md` | QA: Testing, cobertura |

**Workflow:**
1. Abre agente correspondiente
2. Copia TODO el contenido
3. Pégalo en tu IA
4. Pide que implemente tu feature
5. Corre tests localmente
6. Commit + PR

Ver: `/agents/ONBOARDING.md`

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
- **[docs/SETUP.md](docs/SETUP.md)** — Setup rápido (1 minuto)
- **[docs/QUICK_START.md](docs/QUICK_START.md)** — Quick start (5 minutos)
- **[/agents/ONBOARDING.md](/agents/ONBOARDING.md)** — Cómo desarrollar

### 🏗️ Arquitectura y decisiones
- **[/documentation/decisions/Decisiones.md](/documentation/decisions/Decisiones.md)** — Decisiones técnicas tomadas
- **[/documentation/architecture/STACK.md](/documentation/architecture/STACK.md)** — Stack técnico (Java 21, Angular 20, PostgreSQL, K8s)
- **[/agents/API_CONTRACTS.md](/agents/API_CONTRACTS.md)** — API endpoints y DTOs

### 💻 Desarrollo
- **[/agents/backend.md](/agents/backend.md)** — Guía Backend (copia en tu IA)
- **[/agents/frontend.md](/agents/frontend.md)** — Guía Frontend (copia en tu IA)
- **[/documentation/workflow/Convenciones_de_branching.md](/documentation/workflow/Convenciones_de_branching.md)** — Git workflow, commits
- **[/documentation/workflow/GITHUB_PROJECTS.md](/documentation/workflow/GITHUB_PROJECTS.md)** — GitHub Projects, HUs, aceptance criteria

### ✅ Testing
- **[/documentation/workflow/TESTING.md](/documentation/workflow/TESTING.md)** — Unit, integration, E2E tests
  - JUnit 5 + Mockito (backend)
  - Jasmine + Playwright (frontend)
  - Cobertura: 80% (backend), 75% (frontend)

### 🔧 DevOps e Infraestructura
- **[/agents/infra.md](/agents/infra.md)** — Kubernetes, containerd, CI/CD
- **[/documentation/guides/TROUBLESHOOTING.md](/documentation/guides/TROUBLESHOOTING.md)** — Errores y soluciones

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

Detalles: ver **[/documentation/architecture/STACK.md](/documentation/architecture/STACK.md)**

---

## Checklist antes de hacer push

- [ ] Leí `/agents/ONBOARDING.md`
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
- **[/documentation/guides/TROUBLESHOOTING.md](/documentation/guides/TROUBLESHOOTING.md)** — Errores comunes
- **[/documentation/workflow/TESTING.md](/documentation/workflow/TESTING.md)** — Fallos en tests
- **[/agents/infra.md](/agents/infra.md)** — Problemas DevOps

---

## ¿Quién hace qué?

| Role | Tarea | Referencia |
|------|-------|-----------|
| **Backend Dev** | Codificar servicio | `/agents/backend.md` |
| **Frontend Dev** | Codificar componente | `/agents/frontend.md` |
| **DevOps** | Deploy, CI/CD | `/agents/infra.md` |
| **QA** | Testing, cobertura | `/documentation/workflow/TESTING.md` |
| **Tech Lead** | Decisiones, arquitectura | `/documentation/decisions/Decisiones.md` |

---

## Estado

- **Stack:** Java 21, Spring Boot 4.0.6, Angular 20.3.0
- **MVP:** En desarrollo (E1-E4)
- **Última actualización:** 24 de mayo 2026
- **CI/CD:** GitHub Actions + self-hosted K8s

---

---

## 🔄 Cambios Recientes

### Reorganización de la raíz del proyecto (Junio 2026)

Se reorganizaron los archivos de la raíz para mejorar la estructura sin afectar el funcionamiento:

**Movimientos:**
- `SETUP.md`, `QUICK_START.md`, `CHANGELOG.md` → `docs/`
- `sonar-project.properties` → `config/`
- `setup-env-preproduction.sh`, `use-preproduction.sh` → `scripts/setup/`
- `seed-locales.sql` → `scripts/sql/`

**Archivos que permanecen en la raíz:**
- `.env`, `.env.example` (referencias en docker-compose.yml)
- `docker-compose.yml` (punto de entrada)
- `package.json`, `package-lock.json` (npm)
- `.gitignore`, `.nvmrc` (git config)

**Referencias actualizadas:**
- README.md refiere a los nuevos paths
- `docker-compose.yml` sigue funcionando sin cambios
- Todos los scripts mantienen su funcionalidad

**Cómo usar la nueva estructura:**
```bash
# Docs
cat docs/SETUP.md                  # Setup instructions
cat docs/QUICK_START.md            # Quick start guide

# Config
cat config/sonar-project.properties # SonarQube config

# Scripts (nuevo: usar scripts/run.sh para acceso fácil)
./scripts/run.sh help              # Ver todos los comandos
./scripts/setup/init.sh            # Setup automático
./scripts/github/sync-github-projects.js
./scripts/sql/seed-locales.sql     # Database seeds
```

### Reorganización de la carpeta scripts/ (Junio 2026)

Se agruparon los scripts por funcionalidad para mejor organización:

**Nueva estructura:**
```
scripts/
├── setup/      → Configuración del proyecto
├── github/     → Integración con GitHub Projects
├── git/        → Git workflow (branches, hooks)
├── changelog/  → Generación de CHANGELOG
├── sql/        → Data initialization
└── run.sh      → 🆕 Script orquestador principal
```

**Acceso centralizado:**
```bash
./scripts/run.sh setup:init         # Setup inicial
./scripts/run.sh github:sync        # Sincronizar HUs
./scripts/run.sh git:branch E4-H08 crear-local  # Crear rama
./scripts/run.sh github:validate E4-H08        # Validar HU
./scripts/run.sh help               # Ver todos los comandos
```

**Scripts en subdirectorios (directo):**
```bash
./scripts/setup/init.sh
node scripts/github/sync-github-projects.js
./scripts/git/create-branch.sh E4-H08 crear-local
```

---

**Sale Partido** — Ingeniería de software profesional
