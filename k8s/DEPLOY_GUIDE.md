# 🚀 Guía de Despliegue Frontend + Backend en K8s

## Prerrequisitos

```bash
# Verifica que tengas kubectl y minikube (o docker-desktop)
kubectl version --client
minikube status

# Si usas minikube, activa el docker env
eval $(minikube docker-env)
```

## Paso 1: Buildear imágenes Docker

```bash
# Desde la raíz del proyecto
cd /home/dev/dev/sp/sale_partido

# Backend
docker build -t salepartido-backend:latest ./backend

# Frontend
docker build -t salepartido-frontend:latest ./frontend

# Verificar que están creadas
docker images | grep salepartido
```

## Paso 2: Crear namespace

```bash
kubectl create namespace salepartido
```

## Paso 3: Crear Secrets y ConfigMaps

```bash
# Secrets (credenciales)
kubectl create secret generic salepartido-secret \
  --from-literal=POSTGRES_PASSWORD='tu-contraseña-segura' \
  --from-literal=SPRING_JWT_SECRET_KEY='tu-jwt-secret-aleatorio-64-caracteres' \
  --from-literal=REDIS_PASSWORD='tu-redis-password' \
  -n salepartido

# ConfigMap (variables públicas)
kubectl apply -f k8s/configmap.yaml
```

## Paso 4: Desplegar Base de Datos

```bash
# PostgreSQL
kubectl apply -f k8s/postgres.yaml

# Redis
kubectl apply -f k8s/redis.yaml

# Esperar a que estén listas
kubectl wait --for=condition=Ready pod -l app=postgres -n salepartido --timeout=300s
kubectl wait --for=condition=Ready pod -l app=redis -n salepartido --timeout=300s
```

## Paso 5: Desplegar Backend y Frontend

```bash
# Backend
kubectl apply -f k8s/backend.yaml
kubectl rollout status deployment/salepartido-backend -n salepartido

# Frontend
kubectl apply -f k8s/frontend.yaml
kubectl rollout status deployment/salepartido-frontend -n salepartido
```

## Paso 6: Verificar que todo funciona

```bash
# Ver pods
kubectl get pods -n salepartido

# Ver servicios
kubectl get svc -n salepartido

# Port forward para acceder en local
kubectl port-forward svc/salepartido-frontend 4200:80 -n salepartido
kubectl port-forward svc/salepartido-backend 8080:8080 -n salepartido

# Ahora accede a http://localhost:4200
```

## Paso 7: Verificar conectividad

```bash
# Dentro del frontend pod
kubectl exec -it deployment/salepartido-frontend -n salepartido -- sh

# Desde el container, prueba la conexión al backend
curl http://salepartido-backend:8080/api/health

# O desde tu máquina
curl -H "Host: salepartido-frontend" localhost:4200/api/health
```

## Solución de Problemas

### Frontend no carga
```bash
# Ver logs del frontend
kubectl logs deployment/salepartido-frontend -n salepartido

# Ver configuración de nginx
kubectl exec deployment/salepartido-frontend -n salepartido -- cat /etc/nginx/conf.d/default.conf
```

### Frontend no puede conectar al backend
```bash
# Verifica que el service del backend existe
kubectl get svc salepartido-backend -n salepartido

# Verifica los endpoints
kubectl get endpoints -n salepartido

# Prueba desde el frontend pod
kubectl exec deployment/salepartido-frontend -n salepartido -- \
  wget -O- http://salepartido-backend:8080/api/health
```

### Limpiar todo
```bash
kubectl delete namespace salepartido
```

## Cómo funciona la conexión

1. **Frontend** hace peticiones a `/api/...`
2. **Nginx** (en el frontend) intercepta peticiones a `/api/` y las proxea a `http://salepartido-backend:8080/`
3. **Backend Service** en K8s resuelve `salepartido-backend` al IP del pod
4. **Backend** recibe la petición en puerto 8080

Este flujo funciona porque todos están dentro del mismo namespace de K8s.
