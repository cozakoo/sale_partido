# Sale Partido

**Plataforma de gestión y reserva de espacios deportivos con participación comunitaria**

> Trabajo práctico integrador de Ingeniería de Software (IF015 — UNPSJB)

---

## 📋 Descripción

**Sale Partido** es una plataforma integral que conecta jugadores, propietarios de espacios deportivos y organizadores de eventos. Permite descubrir espacios, reservar, participar en eventos y competencias, todo con un sistema de notificaciones y pagos integrado.

### Características principales

- **Exploración:** Descubrimiento y búsqueda de espacios deportivos con filtros y vista en mapa
- **Reservas:** Sistema completo de reserva, confirmación y cancelación
- **Eventos:** Creación y gestión de eventos deportivos
- **Participación:** Inscripción, reseñas y calificaciones
- **Competencias:** Torneos, rankings y tablas de posiciones
- **Notificaciones:** Sistema de alertas por push, email y preferencias
- **Pagos:** Cobros integrados, transferencias y reconciliación
- **Analytics:** Estadísticas, reportes y sugerencias automáticas

---

## 🛠️ Stack Técnico

| Capa | Tecnología |
|------|-----------|
| **Backend** | Java 21 + Spring Boot 3.x + Maven |
| **Frontend** | TypeScript 5.x + Angular 17+ + SCSS + TailwindCSS |
| **Base de Datos** | PostgreSQL 15+ |
| **Caché** | Redis 7+ |
| **Autenticación** | JWT (HS256) + Spring Security |
| **API** | REST + Swagger/OpenAPI |
| **Contenedores** | Docker + Docker Compose |
| **CI/CD** | GitHub Actions |
| **Testing** | JUnit 5 + Jasmine/Karma + Playwright (E2E) |
| **Calidad** | SonarQube + Checkstyle + ESLint |

---

## 🚀 Quick Start

### Requisitos previos

- Docker & Docker Compose
- Git
- Java 21+ (opcional si usas Docker)
- Node.js 18+ (opcional si usas Docker)

### Opción 1: Con Docker (recomendado)

```bash
# Clonar repositorio
git clone https://github.com/tu-org/sale-partido.git
cd sale-partido

# Levantar stack completo
docker-compose up -d

# Verificar servicios
docker-compose ps
```

Servicios disponibles:
- **Backend**: http://localhost:8080
- **Frontend**: http://localhost:4200
- **Swagger API**: http://localhost:8080/swagger-ui.html
- **PostgreSQL**: localhost:5432
- **Redis**: localhost:6379

### Opción 2: Setup local

#### Backend

```bash
cd sale-partido-backend

# Instalar dependencias
./mvnw clean install

# Ejecutar en dev
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

Backend estará en `http://localhost:8080`

#### Frontend

```bash
cd sale-partido-frontend

# Instalar dependencias
npm install

# Ejecutar en dev
ng serve
```

Frontend estará en `http://localhost:4200`

---

## 📁 Estructura del Proyecto

```
sale-partido/
├── sale-partido-backend/               # Backend Spring Boot
│   ├── src/main/java/com/saleww/
│   │   ├── domain/                     # DDD: modelos por épica
│   │   │   ├── exploration/            # E1: Búsqueda espacios
│   │   │   ├── participation/          # E2: Participación
│   │   │   ├── events/                 # E3: Eventos
│   │   │   ├── spaces/                 # E4: Gestión espacios
│   │   │   ├── reservations/           # E5: Reservas
│   │   │   ├── notifications/          # E6: Notificaciones
│   │   │   ├── payments/               # E7: Pagos
│   │   │   ├── competitions/           # E8: Competencias
│   │   │   └── analytics/              # E9: Analítica
│   │   ├── shared/                     # Código común
│   │   ├── infrastructure/             # Integraciones externas
│   │   └── config/                     # Configuración
│   ├── src/test/                       # Tests unitarios e integración
│   ├── pom.xml
│   └── Dockerfile
│
├── sale-partido-frontend/              # Frontend Angular
│   ├── src/app/
│   │   ├── core/                       # Guards, interceptors, servicios críticos
│   │   ├── shared/                     # Componentes reutilizables
│   │   └── modules/                    # Features por épica
│   ├── e2e/                            # Tests E2E con Playwright
│   ├── package.json
│   ├── angular.json
│   └── Dockerfile
│
├── infra/                              # Infraestructura
│   ├── docker-compose.yml              # Dev local
│   ├── docker-compose.prod.yml         # Producción
│   └── k8s/                            # Manifiestos Kubernetes (opcional)
│
├── doc/
│   ├── CLAUDE.md                       # Guía completa del proyecto
│   ├── ARCHITECTURE.md                 # Decisiones técnicas (ADR)
│   ├── API.md                          # Documentación API
│   └── DEPLOYMENT.md                   # Guía de despliegue
│
└── README.md                           # Este archivo
```

