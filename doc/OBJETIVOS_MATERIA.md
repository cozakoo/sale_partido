# CLAUDE.md — Sale Partido

**Última actualización:** 29 de abril de 2026  
**Proyecto:** Sale Partido (TP Ingeniería de Software — IF015)  
**Equipo:** 4-5 desarrolladores  
**Metodología:** Scrumban + 2 semanas de sprints

---

## CONTEXTO GENERAL

### Qué es Sale Partido

Plataforma de gestión y reserva de espacios deportivos con participación comunitaria. MVP enfocado en **exploración**, **reservas**, **eventos** y **participación**.

### Objetivo IF015

Demostrar ingeniería de software profesional: análisis→diseño→implementación→testing→deployment con métricas de calidad.

---

## ARQUITECTURA TÉCNICA

### Stack Decisivo (Votado por mayoría)

#### **Backend**

```
Lenguaje:        Java 21
Framework:       Spring Boot 3.x
Arquitectura:    Monolito Modular (DDD)
Gestión deps:    Maven
ORM:             Spring Data JPA + Hibernate
Validación:      Spring Validation (@Valid, @Validated)
API:             REST (Spring Web)
Autenticación:   Spring Security + JWT (HS256)
Documentación:   Springdoc OpenAPI (Swagger)
```

#### **Frontend**

```
Lenguaje:        TypeScript 5.x
Framework:       Angular 17+
Build:           Angular CLI
Estilos:         SCSS + TailwindCSS
Gestión estado:  RxJS (Reactive)
Forms:           Reactive Forms (@angular/forms)
Http Client:     HttpClient + Interceptors
Testing:         Jasmine/Karma (unit) + Playwright (E2E)
```

#### **Base de Datos**

```
BD Principal:    PostgreSQL 15+
Caché:           Redis 7+ (cache + sesiones distribuidas)
Migraciones:     Flyway (Java)
Conexión Pool:   HikariCP (default en Spring)
```

#### **DevOps & Contenerización**

```
Contenedores:    Docker (multi-stage builds)
Orquestación:    Kubernetes (minikube local, K8s en prod)
Alternativa:     Railway.app o Render (si K8s es overkill)
CI/CD:           GitHub Actions
Testing E2E:     Playwright (TypeScript)
Control versión: GitHub (gitflow)
```

#### **Monitoreo & Logging**

```
Logging:         Logback (backend) + Winston (frontend)
Monitoreo:       Prometheus + Grafana (métricas)
APM:             Spring Boot Actuator
Centralizado:    ELK Stack o Loki (opcional para MVP)
Alertas:         Email simple o webhook (si CPU > 80%)
```

#### **Calidad de Código**

```
Code Quality:    SonarQube (backend + frontend)
Linting Backend: Checkstyle + SpotBugs
Linting Frontend: ESLint + Prettier
Seguridad:       Snyk + OWASP Dependency Check
SAST:            SonarQube + Snyk
Cobertura:       ≥80% (backend), ≥75% (frontend)
```

---

## ESTRUCTURA DE DIRECTORIOS

### Backend (Java/Spring Boot)

