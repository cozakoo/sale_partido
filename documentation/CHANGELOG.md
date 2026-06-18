# <!-- Encabezado de documentación -->
<div align="center">
	<img src="assets/logo.png" alt="Logo" width="140" />

	<p>
		<img src="https://img.shields.io/badge/build-passing-brightgreen" alt="build" />
		<img src="https://img.shields.io/badge/coverage-80%25-yellow" alt="coverage" />
	</p>
</div>

---

# Registro de Cambios

Todos los cambios notables de este proyecto serán documentados en este archivo.

El formato se basa en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto sigue [Versionado Semántico](https://semver.org/spec/v2.0.0/).

## [Sin Liberar]

### Setup & Documentación
- Reorganizado directorio de scripts en subdirectorios lógicos (setup/, github/, git/, changelog/, sql/)
- Removida validación automática de commits mediante git hooks (los commits ahora son libres)
- Mejorado `create-branch.sh` para soportar múltiples tipos de rama (feature, bugfix, hotfix, refactor, chore, test)
- Automatizada la sincronización de GitHub Projects al crear ramas
- Migrados archivos de datos de GitHub Projects a `doc/.local/data/` (almacenamiento local, sin versión)
- Actualizada toda la documentación para reflejar cambios de setup
- Clarificado requisito de Node.js 18+ y su justificación
- Agregado lanzador centralizado de scripts (`./scripts/run.sh`)
- Documentadas formas recomendadas de ejecutar scripts
- Removidas credenciales hardcodeadas de `.env.example`

### Corregido
- Corregidas inconsistencias entre documentación e implementación
- Removidas referencias obsoletas a git-hooks en instrucciones de setup

---

## [0.0.1] - 2026-05-30

### Agregado
- Setup inicial del proyecto con Java 21, Spring Boot 4.0.6, Angular 20.3
- Autenticación y autorización de usuarios (JWT)
- Integración de PostgreSQL y Redis
- Manifiestos de despliegue en Kubernetes
- CI/CD con GitHub Actions (SonarQube, pruebas E2E con Playwright)
- API RESTful con documentación OpenAPI/Swagger
- Interfaz Material Design con Bootstrap 5
- Pruebas JUnit 5 con Mockito y TestContainers

### Infraestructura
- Contenedores Docker para backend, frontend, PostgreSQL, Redis
- Manifiestos de Kubernetes con configmaps y secrets
- Runner de GitHub Actions autohospedado
- Análisis de calidad de código con SonarQube

---

[Sin Liberar]: https://github.com/cozakoo/sale-partido/compare/v0.0.1...HEAD
[0.0.1]: https://github.com/cozakoo/sale-partido/releases/tag/v0.0.1
