# Kuberator ☸️ — DevOps / Infraestructura

**Agent:** Kuberator (Infrastructure & DevOps Engineer)

**Copia TODO este contenido en tu IA antes de trabajar en DevOps.**

## Stack

- **Servidor:** Debian GNU/Linux 13 (trixie)
- **Orquestación:** Kubernetes 1.31.14 (kubeadm)
- **Runtime:** containerd 2.2.3 (NO Docker daemon)
- **CI/CD:** GitHub Actions + self-hosted runner
- **BD:** PostgreSQL 15+
- **Caché:** Redis 7+

## Entorno en vivo

| Componente | Detalle |
|-----------|---------|
| **IP privada** | 192.168.0.140 |
| **IP pública** | 138.36.96.63 |
| **Namespace K8s** | `salepartido` |
| **Frontend** | http://138.36.96.63:31167 |
| **Backend API** | http://138.36.96.63:32328 |

## Puertos NodePort

| Servicio | Puerto interno | Puerto externo |
|----------|---------------|----------------|
| Frontend (nginx) | 80 | 31167 |
| Backend (Spring Boot) | 8080 | 32328 |
| PostgreSQL | 5432 | ClusterIP (interno) |
| Redis | 6379 | ClusterIP (interno) |

## Deploy automático (GitHub Actions)

### Trigger

```
Push a rama main → deploy automático
```

### Proceso

1. **Build imágenes Docker**
   ```bash
   docker build -t salepartido-backend:latest ./backend
   docker build -t salepartido-frontend:latest ./frontend
   ```

2. **Exportar e importar a containerd** (crucial)
   ```bash
   docker save salepartido-backend:latest -o /tmp/backend.tar
   sudo ctr -n k8s.io images import /tmp/backend.tar
   ```

3. **Aplicar manifests K8s**
   ```bash
   kubectl apply -f k8s/
   ```

4. **Restart deployments**
   ```bash
   kubectl rollout restart deployment/salepartido-backend -n salepartido
   kubectl rollout restart deployment/salepartido-frontend -n salepartido
   ```

### POR QUÉ containerd, NO Docker registry

- Kubernetes usa **containerd** como runtime, NO Docker daemon
- Docker y containerd tienen registros **separados**
- `docker build` → imagen en Docker registry
- K8s **NO ve** imágenes en Docker registry automáticamente
- Solución: `docker save` → `ctr import` → K8s ve la imagen

**Si salteas este paso, el deploy falla silenciosamente** (K8s descarga `latest` pero sigue usando versión vieja en caché).

## Runner de CI/CD

- **Ubicación:** `/home/dev/dev/sp/actions-runner`
- **Usuario:** dev (con permiso sudo para ctr sin contraseña)
- **Config sudoers:** `/etc/sudoers.d/ctr-nopasswd → dev ALL=(ALL) NOPASSWD: /usr/bin/ctr`
- **Servicio:** `sudo systemctl status actions.runner.cozakoo-sale_partido.debian-srv`

### Si el runner no corre

```bash
cd /home/dev/dev/sp/actions-runner
sudo ./svc.sh status
sudo ./svc.sh start
```

## Configuración backend

### ConfigMap

```yaml
# k8s/configmap.yaml
SPRING_PORT=8080
SPRING_CORS=http://138.36.96.63:31167,http://localhost:4200
POSTGRES_USER=admin
POSTGRES_DATABASE_NAME=salepartido_database
REDIS_HOST=salepartido-redis
REDIS_PORT=6379
```

### Secret

```yaml
# k8s/secret.yaml
POSTGRES_PASSWORD=***
SPRING_JWT_SECRET_KEY=***
REDIS_PASSWORD=***
```

**Regla:** Passwords, JWT secret, credenciales → SOLO en Secret, NUNCA en repo

## Configuración frontend

### Backend URL (problema conocido)

Frontend tiene URL del backend **hardcodeada** en `src/app/app.ts`:

```typescript
private readonly BACKEND_URL = 'http://138.36.96.63:32328';
```

**Problema:** Si cambia IP del servidor → hay que actualizar URL y redesplegar

**Solución futura:** Usar environment.ts o variable de entorno en nginx (add to backlog)

## Endpoints del backend

```
GET /             → Listar todos (con caché Redis)
POST /            → Crear registro y limpiar caché
```

## Comandos útiles

### Ver estado