```
sale-partido-backend/
├── .github/
│   └── workflows/
│       ├── ci.yml                 ← Tests + lint + build
│       ├── integration.yml        ← Integración con BD
│       ├── e2e.yml               ← Playwright (nightly)
│       └── deploy.yml            ← CD a staging/prod
│
├── src/
│   ├── main/java/com/saleww/
│   │   ├── SalePartidoApplication.java
│   │   ├── config/
│   │   │   ├── SecurityConfig.java        ← JWT + Spring Security
│   │   │   ├── CorsConfig.java            ← CORS para frontend
│   │   │   ├── CacheConfig.java           ← Redis
│   │   │   ├── WebConfig.java             ← Jackson, formatters
│   │   │   └── OpenApiConfig.java         ← Swagger
│   │   │
│   │   ├── domain/                        ← DDD: por épica
│   │   │   ├── exploration/               ← E1: Búsqueda espacios
│   │   │   │   ├── entity/
│   │   │   │   │   ├── Space.java
│   │   │   │   │   └── SearchCriteria.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── SpaceDTO.java
│   │   │   │   │   └── SearchRequestDTO.java
│   │   │   │   ├── repository/
│   │   │   │   │   └── SpaceRepository.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── SpaceService.java   ← Lógica negocio
│   │   │   │   │   └── SearchService.java
│   │   │   │   ├── controller/
│   │   │   │   │   └── SpaceController.java
│   │   │   │   └── exception/
│   │   │   │       └── SpaceNotFoundException.java
│   │   │   │
│   │   │   ├── participation/             ← E2: Participación
│   │   │   │   ├── entity/
│   │   │   │   ├── dto/
│   │   │   │   ├── repository/
│   │   │   │   ├── service/
│   │   │   │   ├── controller/
│   │   │   │   └── exception/
│   │   │   │
│   │   │   ├── events/                    ← E3: Eventos
│   │   │   ├── spaces/                    ← E4: Gestión espacios
│   │   │   ├── reservations/              ← E5: Reservas
│   │   │   ├── notifications/             ← E6: Notificaciones
│   │   │   ├── payments/                  ← E7: Pagos
│   │   │   ├── competitions/              ← E8: Competencias
│   │   │   └── analytics/                 ← E9: Analítica
│   │   │
│   │   ├── shared/                        ← Código común
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ApiError.java
│   │   │   │   └── ApiException.java
│   │   │   ├── util/
│   │   │   │   ├── DateUtil.java
│   │   │   │   └── ValidationUtil.java
│   │   │   ├── security/
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   └── JwtAuthenticationFilter.java
│   │   │   └── constants/
│   │   │       └── ApiConstants.java
│   │   │
│   │   └── infrastructure/                ← Integraciones externas
│   │       ├── cache/
│   │       │   └── CacheService.java
│   │       ├── mail/
│   │       │   └── EmailService.java
│   │       ├── payment/
│   │       │   └── PaymentGateway.java
│   │       └── notification/
│   │           └── NotificationService.java
│   │
│   └── test/
│       ├── java/com/saleww/
│       │   ├── unit/
│       │   │   ├── service/
│       │   │   │   └── SpaceServiceTest.java
│       │   │   └── repository/
│       │   │       └── SpaceRepositoryTest.java
│       │   └── integration/
│       │       ├── SpaceIntegrationTest.java
│       │       └── AuthenticationIntegrationTest.java
│       └── resources/
│           ├── application-test.properties
│           └── test-data.sql
│
├── pom.xml                                ← Maven: deps, plugins, profiles
├── Dockerfile                             ← Multi-stage build
├── docker-compose.test.yml               ← Para CI: PostgreSQL + Redis
├── docker-compose.yml                    ← Dev local
│
└── README.md                              ← Instrucciones setup

```

### Frontend (Angular/TypeScript)

