# Infraestructura de Producción - Sale Partido

## Entorno actual

| Componente | Detalle |
|-----------|---------|
| **Servidor** | Debian GNU/Linux 13 (trixie) |
| **IP privada** | 192.168.0.140 |
| **IP pública** | 138.36.96.63 |
| **Runtime K8s** | containerd 2.2.3 (NO Docker) |
| **Kubernetes** | v1.31.14 (kubeadm) |
| **Namespace** | `salepartido` |

## URLs de acceso

| Servicio | URL |
|---------|-----|
| **Frontend** | http://138.36.96.63:31167 |
| **Backend API** | http://138.36.96.63:32328 |

## Puertos NodePort

| Servicio | Puerto interno | Puerto externo (NodePort) |
|---------|--------------|--------------------------|
| Frontend (nginx) | 80 | 31167 |
| Backend (Spring Boot) | 8080 | 32328 |
| PostgreSQL | 5432 | ClusterIP (interno) |
| Redis | 6379 | ClusterIP (interno) |

## GitHub Actions CI/CD

El despliegue automático corre en un **self-hosted runner** en el propio servidor.

- **Runner ubicado en:** `/home/dev/dev/sp/actions-runner`
- **Trigger:** push a rama `main`
- **Workflow:** `.github/workflows/deploy.yml`

### Proceso de deploy automático

Cada push a `main` ejecuta:
1. Build de imagen Docker del backend (`Dockerfile.prod`)
2. Build de imagen Docker del frontend (`Dockerfile.prod`)
3. Exportar imágenes con `docker save` e importarlas a containerd con `sudo ctr -n k8s.io images import`
4. Aplicar manifests de Kubernetes (`kubectl apply -f k8s/`)
5. Restart de deployments para usar las nuevas imágenes
6. Copia directa de archivos compilados del frontend al pod

### Por qué importamos a containerd manualmente

El cluster K8s usa **containerd** como runtime, no Docker. Docker y containerd tienen registros de imágenes separados. Una imagen buildeada con `docker build` NO está disponible automáticamente para K8s. El proceso correcto es:

```bash
docker build -t salepartido-frontend:latest ./frontend
docker save salepartido-frontend:latest -o /tmp/frontend.tar
sudo ctr -n k8s.io images import /tmp/frontend.tar
kubectl rollout restart deployment/salepartido-frontend -n salepartido
```

### Permiso sudo configurado

El usuario `dev` tiene permiso para correr `ctr` sin contraseña:
```
/etc/sudoers.d/ctr-nopasswd → dev ALL=(ALL) NOPASSWD: /usr/bin/ctr
```

## Variables de entorno del backend

Configuradas en `k8s/configmap.yaml` y `k8s/secret.yaml`:

```
SPRING_PORT=8080
SPRING_CORS=http://138.36.96.63:31167,http://localhost:4200,...
POSTGRES_USER=admin
POSTGRES_DATABASE_NAME=salepartido_database
REDIS_HOST=salepartido-redis
REDIS_PORT=6379
```

Los secretos (passwords, JWT key) están en el Secret de K8s, no en el repo.

## Configuración del frontend

El frontend apunta al backend con URL hardcodeada en `src/app/app.ts`:

```typescript
private readonly BACKEND_URL = 'http://138.36.96.63:32328';
```

**Nota:** cuando se cambie la IP del servidor hay que actualizar esta URL y redesplegar.

## Estructura de los endpoints del backend

El `HelloWorldController` expone:
- `GET /` → lista todos los registros (con caché Redis)
- `POST /` → crea un registro y limpia caché

## Comandos útiles en producción

```bash
# Ver estado de todos los pods
kubectl get pods -n salepartido

# Ver logs del backend
kubectl logs -f deployment/salepartido-backend -n salepartido

# Ver logs del frontend
kubectl logs -f deployment/salepartido-frontend -n salepartido

# Verificar imágenes en containerd
sudo ctr -n k8s.io images ls | grep salepartido

# Reimportar imagen manualmente
docker save salepartido-frontend:latest -o /tmp/frontend.tar
sudo ctr -n k8s.io images import /tmp/frontend.tar
kubectl delete pod -l app=frontend -n salepartido --grace-period=0 --force

# Ver status del runner de CI/CD
sudo systemctl status actions.runner.cozakoo-sale_partido.debian-srv

# Acceder a PostgreSQL
kubectl port-forward svc/salepartido-postgres 5432:5432 -n salepartido
psql -h localhost -U admin -d salepartido_database
```

## Troubleshooting conocido

### El frontend usa imagen vieja (JS con hash antiguo)
K8s cachea imágenes en containerd. Si después de un deploy el frontend sigue con el archivo JS viejo:
```bash
# Ver qué imagen usa el pod
kubectl get pod -n salepartido -l app=frontend -o jsonpath='{.items[0].status.containerStatuses[0].imageID}'

# Copiar archivos compilados directamente al pod (sin reimportar imagen)
docker create --name temp salepartido-frontend:latest
docker cp temp:/usr/share/nginx/html/frontend/browser /tmp/html
docker rm temp
POD=$(kubectl get pods -n salepartido -l app=frontend -o jsonpath='{.items[0].metadata.name}')
kubectl cp /tmp/html/. salepartido/$POD:/usr/share/nginx/html/
```

### Error 500 en el backend
Revisar logs: `kubectl logs deployment/salepartido-backend -n salepartido`
Verificar que Redis y PostgreSQL estén corriendo: `kubectl get pods -n salepartido`

### El runner de CI/CD no corre
```bash
cd /home/dev/dev/sp/actions-runner
sudo ./svc.sh status
sudo ./svc.sh start
```
