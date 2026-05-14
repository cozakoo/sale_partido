# 🐙 Integración con GitHub y CI/CD

Este documento resume los servicios de integración, plugins y el flujo de trabajo automatizado del proyecto **Sale Partido**.

## 🏗️ Flujo de CI/CD (GitHub Actions)

El proyecto utiliza GitHub Actions para automatizar las pruebas y los despliegues.

### Workflows Principales:
1.  **`build.yml` (SonarQube)**:
    *   **Trigger**: Push a `main`, `pre_prod`, `dev`.
    *   **Función**: Compila el código, ejecuta pruebas unitarias y envía el análisis de calidad a SonarQube.
2.  **`deploy.yml` (Despliegue K8s)**:
    *   **Trigger**: Push a `main` (Producción) o `pre_prod` (Demos).
    *   **Ambientes**:
        *   **Producción**: Namespace `salepartido`.
        *   **Demos/Testing**: Namespace `salepartido-preprod`.

## 🔌 Plugins y Aplicaciones Recomendadas

Para mantener la salud del repositorio, se recomienda habilitar:

| Plugin | Propósito | Estado |
| :--- | :--- | :--- |
| **Dependabot** | Actualización automática de dependencias vulnerables. | Sugerido |
| **SonarCloud** | Análisis de código estático y cobertura de tests. | ✅ Configurado |
| **Snyk** | Escaneo de seguridad en contenedores y código. | Sugerido |
| **GitHub Actions Bot** | Notificaciones de fallos en el pipeline. | ✅ Configurado |

## 🔑 Secretos del Repositorio (Actions Secrets)
Para que los despliegues funcionen, se deben configurar los siguientes secretos en GitHub:
- `SONAR_TOKEN`: Token para el análisis de SonarQube.
- `SONAR_HOST_URL`: URL del servidor de SonarQube.
- `KUBECONFIG` (si se usa runner externo): Configuración para acceder al cluster K8s.

## 📌 Convenciones de Pull Request
1. **Título**: `[E#-H##] Descripción corta` (Ej: `[E1-H01] Login de usuario`).
2. **Requisitos**: 
    - Pasar todos los checks de CI.
    - Al menos 1 aprobación de un compañero o revisión por un agente de IA.
    - Cobertura de tests no debe disminuir.

---
*Última actualización: 14/05/2026*