```
sale-partido-frontend/
├── .github/workflows/
│   └── (heredadas del backend)
│
├── src/
│   ├── app/
│   │   ├── core/                          ← Servicios críticos
│   │   │   ├── guards/
│   │   │   │   ├── auth.guard.ts
│   │   │   │   └── admin.guard.ts
│   │   │   ├── interceptors/
│   │   │   │   ├── jwt.interceptor.ts     ← Inyecta token JWT
│   │   │   │   ├── error.interceptor.ts   ← Manejo global errores
│   │   │   │   └── retry.interceptor.ts   ← Reintentos
│   │   │   ├── services/
│   │   │   │   ├── auth.service.ts
│   │   │   │   ├── api.service.ts         ← Base para HTTP
│   │   │   │   └── cache.service.ts
│   │   │   └── core.module.ts
│   │   │
│   │   ├── shared/                        ← Componentes reutilizables
│   │   │   ├── components/
│   │   │   │   ├── header/
│   │   │   │   ├── footer/
│   │   │   │   ├── navbar/
│   │   │   │   └── modal/
│   │   │   ├── pipes/
│   │   │   │   └── date-format.pipe.ts
│   │   │   ├── directives/
│   │   │   │   └── debounce.directive.ts
│   │   │   ├── models/
│   │   │   │   ├── user.model.ts
│   │   │   │   └── api-response.model.ts
│   │   │   └── shared.module.ts
│   │   │
│   │   └── modules/                       ← Por épica/feature
│   │       ├── exploration/               ← E1: Búsqueda
│   │       │   ├── pages/
│   │       │   │   ├── search/
│   │       │   │   │   └── search.component.ts
│   │       │   │   └── space-detail/
│   │       │   │       └── space-detail.component.ts
│   │       │   ├── components/
│   │       │   │   ├── space-card/
│   │       │   │   ├── filter-panel/
│   │       │   │   └── map-view/
│   │       │   ├── services/
│   │       │   │   └── space.service.ts
│   │       │   ├── __tests__/
│   │       │   │   ├── space.service.spec.ts
│   │       │   │   └── search.component.spec.ts
│   │       │   └── exploration-routing.module.ts
│   │       │
│   │       ├── participation/             ← E2: Participación
│   │       ├── events/                    ← E3: Eventos
│   │       ├── spaces/                    ← E4: Espacios
│   │       ├── reservations/              ← E5: Reservas
│   │       ├── notifications/             ← E6: Notificaciones
│   │       ├── payments/                  ← E7: Pagos
│   │       ├── competitions/              ← E8: Competencias
│   │       └── analytics/                 ← E9: Analítica
│   │
│   ├── assets/                            ← Imágenes, fonts
│   ├── styles/                            ← SCSS global
│   │   ├── variables.scss
│   │   ├── mixins.scss
│   │   └── global.scss
│   │
│   ├── app.component.ts
│   ├── app.config.ts
│   └── app-routing.module.ts
│
├── e2e/                                   ← Playwright tests
│   ├── auth.spec.ts
│   ├── exploration.spec.ts
│   ├── events.spec.ts
│   ├── reservations.spec.ts
│   └── fixtures/
│       ├── test-user.json
│       └── test-spaces.json
│
├── package.json
├── tsconfig.json
├── tsconfig.app.json
├── angular.json                           ← Config Angular CLI
├── playwright.config.ts                   ← Config Playwright
├── Dockerfile                             ← Multi-stage build
├── docker-compose.yml
├── .eslintrc.json
├── .prettierrc.json
│
└── README.md

```

### Infraestructura (Kubernetes opcional)

```
infra/
├── docker-compose.yml                     ← Dev local
│   └── services: backend, frontend, postgres, redis
│
├── docker-compose.prod.yml                ← Producción
│   └── mismo, sin volumes
│
├── k8s/                                   ← Si Kubernetes es mandatorio
│   ├── namespace.yaml
│   ├── configmap.yaml                     ← Env, app properties
│   ├── secret.yaml                        ← DB creds, JWT secret
│   ├── postgres-pvc.yaml                  ← Persistent storage
│   ├── postgres-deployment.yaml
│   ├── redis-deployment.yaml
│   ├── backend-deployment.yaml            ← Spring Boot
│   │   └── livenessProbe, readinessProbe, resources
│   ├── backend-service.yaml               ← LoadBalancer o ClusterIP
│   ├── frontend-deployment.yaml
│   ├── frontend-service.yaml
│   ├── ingress.yaml                       ← Nginx Ingress
│   │   └── TLS, routing backend + frontend
│   ├── hpa.yaml                           ← Auto scaling (si load > 70% CPU)
│   └── monitoring/
│       ├── prometheus-config.yaml
│       └── grafana-deployment.yaml
│
├── terraform/                             ← IaC (opcional para MVP)
│   ├── main.tf
│   ├── variables.tf
│   └── outputs.tf
│
└── scripts/
    ├── deploy.sh                          ← Automatizar deployment
    ├── migrate.sh                         ← Ejecutar migrations
    └── health-check.sh
```

---

## ÉPICAS (Definitivas — NO MODIFICAR)

