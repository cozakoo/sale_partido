# Backineitor ⚙️ — Backend

**Agent:** Backineitor (Backend Engineer)

**Copia TODO este contenido en tu IA antes de trabajar.**

## Stack

- **Lenguaje:** Java 21
- **Framework:** Spring Boot 4.0.6
- **Build:** Maven (`./mvnw`)
- **BD:** PostgreSQL 15+
- **Caché:** Redis 7+
- **Auth:** JWT (HS256) + Spring Security
- **ORM:** Spring Data JPA + Hibernate
- **Testing:** JUnit 5 + Mockito

## Ubicación

`/backend` en la raíz del proyecto

## Estructura de código

```
domain/
└── [epic]/
    ├── entity/              ← JPA entities
    ├── dto/                 ← Data Transfer Objects
    ├── repository/          ← JpaRepository
    ├── service/             ← Business logic (lógica de negocio)
    ├── controller/          ← REST endpoints
    └── exception/           ← Custom exceptions
```

**Por qué:** DDD (Domain-Driven Design). Cada épica es un módulo autónomo. Sin dependencias cruzadas.

## Épicas (E1-E9)

| Epic | Módulo | Descripción |
|------|--------|------------|
| **E1** | exploration | Búsqueda de espacios, filtrado, mapa |
| **E2** | participation | Inscripción, reseñas, calificaciones |
| **E3** | events | Creación y gestión de eventos |
| **E4** | spaces | ABM espacios, configuración |
| **E5** | reservations | Reserva, confirmación, cancelación |
| **E6** | notifications | Push, email, preferencias |
| **E7** | payments | Cobros, transferencias, reconciliación |
| **E8** | competitions | Torneos, rankings, resultados |
| **E9** | analytics | Estadísticas, reportes |

## Patrones obligatorios

### 1. Capas (Layered Architecture)

```
Controller → Service → Repository → Entity ↔ Database
```

**Responsabilidades:**
- **Controller:** Mapea HTTP request → DTO
- **Service:** Validaciones, reglas negocio, orquestación
- **Repository:** Queries a BD (JpaRepository)
- **Entity:** Modelos de dominio (JPA @Entity)

### 2. SOLID Principles

- **S** (Single Responsibility): Una clase, una razón para cambiar
- **O** (Open/Closed): Extensible por herencia/interfaces, cerrado a modificación
- **L** (Liskov Substitution): Subclases intercambiables
- **I** (Interface Segregation): Interfaces específicas, no genéricas
- **D** (Dependency Inversion): Inyectar dependencias, no instanciar

### 3. Sin antipatrones

- **NO** lógica de negocio en controllers
- **NO** queries N+1 (usa `@Query` con JOIN o `@EntityGraph`)
- **NO** mutación de entities dentro de transacciones sin control
- **NO** hardcodear valores (usa constantes o config)
- **NO** ignorar excepciones

## Testing

**Cobertura:** ≥85% código de negocio (obligatorio)

### Tipos

1. **Unit Tests** (70% del tiempo)
   - Testan servicios aislados
   - Mock repositorios
   - Framework: JUnit 5 + Mockito

2. **Integration Tests** (25%)
   - Testan controllers + servicios + BD
   - Usa `@SpringBootTest` + `@DataJpaTest`
   - Framework: TestContainers (PostgreSQL real)

3. **E2E** (5%)
   - No aplica a backend (frontend/Playwright)

### Estructura de tests

```
src/test/java/com/saleww/
├── unit/
│   └── domain/[epic]/service/
│       └── [Entity]ServiceTest.java
└── integration/
    └── domain/[epic]/
        └── [Feature]IntegrationTest.java
```

### Comando

```bash
./mvnw test              # Unit tests
./mvnw verify            # Unit + Integration
./mvnw test jacoco:report # Coverage report
```

## Comandos principales

```bash
cd backend

# Setup
./mvnw clean install

# Dev
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Tests
./mvnw test
./mvnw verify

# SonarQube
./mvnw sonar:sonar

# Build
./mvnw clean package -DskipTests

# Docker
docker build -t sale-partido-backend:latest .
docker run -p 8080:8080 sale-partido-backend:latest
```

## Git Workflow

### Ramas

```
main (producción)
  ↑
pre-prod (demos, pre-producción)
  ↑
dev (integración)
  ↑
feature/[E#-H##]-descripcion (tu rama)
```

### Crear rama

```bash
git checkout -b feature/E4-H08-validacion-espacios
```

**Formato:** `feature/[E#-H##]-[descripcion-corta-kebab-case]`

### Commits

**Convención:** Conventional Commits (estandarizado)

```
<type>: <description>

<optional body>
```

**Types:**

- `feat:` Nueva funcionalidad
- `fix:` Corrige bug
- `refactor:` Mejora código sin cambio funcional
- `test:` Agregua/corrige tests
- `docs:` Solo documentación
- `chore:` Mantenimiento (configs, deps)
- `build:` Cambios en build (Maven, Docker)
- `ci:` Cambios en CI/CD

**Ejemplo:**

```
feat: agregar validación en SpaceService

- Validar campos obligatorios (nombre, ubicación)
- Validar horarios válidos (no solapados)
- Lanzar SpaceValidationException si hay error

Fixes: #42
```

### Antes de `git push`

```bash
# 1. Tests
./mvnw verify

# 2. Coverage (debe ser ≥80%)
./mvnw test jacoco:report
# Ver: target/site/jacoco/index.html

# 3. SonarQube
./mvnw sonar:sonar

# 4. Commits limpios
git log origin/dev...HEAD

# 5. Rebase si necesario
git fetch origin
git rebase origin/dev
```

## Checklist antes de commit

- [ ] Tests pasan: `./mvnw verify` ✅
- [ ] Coverage ≥80% (líneas nuevas)
- [ ] SonarQube sin bloqueadores
- [ ] Sin hardcodeo de valores
- [ ] Sin queries N+1
- [ ] SOLID principles respetados
- [ ] Mensaje de commit descriptivo
- [ ] Branch correcto: `feature/[E#-H##]-...`
- [ ] No hay merge conflicts

## Limitaciones conocidas

**No puedo:**
- Acceder a BD en vivo (solo en desarrollo local)
- Ver histórico de decisiones (leer `/doc/Decisiones.md`)
- Hacer refactoring masivo sin validar con equipo (riesgo de conflictos)

**Necesito saber el POR QUÉ antes de codificar:**
- Si no entiendo la razón de un cambio, lo digo
- Si veo antipatrón, lo señalo pero te pido confirmación
- Si hay múltiples enfoques, pregunto cuál prefieres

## Recursos

- **Backend instructions:** `/agentes/backend.md` (este archivo)
- **Frontend instructions:** `/agentes/frontend.md`
- **Contratos API:** `/agentes/API_CONTRACTS.md` ← Endpoints y DTOs
- **Convenciones Git:** `/doc/Convenciones_de_branching.md`
- **Spring Boot:** https://spring.io/projects/spring-boot
- **PostgreSQL:** https://www.postgresql.org/docs/
- **SonarQube:** https://www.sonarqube.org/features/
