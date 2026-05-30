# Stack Técnico — Detallado

**Versiones exactas y justificación de cada componente.**

---

## Backend

| Componente | Versión | Justificación |
|------------|---------|---------------|
| **Java** | 21 LTS | LTS actual, long-term support, features modernos (records, sealed types) |
| **Spring Boot** | 4.0.6 | Latest stable, Spring 6 base, native compilation |
| **Maven** | 3.8.x | Build reproducible, dependency management |
| **PostgreSQL** | 15+ | ACID guarantees, JSON support, performance |
| **Redis** | 7+ | Fast caching, Streams API, better cluster support |
| **Spring Security** | 6.x | JWT integration, OAuth2 ready |
| **Spring Data JPA** | 3.x | Hibernate 6, modern ORM |
| **JUnit 5** | 5.9+ | Parameterized tests, composable annotations |
| **Mockito** | 5.x+ | Modern mocking, good Spring integration |
| **TestContainers** | 1.19+ | Real DB testing, Docker integration |
| **JaCoCo** | 0.8.x | Coverage reporting, Maven plugin |
| **SonarQube** | 9.x | Code quality gates |
| **Checkstyle** | 10.x | Code style enforcement |

---

## Frontend

| Componente | Versión | Justificación |
|------------|---------|---------------|
| **TypeScript** | 5.9.2 | Type safety, strict mode enabled |
| **Angular** | 20.3.0 | Standalone components (no NgModule), Signals, latest features |
| **Node.js** | 18+ LTS | Long-term support, npm 9+ |
| **SCSS** | 1.x | CSS with variables, nested rules, mixins |
| **Bootstrap** | 5.x | Component library, responsive grid, accessibility |
| **Jasmine** | 5.x | BDD testing framework |
| **Karma** | 6.x | Test runner, watch mode, coverage |
| **Playwright** | 1.40+ | E2E testing, fast, cross-browser |
| **ESLint** | 8.x | Linting, Angular rules |
| **Prettier** | 3.x | Code formatting, consistency |

---

## DevOps / Infraestructura

| Componente | Versión | Justificación |
|------------|---------|---------------|
| **Docker** | 25+ | Container runtime, BuildKit improvements |
| **Docker Compose** | 2.x | Local orchestration, variable substitution |
| **Kubernetes** | 1.31.14 | Production orchestration, high availability |
| **containerd** | 2.2.3 | K8s container runtime (NO Docker daemon) |
| **GitHub Actions** | Latest | CI/CD nativo en GitHub |
| **Debian Linux** | 13 (trixie) | Production OS, LTS support |

---

## Dependencias Críticas

### Backend (pom.xml)

```xml
<!-- Spring Boot -->
<spring-boot-starter-web/>           <!-- REST, Tomcat -->
<spring-boot-starter-data-jpa/>      <!-- Hibernate ORM -->
<spring-boot-starter-security/>      <!-- JWT, Auth -->
<spring-boot-starter-validation/>    <!-- Bean validation -->

<!-- Database -->
<postgresql/>                         <!-- JDBC driver -->

<!-- Cache -->
<spring-boot-starter-data-redis/>    <!-- Redis client -->

<!-- Testing -->
<spring-boot-starter-test/>          <!-- JUnit, Mockito -->
<testcontainers-postgresql/>         <!-- PostgreSQL en tests -->

<!-- Quality -->
<jacoco-maven-plugin/>               <!-- Code coverage -->
<sonar-maven-plugin/>                <!-- SonarQube integration -->
```

### Frontend (package.json)

```json
{
  "dependencies": {
    "@angular/core": "20.3.0",
    "@angular/common": "20.3.0",
    "@angular/platform-browser": "20.3.0",
    "@angular/platform-browser-dynamic": "20.3.0",
    "@angular/router": "20.3.0",
    "@angular/forms": "20.3.0",
    "bootstrap": "5.x",
    "rxjs": "^7.x"
  },
  "devDependencies": {
    "typescript": "5.9.2",
    "@angular/cli": "20.x",
    "jasmine": "5.x",
    "karma": "6.x",
    "@playwright/test": "1.40.x",
    "eslint": "8.x",
    "prettier": "3.x"
  }
}
```

---

## Justificación Arquitectónica

### ¿Por qué Spring Boot 4.x y no 3.x?

- Spring 6 permite `CompletableFuture` sin `@Async`
- Mejor integración con Virtual Threads (Project Loom)
- Native compilation listo para producción
- JWT integrado en Spring Security

### ¿Por qué Angular 20 y no React?

- Framework opinionado (convenciones > configuración)
- Standalone components sin NgModule boilerplate
- Signals para reactividad moderna
- Routing nativo robusto
- TypeScript strict por default

### ¿Por qué Kubernetes con containerd y NO Docker registry?

- Kubernetes usa **containerd** como runtime
- Docker y containerd tienen registros **separados**
- `docker build` → imagen en Docker registry
- K8s **NO ve** imágenes de Docker sin `docker save → ctr import`
- GitHub Actions self-hosted en el mismo servidor → sin registry externo

---

## Alternativas consideradas (Descartadas)

| Componente | Alternativa | Por qué NO |
|-----------|-------------|-----------|
| **Framework Backend** | Spring Boot → Quarkus | Spring Boot mejor para CRUD, más control |
| **Framework Frontend** | Angular → Next.js | Angular es más estructurado para empresa |
| **Base de Datos** | PostgreSQL → MySQL | PostgreSQL tiene mejor JSON support |
| **Caché** | Redis → Memcached | Redis tiene más tipos de dato |
| **ORM** | JPA → MyBatis | JPA es standard, menos boilerplate |
| **K8s Runtime** | containerd → Docker | containerd más eficiente, Docker daemon innecesario |

---

## Instalación de cada componente

### Backend

```bash
# Java 21
java -version
# javac 21.x.x

# Maven
./mvnw -v
# Apache Maven 3.8.x

# PostgreSQL (Docker)
docker run -d -p 5432:5432 \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=xxx \
  -e POSTGRES_DB=salepartido_database \
  postgres:15
```

### Frontend

```bash
# Node.js
node -v    # v18+
npm -v     # v9+

# Angular CLI
npm install -g @angular/cli@20

# Dependencias
npm install
```

### DevOps

```bash
# Docker
docker --version
# Docker version 25.x+

# Kubernetes (kubeadm en Debian)
kubectl version --client
# v1.31.14+

# containerd
ctr --version
# containerd github.com/containerd/containerd v2.2.3
```

---

## Checklist: Versiones correctas antes de commit

- [ ] Java 21: `java -version`
- [ ] Maven 3.8.x: `./mvnw -v`
- [ ] PostgreSQL 15+: `docker ps | grep postgres`
- [ ] Node.js 18+: `node -v`
- [ ] Angular 20.3.0: `ng version`
- [ ] TypeScript 5.9.2: `npm list typescript`

---

## Recursos

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Angular 20 Docs](https://angular.io/docs)
- [PostgreSQL 15](https://www.postgresql.org/docs/15/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Playwright](https://playwright.dev/)
- [SonarQube](https://www.sonarqube.org/features/)