| Épica | Nombre | Descripción | Dueño | Estado |
|-------|--------|-------------|-------|--------|
| **E1** | Exploración | Descubrimiento y búsqueda de espacios, filtrado, mapa | Jugador | Activa |
| **E2** | Participación | Inscripción, participación, reseñas, calificaciones | Participante | Activa |
| **E3** | Org. Eventos | Creación, edición, gestión de eventos deportivos | Administrador | Activa |
| **E4** | Gestión Espacios | ABM espacios, configuración, disponibilidad | Propietario | Activa |
| **E5** | Reservaciones | Reserva, confirmación, cancelación, historial | Propietario | Activa |
| **E6** | Notificaciones | Push, email, preferencias, historial | Sistema | Activa |
| **E7** | Pagos | Cobros por eventos, transferencias, reconciliación | Sistema | Activa |
| **E8** | Competencias | Torneos, rankings, tablas posiciones, resultados | Administrador | Activa |
| **E9** | Analytics | Estadísticas, reportes, sugerencias automáticas | Analista | Activa |

---

## FLUJO DE HISTORIAS DE USUARIO

### Flujo en Trello

```
Backlog
  ↓ (HU refinadas, con CA listos)
  ↓
En análisis (WIP limit: 3)
  ↓ (Se refinan, se validan)
  ↓
Análisis listo
  ↓
Desarrollo (WIP limit: 3 por dev)
  ↓ (Dev local → commit → PR a rama feature)
  ↓
Desarrollo listo
  ↓
Testing (WIP limit: 3)
  ↓ (QA manual + E2E + métricas)
  ↓
Testing listo
  ↓
Producción
  ↓ (Merge a main, deploy automático)
```

### Flujo Git & Ambientes

```
Dev local              Branch: feature/[E#-H##]-descripcion
  ↓ (git commit)       Includes: unit tests
  ↓ (git push)
GitHub PR              → dev origin
  ↓ (CI automático)    Checks: lint + unit + build (5 min)
  ↓ (Aprobación code review)
Merge a dev            CD automático
  ↓
Ambiente Dev           Backend + Frontend + BD test
  ↓ (Integración tests, QA manual)
  ↓
Ambiente Staging       PR a rama staging
  ↓                    CD automático
                       QA funcional + E2E + Performance
                       Demo/Review con cliente
  ↓ (Aprobación cliente)
Ambiente Prod          PR a rama main (votación equipo)
  ↓                    CD automático con canary o blue-green
  ↓
Monitoring            Métricas en vivo, alertas activas
```

### Estados HU en Trello

Cada HU tiene:

- **Etiqueta de épica:** `[E1]`, `[E2]`, etc.
- **Estado:** `[ANALIZADA]`, `[DESARROLLO]`, `[TESTING]`, `[DONE]`
- **Descripción:** Formato BDD (Como...Quiero...Para)
- **Criterios de aceptación:** JSON o lista ordenada
- **Asignado a:** Dev responsable
- **Links:** PR, tests, documentación
- **Estimación:** Story points (Fibonacci: 1, 2, 3, 5, 8, 13)
- **Bloqueos:** Si depende de otra HU

---

## 🔄 CICLO DE DESARROLLO

### Sprint (2 semanas)

**Día 1 (Lunes) — Planning**

```
1. Review backlog (10 min)
2. Estimar HU nuevas con técnica Planning Poker
3. Seleccionar HU para sprint (capacity: equipo puede hacer X pts)
4. Definir prioridad (negocio + técnico)
5. Asignar devs por épica
```

**Días 2-9 (Martes-Viernes Semana 2)**

```
Diario (15 min):
  - Qué hice ayer
  - Qué haré hoy
  - Bloqueos / ayuda
  
Desarrollo:
  - HU → rama feature
  - Unit tests (TDD): red → green → refactor
  - PR cuando está listo (no esperar al final)
  - Code review: 2 aprobaciones mínimo
  - Merge cuando está clean
```

**Día 10 (Viernes Semana 2) — Retro & Demo**

```
Demo (30 min):
  - Live: qué funciona en staging
  - Feedback cliente
  
Retrospectiva (30 min):
  - Qué salió bien
  - Qué salió mal
  - Acciones para mejorar
```

