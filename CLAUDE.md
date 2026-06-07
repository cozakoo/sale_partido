# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Sale Partido — plataforma de reserva de espacios deportivos. Trabajo integrador de Ingeniería de Software (IF015, UNPSJB). Monorepo con backend Spring Boot (Java 21) y frontend Angular 20.

## Commands

### Backend (`cd backend`)
```bash
./mvnw spring-boot:run          # Levantar servidor (puerto 8080)
./mvnw test                     # Correr tests
./mvnw verify                   # Tests + SonarQube
./mvnw test jacoco:report        # Tests con reporte de coverage
./mvnw test -Dtest=NombreTest   # Un solo test
```

### Frontend (`cd frontend`)
```bash
npm install                     # Instalar dependencias
ng serve                        # Dev server (puerto 4200)
npm run test                    # Tests (Jasmine)
npm run e2e                     # E2E tests (Playwright)
npm run lint                    # ESLint
```

### Docker (stack completo)
```bash
docker-compose up -d
```
Swagger: http://localhost:8080/swagger-ui.html

## Architecture

### Backend — DDD por epica

Package base: `io.github.salepartido.api`

```
domain/
  [epica]/               # locales, events, reservations, etc.
    model/               # Entidades JPA (con Lombok @Getter/@Setter)
    repository/          # Interfaces Spring Data JPA
    service/             # Logica de negocio (regla de oro: thin controllers)
    controller/
      dto/               # Request/Response DTOs
      mapper/            # Mappers de entidad <-> DTO
      validator/         # Custom validators con anotaciones
infrastructure/
  config/                # SecurityConfiguration, OpenApiConfig, AppConstants
  error/                 # GlobalExceptionHandler
devtools/                # SeedService y DevController (solo dev)
```

- Entidades usan UUID como PK (`@GeneratedValue(strategy = GenerationType.UUID)`)
- Lombok en entidades: `@Getter`, `@Setter`, `@NoArgsConstructor`
- Inyeccion via constructor (no `@Autowired` en campos)
- `GlobalExceptionHandler` centraliza respuestas de error
- `BusinessRule` interface en `service/rule/` para reglas de dominio extraibles

### Frontend — Features por epica

```
src/app/
  core/
    Constantes.ts        # URLs de endpoints (ENDPOINT_LOCALES, etc.)
  features/
    [epica]/
      pages/             # Componentes-pagina (rutas directas)
      components/        # Componentes reutilizables dentro de la feature
      services/          # Servicios HTTP (providedIn: 'root')
      models/            # Interfaces TypeScript (tipos de dominio)
      routes.ts          # Rutas lazy de la feature
  app.routes.ts          # Rutas raiz (agrupa features)
  app.config.ts          # Bootstrap standalone
```

- Componentes standalone (sin NgModules). NO poner `standalone: true` en el decorador (es default en Angular 20).
- Estado con `signal()` y `computed()`. No usar `mutate()`, usar `update()` o `set()`.
- DI con `inject()`, no constructor injection.
- Templates: `@if`, `@for`, `@switch` (control flow nativo, no `*ngIf`/`*ngFor`).
- `ChangeDetectionStrategy.OnPush` en todos los componentes.
- `class` bindings en lugar de `ngClass`; `style` en lugar de `ngStyle`.

## Git Workflow

**Branch:** `feature/E[#]-H[##]-descripcion-corta`
**Commit:** `feat(E4): descripcion` (Conventional Commits + referencia a epica)

Los git hooks validan el formato al hacer commit. No usar `--no-verify`.

## Testing

- **Backend:** JUnit 5 + Mockito. Tests en `src/test/java/.../features/` (por feature) y `.../etc/` (integración).
- **Frontend:** Jasmine (unit) + Playwright (E2E).
- **Coverage minimo:** 80% backend, 75% frontend.

## Key Docs

- `agents/backend.md` — guia completa para backend (patrones, antipatrones, ejemplos)
- `agents/frontend.md` — guia completa para frontend
- `agents/API_CONTRACTS.md` — contrato de endpoints backend <-> frontend
- `documentation/decisions/Decisiones.md` — decisiones arquitectonicas tomadas
- `documentation/architecture/STACK.md` — stack tecnico detallado