---

## ✅ Testing

### Ejecutar tests

#### Backend

```bash
cd sale-partido-backend

# Tests unitarios
./mvnw test

# Tests unitarios + integración
./mvnw verify

# Verificar cobertura
./mvnw test jacoco:report

# SonarQube
./mvnw sonar:sonar
```

**Cobertura requerida:** ≥80% código de negocio

#### Frontend

```bash
cd sale-partido-frontend

# Tests unitarios
npm run test

# Tests E2E
npm run e2e

# Lint + Prettier
npm run lint
```

**Cobertura requerida:** ≥75% código de negocio

---

## 🔄 Flujo de Trabajo

### Ramas

```
main (producción)
├── staging (pre-producción)
└── dev (integración)
    └── feature/[E#-H##]-descripcion (trabajo diario)
```

### Proceso de desarrollo

1. **Crear rama feature**
   ```bash
   git checkout -b feature/E1-H03-vista-mapa
   ```

2. **Desarrollar con TDD**
   - Escribir test primero
   - Implementar lo mínimo para pasar
   - Refactorizar

3. **Commit descriptivo**
   ```bash
   git commit -m "[E1-H03] Vista en mapa

   - Implementar MapComponent con Leaflet
   - Tests: 85% coverage
   - Integración con SpaceService"
   ```

4. **Push y Pull Request**
   ```bash
   git push origin feature/E1-H03-vista-mapa
   ```
   - Descripción clara de cambios
   - Enlace a issue/HU en Trello
   - Screenshots si es UI

5. **Code Review**
   - Mínimo 2 aprobaciones
   - CI/CD debe pasar ✅

6. **Merge a dev**
   - Se dispara CD automático a ambiente dev

---

## 📊 Épicas del Proyecto

| Épica | Nombre | Descripción |
|-------|--------|-------------|
| **E1** | Exploración | Búsqueda de espacios, filtrado, mapa |
| **E2** | Participación | Inscripción, reseñas, calificaciones |
| **E3** | Org. Eventos | Creación y gestión de eventos |
| **E4** | Gestión Espacios | ABM espacios, configuración |
| **E5** | Reservaciones | Reserva, confirmación, cancelación |
| **E6** | Notificaciones | Push, email, preferencias |
| **E7** | Pagos | Cobros, transferencias, reconciliación |
| **E8** | Competencias | Torneos, rankings, resultados |
| **E9** | Analytics | Estadísticas y reportes |

---

## 🔐 Seguridad

### Authentication

- **JWT (HS256)** con expiración de 1 hora
- Tokens en `sessionStorage` (NO localStorage)
- Refresh tokens opcionales (httpOnly cookies)

### Protecciones

- ✅ Rate limiting: 100 req/min por IP
- ✅ CORS: Solo orígenes conocidos
- ✅ Input validation en todos los endpoints
- ✅ SQL injection prevention (JPA parameterized queries)
- ✅ XSS prevention (Angular escapa HTML)
- ✅ HTTPS obligatorio en producción
- ✅ OWASP ZAP escaneo en CI/CD

---

## 📈 Métricas

### Técnicas

| Métrica | Objetivo |
|---------|----------|
| **Coverage** | ≥80% (backend), ≥75% (frontend) |
| **Code Duplication** | <3% |
| **Code Smells** | <10 major issues |
| **API Latency P95** | <200ms |
| **Frontend Bundle** | <300KB gzip |
| **Uptime** | >99.5% producción |
| **Error Rate** | <0.1% |

### Negocio

| Métrica | Objetivo |
|---------|----------|
| Usuarios activos mensuales | +20% por sprint |
| Tasa de participación | >60% |
| NPS (Net Promoter Score) | >50 |
| Retención mes 2 | >40% |

---

## 🐛 Troubleshooting

### CI/CD falla

```bash
# Ejecutar localmente
./mvnw clean verify          # Backend
npm run test && npm run lint # Frontend
```

### Puerto ocupado

```bash
# Backend (8080)
lsof -i :8080
kill -9 <PID>

# Frontend (4200)
ng serve --port 4300
```