---

## PATRONES ARQUITECTÓNICOS

### Backend: Capas y Responsabilidades

```
┌─────────────────────────────────────────┐
│          Controller/REST API             │ ← Mapea HTTP → lógica
├─────────────────────────────────────────┤
│          Service (Business Logic)        │ ← Orquestación, validaciones
├─────────────────────────────────────────┤
│          Repository (Data Access)       │ ← Queries a BD
├─────────────────────────────────────────┤
│          Entity (Domain Model)           │ ← Reglas de negocio
└─────────────────────────────────────────┘
```

**Flujo de una HU:**

```
1. Usuario → HTTP Request
2. Controller recibe, valida input (DTO)
3. Llama Service.crearEspacio(dto)
4. Service valida reglas negocio (¿existe propietario? ¿ubicación válida?)
5. Service llama Repository.save(entity)
6. Repository ejecuta Query SQL a PostgreSQL
7. Se devuelve ResponseDTO
8. Controller devuelve HTTP 201 + data
```

### Frontend: Smart vs Dumb Components

```
Smart (Container):                Dumb (Presentational):
├─ Conecta a servicios          ├─ Recibe @Input
├─ Maneja estado (RxJS)         ├─ Emite @Output
├─ Lógica condicional           ├─ Template limpio
└─ 1 por feature                └─ Reutilizable

Ejemplo:
SearchComponent (Smart)
  └─ llama SpaceService
  └─ subscribe a Observable
  └─ pasa datos a SpaceListComponent (Dumb)
```

### DDD: Por épica/dominio

```
Cada épica = paquete autónomo:

domain/exploration/
  ├─ Modelos (Space, SearchCriteria)
  ├─ Servicios (SearchService)
  ├─ Repositorios (SpaceRepository)
  ├─ Controllers (SpaceController)
  └─ Exceptions (SpaceNotFound)

↳ Sin dependencias cruzadas con otros dominios
↳ Cambios en E1 no afectan E2
↳ Escalable a microservicios después
```

---

## 🧪 TESTING STRATEGY

### Pirámide de Testing

```
         ▲
        / \
       /   \  E2E Tests (Playwright)
      /     \ - 10% de casos críticos
     /       \ - Nightly (2-3 hrs)
    /_________\
   /           \
  /    Integr.  \ Integration Tests (TestContainers)
 /     Tests     \ - 30% de flujos complejos
/_________________\ - En cada PR
/                 \
/   Unit Tests     \ Unit Tests (Jasmine/JUnit)
/                   \ - 60% de cobertura
/____________________\ - En cada commit
```

### Cobertura requerida

```
Backend:
  - Unit tests:       ≥80% (obligatorio)
  - Integration:      ≥50% (casos críticos)
  - E2E:              5-10 casos (workflows principales)

Frontend:
  - Unit tests:       ≥75% (obligatorio)
  - E2E:              5-10 escenarios (flujos usuario)

Métrica global: ≥80% código productivo cubierto
```

### Qué testear (prioridades)

```
 CRÍTICO (siempre):
  - Autenticación / Autorización
  - Transacciones de dinero (E7)
  - Validaciones de datos
  - Casos edge (entrada vacía, nula, muy grande)

 IMPORTANTE:
  - Servicios de negocio (E1-E6, E8-E9)
  - Repositorios (queries complejas)
  - Flujos de usuario principales

 NICE-TO-HAVE:
  - Componentes UI simples
  - Utilidades (helpers)
  - Configuración
```

---

## SEGURIDAD MÍNIMA

### Authentication & Authorization

```
Auth Flow:
1. Usuario entra credenciales (user + password)
2. Backend valida con bcrypt (NO SHA256 plano)
3. Si OK → genera JWT (HS256, exp: 1 hora)
4. Frontend guarda token en sessionStorage (NO localStorage)
5. Frontend envía token en header Authorization: Bearer <token>
6. Backend verifica token en cada request (JwtAuthenticationFilter)
7. Si inválido/expirado → 401 Unauthorized

JWT Payload:
{
  "sub": "user-id",
  "email": "user@example.com",
  "roles": ["USER", "ADMIN"],
  "iat": 1234567890,
  "exp": 1234571490
}

Refresh token (opcional):
- Token largo vive (7 días) para obtener nuevo JWT
- Guardado en httpOnly cookie (backend genera nuevo JWT)
```