```bash
# Todos los pods
kubectl get pods -n salepartido

# Status de nodos
kubectl get nodes

# Status del runner
sudo systemctl status actions.runner.cozakoo-sale_partido.debian-srv
```

### Logs

```bash
# Backend
kubectl logs -f deployment/salepartido-backend -n salepartido

# Frontend
kubectl logs -f deployment/salepartido-frontend -n salepartido

# Pod específico
kubectl logs <pod-name> -n salepartido
```

### Imágenes en containerd

```bash
# Listar imágenes
sudo ctr -n k8s.io images ls | grep salepartido

# Eliminar imagen vieja
sudo ctr -n k8s.io images rm salepartido-backend:old-tag
```

### Reimportar imagen manualmente

```bash
docker save salepartido-frontend:latest -o /tmp/frontend.tar
sudo ctr -n k8s.io images import /tmp/frontend.tar
kubectl delete pod -l app=frontend -n salepartido --grace-period=0 --force
```

### Acceder a BD

```bash
kubectl port-forward svc/salepartido-postgres 5432:5432 -n salepartido
psql -h localhost -U admin -d salepartido_database
```

### Reimportar archivos frontend (sin rebuild)

Si después de deploy el frontend sigue con JS viejo:

```bash
docker create --name temp salepartido-frontend:latest
docker cp temp:/usr/share/nginx/html/frontend/browser /tmp/html
docker rm temp
POD=$(kubectl get pods -n salepartido -l app=frontend -o jsonpath='{.items[0].metadata.name}')
kubectl cp /tmp/html/. salepartido/$POD:/usr/share/nginx/html/
```

## Troubleshooting

### Error 500 en backend

```bash
# 1. Ver logs
kubectl logs deployment/salepartido-backend -n salepartido

# 2. Verificar servicios
kubectl get pods -n salepartido

# 3. Verificar conectividad
kubectl exec deployment/salepartido-backend -n salepartido -- curl redis:6379
```

### Frontend muestra JS viejo

```bash
# 1. Ver qué imagen usa el pod
kubectl get pod -n salepartido -l app=frontend -o jsonpath='{.items[0].status.containerStatuses[0].imageID}'

# 2. Si está vieja, reimportar archivos (ver comando arriba)
```

### Imágenes no se actualizan

**Causa más común:** No corriste `docker save → ctr import`

```bash
# Verificar
sudo ctr -n k8s.io images ls | grep salepartido

# Si la imagen vieja sigue ahí, eliminarla y reimportar
sudo ctr -n k8s.io images rm salepartido-backend:latest
docker save salepartido-backend:latest -o /tmp/backend.tar
sudo ctr -n k8s.io images import /tmp/backend.tar
kubectl rollout restart deployment/salepartido-backend -n salepartido
```

### Runner de CI/CD no corre

```bash
cd /home/dev/dev/sp/actions-runner
sudo ./svc.sh status
sudo ./svc.sh restart
```

## Estructura de archivos

```
k8s/
├── namespace.yaml              ← Namespace: salepartido
├── configmap.yaml              ← Variables (no secretos)
├── secret.yaml                 ← Passwords, tokens (GITIGNORE)
├── postgres.yaml               ← StatefulSet PostgreSQL
├── redis.yaml                  ← StatefulSet Redis
├── backend-deployment.yaml     ← Deployment Spring Boot
├── backend-service.yaml        ← Service NodePort
├── frontend-deployment.yaml    ← Deployment nginx
├── frontend-service.yaml       ← Service NodePort
└── ingress.yaml                ← Ingress (opcional, actualmente NodePort)

.github/workflows/
└── deploy.yml                  ← CI/CD: build → import → kubectl apply
```

## Limitaciones conocidas

**No puedo:**
- Acceder a servidor en vivo sin SSH
- Cambiar configuración sin tocar archivos
- Saber credenciales de Secret sin acceso K8s
- Ver qué hay en BD sin port-forward

**Necesito saber:**
- Si el servidor tiene nueva IP antes de cambiar código
- Qué cambios en manifests se necesitan
- Si hay caché de imágenes a limpiar

## Recursos

- **Backend instructions:** `/agents/backend.md`
- **Frontend instructions:** `/agents/frontend.md`
- **Git workflow:** `/documentation/workflow/Convenciones_de_branching.md`
- **Kubernetes:** https://kubernetes.io/documentation/
- **containerd:** https://containerd.io/documentation/
- **GitHub Actions:** https://docs.github.com/en/actions