### BD sin datos de test

```bash
# Reimportar datos
docker-compose exec postgres psql -U postgres -d sale_partido -f /docker-entrypoint-initdb.d/init.sql
```

---

## 📚 Documentación

- **[ARCHITECTURE.md](doc/ARCHITECTURE.md)** — Decisiones técnicas (ADR)
- **[API.md](doc/API.md)** — Endpoints REST (Swagger auto-generated)
- **[DEPLOYMENT.md](doc/DEPLOYMENT.md)** — Guía de despliegue a cada ambiente
- **[CONTRIBUTING.md](doc/CONTRIBUTING.md)** — Cómo contribuir
- **[CLAUDE.md](doc/CLAUDE.md)** — Guía completa para devs

---

## 🤖 Agentes & Contexto del Proyecto

Para que los agentes (Claude o automatizaciones) tengan contexto completo del proyecto, deben consumir:

### Archivo de referencia: `doc/CLAUDE.md`

El archivo **`doc/CLAUDE.md`** contiene la **fuente única de verdad** del proyecto:

```markdown
- Arquitectura técnica y stack decisivo
- Estructura de directorios (backend, frontend, infra)
- 9 épicas del proyecto (E1-E9)
- Flujo de desarrollo (sprints, ramas, PRs)
- Patrones arquitectónicos (capas, DDD)
- Testing strategy (pirámide, cobertura)
- Seguridad mínima (JWT, protecciones)
- Métricas y monitoreo
- Deployment strategy
- Checklist y comandos rápidos
```

### Cómo usar para agentes

1. **Lectura inicial**: Cualquier agente debe leer `doc/CLAUDE.md` para entender:
   - Estructura del proyecto
   - Épica a desarrollar
   - Patrones a seguir
   - Convenciones de código

2. **Contexto de HU**: Cuando se trabaja en una Historia de Usuario (HU):
   - Identificar épica (`E1`, `E2`, etc.)
   - Validar criteria con formato INVEST
   - Seguir patrón DDD + capas
   - Aplicar testing strategy (80%+ coverage)

3. **Validaciones antes de commit**:
   - ✅ Branch naming: `feature/[E#-H##]-descripcion`
   - ✅ Tests: `./mvnw test` + `npm run test`
   - ✅ Lint: sin errores
   - ✅ Commit message: `[E#-H##] Descripción`

### Secciones clave a consumir

| Sección | Para qué | Agentes relevantes |
| --- | --- | --- |
| **ARQUITECTURA TÉCNICA** | Entender stack | Backend/Frontend devs |
| **ESTRUCTURA DIRECTORIOS** | Dónde ir | Cualquiera |
| **ÉPICAS (E1-E9)** | Contexto negocio | Product/QA |
| **PATRONES ARQUITECTÓNICOS** | Cómo diseñar | Architects |
| **TESTING STRATEGY** | Qué testear | QA/devs |
| **FLUJO DESARROLLO** | Sprint & PRs | DevOps/leads |
| **SEGURIDAD MÍNIMA** | Qué validar | Security reviewers |

---

## 🤝 Contribuir

### Pasos

1. Fork el repo
2. Crea rama feature: `git checkout -b feature/E1-H03-...`
3. Commit tus cambios: `git commit -m "[E1-H03] Descripción"`
4. Push a la rama: `git push origin feature/...`
5. Abre Pull Request a `dev`

### Checklist antes de push

- [ ] Tests ejecutan ✅
- [ ] Coverage ≥80% (líneas nuevas)
- [ ] Lint sin errores
- [ ] Build exitoso
- [ ] Commits limpios y descriptivos
- [ ] PR con descripción clara
- [ ] 2+ code reviews aprobadas

---

## 📞 Contacto & Equipo

- **Tech Lead**: Coordinación arquitectónica
- **Backend Devs**: Desarrollo backend + tests
- **Frontend Devs**: Desarrollo frontend + responsividad
- **QA/Testers**: Testing manual + edge cases
- **DevOps**: CI/CD + infraestructura
- **Product Owner**: Priorización + requisitos

---

## 📄 Licencia

[Especificar licencia del proyecto]

---

## 📅 Estado

- **Última actualización**: 29 de abril de 2026
- **Sprint actual**: Scrum + 2 semanas de sprints
- **Estado del MVP**: En desarrollo

---

**Sale Partido** — Ingeniería de software profesional en acción ⚽🏀