### Protecciones en API

```
Rate limiting:     100 req/min por IP
CORS:              Solo orígenes conocidos (whitelist)
CSRF:              Token en formularios (si session-based)
Input validation:  Sanitizar todo (NOT NULL, max length, regex)
SQL injection:     JPA + parameterized queries
XSS prevention:    Angular escapa HTML automático
HTTPS:             Obligatorio en prod (certbot + Let's Encrypt)
OWASP ZAP:         En CI/CD (escaneo automático)
Dependencias:      Snyk check + Dependabot
```

---

## MÉTRICAS & MONITOREO

### Métricas de Negocio

```
Usuarios activos mensuales:    Objetivo: +20% sprint
Tasa participación:            Objetivo: >60%
Eventos creados:               Objetivo: >10 por semana
NPS (Net Promoter Score):      Objetivo: >50
Retención:                     Objetivo: >40% mes 2
```

### Métricas Técnicas

```
Code Quality (SonarQube):
  - Coverage:           ≥80% (bloqueador si <75%)
  - Duplicación:        <3% código
  - Code Smells:        <10 major issues
  - Deuda técnica:      <5% de esfuerzo

Performance:
  - P95 latencia API:   <200ms
  - Frontend bundle:    <300KB gzip
  - Lighthouse score:   >80 en todas métricas
  - DB query P95:       <100ms

Availability:
  - Uptime prod:        >99.5%
  - Error rate:         <0.1%
  - Deploy success:     >98%
  - MTTR (mean time to recover): <15 min

CI/CD:
  - Build time:         <10 min
  - Test execution:     <5 min
  - Deploy time:        <5 min
```

### Dashboard (GitHub README + SonarQube)

```
![Build](https://img.shields.io/github/workflow/status/...?label=CI)
![Coverage](https://sonarcloud.io/api/project_badges/measure?...)
![Security](https://sonarcloud.io/api/project_badges/measure?...)
![Uptime](https://img.shields.io/uptimerobot/status/...)
```

---

## FORMATO ANÁLISIS HISTORIAS DE USUARIO

Cuando se analice una HU, devuelve siempre en este JSON:

```json
{
  "id": "E#-H##",
  "nombre": "[E#-H##] Nombre de la Historia",
  "historia_de_usuario": "Como [rol], quiero [acción], para [beneficio]",
  "criterios_de_aceptacion": [
    "CA1: Descripción clara y SMART",
    "CA2: Descripción clara y SMART",
    "CA3: ..."
  ],
  "cambios": [
    "[CAMPO]: Descripción del cambio y por qué",
    "[CAMPO2]: Descripción del cambio y por qué",
    "..."
  ]
}
```

**Estructura `cambios`:**

- Flat array (NO nested)
- Una línea por cambio
- Formato: `"[CAMPO]: [QUÉ CAMBIÓ] porque [RAZÓN BREVE]"`
- Sin análisis INVEST, sin validaciones adicionales, sin recomendaciones

---

## 🔍 VALIDACIÓN INVEST PARA HU

**Antes de mover HU a Desarrollo:**

| Criterio | Qué significa | Validación |
|----------|---------------|-----------|
| **I**ndependent | No depende de otra HU para tener valor | ✅ Se puede desarrollar aislada |
| **N**egotiable | Describe QUÉ, no CÓMO | ✅ Sin detalles técnicos prescriptivos |
| **V**aluable | Aporta valor al usuario/negocio | ✅ Vinculado a épica y visión |
| **E**stimable | Equipo puede estimar con confianza | ✅ Suficiente claridad, sin incógnitas |
| **S**mall | **Cabe en 1 sprint** (≤8 pts) | ✅ No es épica disfrazada |
| **T**esteable | CA verificables automáticamente | ✅ Criterios SMART, sin ambigüedad |

