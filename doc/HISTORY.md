# 📜 Registro de Decisiones Arquitectónicas (ADR) y Cambios

Este archivo registra las decisiones clave tomadas durante el desarrollo de **Sale Partido** y los cambios significativos.

## 📅 Historial de Cambios Recientes

### [2026-05-14] Estandarización y Multi-entorno
- **CI/CD**: Implementación de despliegue automático para el ambiente de Demos (`pre_prod`).
- **Infraestructura**: Creación de namespaces separados en Kubernetes (`salepartido-preprod`).
- **Gobernanza**: Creación de `AGENTS.md` y `GITHUB.md`.

---

## 🏛️ Architectural Decision Records (ADR)

### ADR-001: Estrategia de Caché con Redis
- **Estatus**: Aceptado.
- **Contexto**: El sistema requiere alta disponibilidad y tiempos de respuesta bajos para la búsqueda de espacios deportivos.
- **Decisión**: Usar Redis como caché de lectura (Cache-Aside) con un TTL de 10 minutos para datos frecuentes.
- **Consecuencia**: Menor carga en PostgreSQL, pero requiere lógica de invalidación en las escrituras.

### ADR-002: Despliegue en Kubernetes
- **Estatus**: Aceptado.
- **Contexto**: Necesitamos una forma escalable de desplegar el stack completo (Java + Angular + DB + Redis).
- **Decisión**: Utilizar Kubernetes con contenedores Docker para orquestar los servicios.
- **Consecuencia**: Mayor complejidad inicial pero facilita el escalado horizontal y la gestión de secretos.

### ADR-003: Versionado de Base de Datos con Flyway
- **Estatus**: Aceptado.
- **Contexto**: El uso de `hibernate.ddl-auto=update` es inestable para múltiples entornos.
- **Decisión**: Migrar a Flyway para gestionar migraciones SQL explícitas.
- **Consecuencia**: Trazabilidad total de cambios en el esquema de la base de datos.

---
*Para agregar una nueva decisión, sigue el formato: ADR-XXX: Título.*