**Red flags:**

```
❌ Palabras como "Administrar", "Gestionar", "Manejar" → Probablemente épica
❌ Criterios vagos: "rápido", "bonito", "fácil" → Reescribir SMART
❌ Múltiples acciones no relacionadas → Fragmentar
❌ Criterios que solo aplican si otra HU fue hecha → Dependencia
❌ Sin rol claro → Reescribir con persona específica
```

---

## DEPLOYMENT STRATEGY

### Dev local

```bash
docker-compose up -d
# Levanta: backend (8080), frontend (4200), postgres, redis

Backend:   http://localhost:8080
Frontend:  http://localhost:4200
Swagger:   http://localhost:8080/swagger-ui.html
```

### Staging (Pre-producción)

```bash
# Branch: staging
git push origin feature/... → PR a staging

# GitHub Actions dispara:
1. CI: lint + unit + integration tests
2. CD: build docker images
3. Deploy a staging con docker-compose prod
4. E2E tests nightly
5. QA manual + demo

Ambiente vivo:  https://staging.saleww.app
```

### Producción

```bash
# Branch: main
git push → PR a main (votación equipo)

# GitHub Actions dispara:
1. CI: todos los tests
2. CD: build images
3. Deploy con canary (10% tráfico) o blue-green
4. Monitoring + alertas vivas
5. Rollback automático si error rate > 1%

Ambiente vivo:  https://saleww.app
```

### Kubernetes (si aplica)

```bash
# Local (minikube)
minikube start
kubectl apply -f infra/k8s/
kubectl port-forward svc/backend-service 8080:8080

# Production
# Usar Railway.app, Render, o DigitalOcean App Platform
# (Kubernetes management sin headache)
```

---

## DOCUMENTACIÓN OBLIGATORIA

Cada PR debe incluir:

```
1. ARCHITECTURE.md       ← Decisiones técnicas (ADR format)
2. API.md               ← Endpoints REST (auto-gen con Swagger)
3. DEPLOYMENT.md        ← Cómo deployar a cada ambiente
4. CONTRIBUTING.md      ← Cómo contribuir
5. DB_SCHEMA.md         ← Diagrama ER, migraciones
6. README.md (backend)  ← Setup, tools, comandos útiles
7. README.md (frontend) ← Setup, storybook, testing
```

---

## COMANDOS RÁPIDOS

### Backend (Spring Boot)

```bash
# Setup
./mvnw clean install

# Dev
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Tests
./mvnw test                           # Unit tests
./mvnw verify                         # Unit + Integration
./mvnw sonar:sonar                    # SonarQube

# Build
./mvnw clean package -DskipTests

# Docker
docker build -t sale-partido-backend:latest .
docker run -p 8080:8080 sale-partido-backend:latest
```

### Frontend (Angular)

```bash
# Setup
npm install

# Dev
ng serve
# http://localhost:4200

# Tests
npm run test                          # Unit tests
npm run e2e                           # Playwright
npm run lint                          # ESLint + Prettier

# Build
ng build --configuration production

# Docker
docker build -t sale-partido-frontend:latest .
docker run -p 80:80 sale-partido-frontend:latest
```

### Docker Compose

```bash
# Dev local
docker-compose up -d

# Con logs
docker-compose up

# Detener
docker-compose down

# Prod
docker-compose -f docker-compose.prod.yml up -d
```

### Git

```bash
# Feature branch
git checkout -b feature/E1-H03-vista-en-mapa

# Commit
git commit -m "[E1-H03] Vista en mapa

- Implementar componente MapComponent
- Integrar Leaflet
- Tests: >80% coverage

Related: #42"

# PR
git push origin feature/E1-H03-vista-en-mapa
# → GitHub UI: crear PR a dev

# Merge (después code review)
git checkout dev
git pull origin dev
git merge feature/E1-H03-vista-en-mapa
git push origin dev
```

---

## CHECKLIST ANTES DE PUSH

- [ ] Branch correcto: `feature/[E#-H##]-descripcion`
- [ ] Tests unitarios: `./mvnw test` o `npm run test` ✅
- [ ] Coverage: ≥80% (líneas nuevas)
- [ ] Lint: sin errores `./mvnw checkstyle:check` o `npm run lint`
- [ ] Build: `./mvnw package` o `ng build` sin warnings
- [ ] Commits limpios: mensaje descriptivo con referencia a issue
- [ ] PR: descripción clara, enlace a issue, screenshot si UI
- [ ] Code review: 2 aprobaciones mínimo
- [ ] Merge: squash o rebase para historia limpia

---

## RECURSOS & REFERENCIAS

### Documentos Internos

- `sale_partido.json` — Trello export (backlog + épicas)
- `Documento_de_visión.pdf` — Requisitos + narrativa
- `Business_Model_Canvas.pdf` — Modelo económico
- `Atributos_de_calidad.docx` — Métricas esperadas

### Libros de Referencia

- **Clean Architecture** (Robert Martin) — Diseño modular
- **Building Evolutionary Architectures** (Ford et al) — CI/CD + testing
- **Agile Estimating and Planning** (Mike Cohn) — Sprints + story points
- **RESTful API Design** — API design patterns

### Tecnologías

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Angular Docs](https://angular.io/docs)
- [PostgreSQL Docs](https://www.postgresql.org/docs/)
- [Docker Docs](https://docs.docker.com/)
- [GitHub Actions](https://docs.github.com/en/actions)
- [Kubernetes Docs](https://kubernetes.io/docs/) (si aplica)
- [Playwright](https://playwright.dev/)
- [SonarQube](https://www.sonarqube.org/features/multi-language-analysis/)

---

## ROLES & RESPONSABILIDADES

| Rol | Responsabilidades |
|-----|-------------------|
| **Tech Lead** | Decisiones arquitectónicas, code review crítico, mentoring |
| **Backend Dev** | HU backend, tests, deployment infrastructure |
| **Frontend Dev** | HU frontend, responsividad, E2E tests |
| **QA/Tester** | Testing manual, casos edge, reportes de bugs |
| **DevOps** | CI/CD, K8s, monitoreo, infraestructura |
| **Product Owner** | Priorización, requirements, demo cliente |

---

## ESCALONAMIENTO DE PROBLEMAS

```
¿CI falla?
  → Revisar logs en GitHub Actions
  → Ejecutar localmente: ./mvnw clean verify
  → Si test falla: arreglar código o test

¿Deploy a prod falló?
  → Rollback automático (check k8s status)
  → Revisar logs en monitoring
  → Hotfix si es crítico, sino esperar próximo sprint

¿Bloqueo técnico?
  → Postear en Slack #engineering
  → Pair programming si es complejo
  → Abrir issue en GitHub con context

¿Deuda técnica alta?
  → Retroalimentación en sprint planning
  → Dedicar 20% tiempo a refactoring
  → SonarQube review en retro
```

---

## FINAL CHECKLIST PARA NUEVOS DEVS

Cuando alguien se suma al equipo:

```
✅ Clonar repo: git clone ...
✅ Setup backend: ./mvnw clean install
✅ Setup frontend: npm install
✅ Docker compose: docker-compose up -d
✅ Verificar apps: http://localhost:8080, http://localhost:4200
✅ Leer ARCHITECTURE.md + CONTRIBUTING.md
✅ Entender épicas (9 dominios)
✅ Entender flujo git (branches, PRs, code review)
✅ Primeras HU: pequeñas (3-5 pts), sin bloqueos
✅ Pairing con tech lead para entender patrones
```

---

## CAMBIOS A ESTE DOCUMENTO

Si necesitás actualizar CLAUDE.md:

1. **Propone cambio** en Slack #engineering
2. **Vota equipo** (mayoría simple)
3. **Ejecuta cambio**, versiona
4. **Comunica** en retrospectiva

**Última revisión:** 29 de abril de 2026  
**Próxima revisión esperada:** Fin de sprint 3

---

**CLAUDE.md — Tu guía de referencia para Sale Partido. Usalo, cítalo, mejoralo.**
