# ☸️ Kubernetes - Guía de Despliegue

> Orquestación y deployment del stack Sale Partido en Kubernetes

---

## 📋 Índice

1. [Arquitectura K8s](#arquitectura-k8s)
2. [Prerequisitos](#prerequisitos)
3. [Despliegue Paso a Paso](#despliegue-paso-a-paso)
4. [Manifests Detallados](#manifests-detallados)
5. [Configuración de Secretos](#configuración-de-secretos)
6. [Monitoreo & Logging](#monitoreo--logging)
7. [Troubleshooting](#troubleshooting)
8. [Commands Útiles](#commands-útiles)

---

## 🏗️ Arquitectura K8s

```
┌─────────────────────────────────────────────────────┐
│        Kubernetes Cluster                           │
│  ┌───────────────────────────────────────────────┐  │
│  │ Namespace: salepartido                        │  │
│  │                                               │  │
│  │ ┌─────────────────────────────────────────┐  │  │
│  │ │ Ingress (nginx)                         │  │  │
│  │ │ • sale-partido.com → frontend (80)      │  │  │
│  │ │ • sale-partido.com/api → backend (8080) │  │  │
│  │ └─────────────────────────────────────────┘  │  │
│  │           ↓              ↓                      │  │
│  │ ┌──────────────────┐ ┌──────────────────┐     │  │
│  │ │ Frontend Service │ │ Backend Service  │     │  │
│  │ │ (ClusterIP:80)   │ │ (ClusterIP:8080) │     │  │
│  │ └──────────────────┘ └──────────────────┘     │  │
│  │      ↓                    ↓                     │  │
│  │ ┌──────────────────┐ ┌──────────────────┐     │  │
│  │ │ Frontend Pods    │ │ Backend Pods     │     │  │
│  │ │ (replicas: 2-3)  │ │ (replicas: 2-3)  │     │  │
│  │ │ • nginx          │ │ • Spring Boot    │     │  │
│  │ └──────────────────┘ └──────────────────┘     │  │
│  │           ↓                                     │  │
│  │ ┌──────────────────────────────────────────┐  │  │
│  │ │ Database Service (PostgreSQL)            │  │  │
│  │ │ • postgres:5432                          │  │  │
│  │ │ • StatefulSet (1 replica)                │  │  │
│  │ └──────────────────────────────────────────┘  │  │
│  │ ┌──────────────────────────────────────────┐  │  │
│  │ │ Cache Service (Redis)                    │  │  │
│  │ │ • redis:6379                             │  │  │
│  │ │ • StatefulSet (1 replica)                │  │  │
│  │ └──────────────────────────────────────────┘  │  │
│  │                                               │  │
│  │ ConfigMaps: variables de configuración       │  │
│  │ Secrets: contraseñas y credenciales          │  │
│  │ PersistentVolumes: almacenamiento            │  │
│  └───────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
```

---

## ✅ Prerequisitos

### En tu máquina local

```bash
# Kubernetes tools
brew install kubectl              # macOS
# o apt-get install kubectl       # Linux

# Cluster local (elige uno)
brew install minikube            # Recomendado
brew install docker-desktop      # Incluye K3s

# Helm (opcional, para gestionar charts)
brew install helm

# Verificar instalación
kubectl version --client
minikube version
```

### En el cluster (si usas proveedor cloud)

```bash
# Google Cloud (GKE)
gcloud container clusters create salepartido-cluster \
  --zone us-central1-a \
  --num-nodes 3

# Azure (AKS)
az aks create \
  --resource-group myResourceGroup \
  --name salepartidoCluster \
  --node-count 3

# AWS (EKS)
eksctl create cluster \
  --name salepartido-cluster \
  --region us-east-1 \
  --nodes 3
```

---

## 🚀 Despliegue Paso a Paso

### 1. Preparar imágenes Docker

```bash
# Buildear localmente
docker build -t salepartido-backend:v1.0.0 ./backend
docker build -t salepartido-frontend:v1.0.0 ./frontend

# Si usas Minikube (importante):
eval $(minikube docker-env)
docker build -t salepartido-backend:v1.0.0 ./backend
docker build -t salepartido-frontend:v1.0.0 ./frontend

# Si usas registry externo (Docker Hub, ACR, GCR):
docker tag salepartido-backend:v1.0.0 myuser/salepartido-backend:v1.0.0
docker push myuser/salepartido-backend:v1.0.0
```

### 2. Crear namespace

```bash
kubectl create namespace salepartido
# o
kubectl apply -f k8s/namespace.yaml
```

### 3. Crear Secrets y ConfigMaps

```bash
# Secrets (credenciales)
kubectl create secret generic salepartido-secret \
  --from-literal=POSTGRES_PASSWORD='tu-contraseña-segura' \
  --from-literal=SPRING_JWT_SECRET_KEY='tu-jwt-secret-aleatorio-64-caracteres' \
  --from-literal=REDIS_PASSWORD='tu-redis-password' \
  -n salepartido

# ConfigMap (variables públicas)
kubectl apply -f k8s/configmap.yaml

# Verificar
kubectl get secrets -n salepartido
kubectl get configmap -n salepartido
```

### 4. Desplegar base de datos y cache

```bash
# PostgreSQL
kubectl apply -f k8s/postgres.yaml

# Redis
kubectl apply -f k8s/redis.yaml

# Esperar a que estén listos
kubectl wait --for=condition=Ready pod \
  -l app=postgres -n salepartido --timeout=300s

# Verificar conexión
kubectl port-forward -n salepartido service/postgres 5432:5432
psql -h localhost -U salepartido_user -d salepartido_database
```

### 5. Desplegar aplicación (Backend & Frontend)

```bash
# Backend
kubectl apply -f k8s/backend.yaml

# Frontend
kubectl apply -f k8s/frontend.yaml

# Esperar a deployments
kubectl rollout status deployment/salepartido-backend -n salepartido
kubectl rollout status deployment/salepartido-frontend -n salepartido
```

### 6. Configurar Ingress (routing)

```bash
# Si usas Nginx Ingress Controller
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.1/deploy/static/provider/cloud/deploy.yaml

# Aplicar Ingress
kubectl apply -f k8s/ingress.yaml

# Obtener IP/DNS del Ingress
kubectl get ingress -n salepartido
```

### 7. Verificar despliegue

```bash
# Ver todo en el namespace
kubectl get all -n salepartido

# Ver pods
kubectl get pods -n salepartido -w

# Ver servicios
kubectl get svc -n salepartido

# Ver Ingress
kubectl get ingress -n salepartido

# Describir deployment si hay problemas
kubectl describe deployment salepartido-backend -n salepartido
```

---

## 📄 Manifests Detallados

### namespace.yaml
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: salepartido
  labels:
    name: salepartido
```

### configmap.yaml
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: salepartido-config
  namespace: salepartido
data:
  # PostgreSQL
  POSTGRES_USER: "salepartido_user"
  POSTGRES_DB: "salepartido_database"
  POSTGRES_HOST: "postgres"
  POSTGRES_PORT: "5432"
  
  # Redis
  REDIS_HOST: "redis"
  REDIS_PORT: "6379"
  REDIS_DATABASE: "0"
  
  # Spring Boot
  SPRING_PROFILE: "prod"
  SPRING_PORT: "8080"
  SERVER_SERVLET_CONTEXT_PATH: ""
  
  # CORS
  SPRING_CORS: "https://sale-partido.com"
  
  # Logging
  LOGGING_LEVEL_COM_SALEWW: "INFO"
  LOGGING_LEVEL_ORG_SPRINGFRAMEWORK: "WARN"
```

### secret.yaml
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: salepartido-secret
  namespace: salepartido
type: Opaque
stringData:
  POSTGRES_PASSWORD: "change-me-in-production"
  SPRING_JWT_SECRET_KEY: "your-jwt-secret-very-long-and-random-string-here"
  REDIS_PASSWORD: "change-me-redis-password"
  SPRING_DATASOURCE_PASSWORD: "change-me-in-production"
```

### postgres.yaml
```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: postgres-pvc
  namespace: salepartido
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 10Gi
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgres
  namespace: salepartido
spec:
  serviceName: postgres
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
      - name: postgres
        image: postgres:16-alpine
        ports:
        - containerPort: 5432
        env:
        - name: POSTGRES_DB
          valueFrom:
            configMapKeyRef:
              name: salepartido-config
              key: POSTGRES_DB
        - name: POSTGRES_USER
          valueFrom:
            configMapKeyRef:
              name: salepartido-config
              key: POSTGRES_USER
        - name: POSTGRES_PASSWORD
          valueFrom:
            secretKeyRef:
              name: salepartido-secret
              key: POSTGRES_PASSWORD
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        volumeMounts:
        - name: postgres-storage
          mountPath: /var/lib/postgresql/data
      volumes:
      - name: postgres-storage
        persistentVolumeClaim:
          claimName: postgres-pvc
---
apiVersion: v1
kind: Service
metadata:
  name: postgres
  namespace: salepartido
spec:
  clusterIP: None
  selector:
    app: postgres
  ports:
  - port: 5432
    targetPort: 5432
```

### redis.yaml
```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: redis-pvc
  namespace: salepartido
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 2Gi
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: redis
  namespace: salepartido
spec:
  serviceName: redis
  replicas: 1
  selector:
    matchLabels:
      app: redis
  template:
    metadata:
      labels:
        app: redis
    spec:
      containers:
      - name: redis
        image: redis:7-alpine
        command:
          - redis-server
          - --requirepass
          - $(REDIS_PASSWORD)
        ports:
        - containerPort: 6379
        env:
        - name: REDIS_PASSWORD
          valueFrom:
            secretKeyRef:
              name: salepartido-secret
              key: REDIS_PASSWORD
        resources:
          requests:
            memory: "128Mi"
            cpu: "100m"
          limits:
            memory: "256Mi"
            cpu: "200m"
        volumeMounts:
        - name: redis-storage
          mountPath: /data
      volumes:
      - name: redis-storage
        persistentVolumeClaim:
          claimName: redis-pvc
---
apiVersion: v1
kind: Service
metadata:
  name: redis
  namespace: salepartido
spec:
  clusterIP: None
  selector:
    app: redis
  ports:
  - port: 6379
    targetPort: 6379
```

### backend.yaml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: salepartido-backend
  namespace: salepartido
  labels:
    app: salepartido-backend
    version: v1
spec:
  replicas: 2
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: salepartido-backend
  template:
    metadata:
      labels:
        app: salepartido-backend
    spec:
      affinity:
        podAntiAffinity:
          preferredDuringSchedulingIgnoredDuringExecution:
          - weight: 100
            podAffinityTerm:
              labelSelector:
                matchExpressions:
                - key: app
                  operator: In
                  values:
                  - salepartido-backend
              topologyKey: kubernetes.io/hostname
      
      containers:
      - name: backend
        image: salepartido-backend:v1.0.0
        imagePullPolicy: IfNotPresent
        ports:
        - name: http
          containerPort: 8080
          protocol: TCP
        
        env:
        - name: SPRING_DATASOURCE_URL
          value: "jdbc:postgresql://postgres:5432/salepartido_database"
        - name: SPRING_DATASOURCE_USERNAME
          valueFrom:
            configMapKeyRef:
              name: salepartido-config
              key: POSTGRES_USER
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: salepartido-secret
              key: POSTGRES_PASSWORD
        - name: SPRING_DATA_REDIS_HOST
          valueFrom:
            configMapKeyRef:
              name: salepartido-config
              key: REDIS_HOST
        - name: SPRING_DATA_REDIS_PORT
          valueFrom:
            configMapKeyRef:
              name: salepartido-config
              key: REDIS_PORT
        - name: SPRING_DATA_REDIS_PASSWORD
          valueFrom:
            secretKeyRef:
              name: salepartido-secret
              key: REDIS_PASSWORD
        - name: SPRING_JWT_SECRET_KEY
          valueFrom:
            secretKeyRef:
              name: salepartido-secret
              key: SPRING_JWT_SECRET_KEY
        - name: SPRING_PROFILES_ACTIVE
          valueFrom:
            configMapKeyRef:
              name: salepartido-config
              key: SPRING_PROFILE
        
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        
        livenessProbe:
          httpGet:
            path: /actuator/health/live
            port: http
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3
        
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: http
          initialDelaySeconds: 10
          periodSeconds: 5
          timeoutSeconds: 3
          failureThreshold: 3
        
        startupProbe:
          httpGet:
            path: /actuator/health/startup
            port: http
          failureThreshold: 30
          periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: salepartido-backend
  namespace: salepartido
  labels:
    app: salepartido-backend
spec:
  type: ClusterIP
  selector:
    app: salepartido-backend
  ports:
  - protocol: TCP
    port: 8080
    targetPort: http
    name: http
```

### frontend.yaml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: salepartido-frontend
  namespace: salepartido
  labels:
    app: salepartido-frontend
    version: v1
spec:
  replicas: 2
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: salepartido-frontend
  template:
    metadata:
      labels:
        app: salepartido-frontend
    spec:
      affinity:
        podAntiAffinity:
          preferredDuringSchedulingIgnoredDuringExecution:
          - weight: 100
            podAffinityTerm:
              labelSelector:
                matchExpressions:
                - key: app
                  operator: In
                  values:
                  - salepartido-frontend
              topologyKey: kubernetes.io/hostname
      
      containers:
      - name: frontend
        image: salepartido-frontend:v1.0.0
        imagePullPolicy: IfNotPresent
        ports:
        - name: http
          containerPort: 80
          protocol: TCP
        
        resources:
          requests:
            memory: "128Mi"
            cpu: "100m"
          limits:
            memory: "256Mi"
            cpu: "200m"
        
        livenessProbe:
          httpGet:
            path: /
            port: http
          initialDelaySeconds: 10
          periodSeconds: 10
          timeoutSeconds: 3
          failureThreshold: 3
        
        readinessProbe:
          httpGet:
            path: /
            port: http
          initialDelaySeconds: 5
          periodSeconds: 5
          timeoutSeconds: 2
          failureThreshold: 2
---
apiVersion: v1
kind: Service
metadata:
  name: salepartido-frontend
  namespace: salepartido
  labels:
    app: salepartido-frontend
spec:
  type: ClusterIP
  selector:
    app: salepartido-frontend
  ports:
  - protocol: TCP
    port: 80
    targetPort: http
    name: http
```

### ingress.yaml
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: salepartido-ingress
  namespace: salepartido
  annotations:
    kubernetes.io/ingress.class: nginx
    cert-manager.io/cluster-issuer: letsencrypt-prod
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/use-regex: "true"
spec:
  ingressClassName: nginx
  tls:
  - hosts:
    - sale-partido.com
    secretName: salepartido-tls
  rules:
  - host: sale-partido.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: salepartido-frontend
            port:
              number: 80
      - path: /api
        pathType: Prefix
        backend:
          service:
            name: salepartido-backend
            port:
              number: 8080
      - path: /swagger-ui(.*)
        pathType: Prefix
        backend:
          service:
            name: salepartido-backend
            port:
              number: 8080
```

---

## 🔐 Configuración de Secretos

### Generar Secretos Seguros

```bash
# JWT Secret (Base64 encoded)
openssl rand -base64 64

# Postgres Password
openssl rand -base64 32

# Redis Password
openssl rand -base64 24

# Crear secret en K8s
kubectl create secret generic salepartido-secret \
  --from-literal=POSTGRES_PASSWORD='YOUR_DB_PASSWORD' \
  --from-literal=SPRING_JWT_SECRET_KEY='YOUR_JWT_KEY' \
  --from-literal=REDIS_PASSWORD='YOUR_REDIS_PASSWORD' \
  -n salepartido

# Verificar secret
kubectl get secret salepartido-secret -n salepartido -o yaml
```

### Usar Sealed Secrets (Producción)

```bash
# Instalar Sealed Secrets
kubectl apply -f https://github.com/bitnami-labs/sealed-secrets/releases/download/v0.18.0/controller.yaml

# Crear sealed secret
echo -n mypassword | kubectl create secret generic mysecret \
  --dry-run=client --from-file=password=/dev/stdin -o yaml | \
  kubeseal -f - > mysealedsecret.yaml

# Aplicar
kubectl apply -f mysealedsecret.yaml
```

---

## 📊 Monitoreo & Logging

### Prometheus & Grafana

```bash
# Instalar Prometheus
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm install prometheus prometheus-community/kube-prometheus-stack -n monitoring --create-namespace

# Instalar Grafana
helm repo add grafana https://grafana.github.io/helm-charts
helm install grafana grafana/grafana -n monitoring --create-namespace
```

### Logs Aggregation (ELK Stack)

```bash
# Elasticsearch
kubectl apply -f https://download.elastic.co/downloads/eck/2.9.0/crds.yaml
kubectl apply -f https://download.elastic.co/downloads/eck/2.9.0/operator.yaml

# Kibana
kubectl apply -f kibana.yaml

# Filebeat/Logstash
kubectl apply -f filebeat.yaml
```

### Ver Logs

```bash
# Logs de Backend
kubectl logs -f deployment/salepartido-backend -n salepartido

# Logs de múltiples pods
kubectl logs -f -l app=salepartido-backend -n salepartido

# Logs con timestamps
kubectl logs deployment/salepartido-backend -n salepartido --timestamps=true

# Logs anteriores (pod crasheado)
kubectl logs <pod-name> -n salepartido --previous
```

---

## 🐛 Troubleshooting

### Pod no inicia

```bash
# Ver estado
kubectl describe pod <pod-name> -n salepartido

# Ver logs
kubectl logs <pod-name> -n salepartido
kubectl logs <pod-name> -n salepartido --previous

# Ejecutar comando en pod
kubectl exec -it <pod-name> -n salepartido -- /bin/sh
```

### Service no accesible

```bash
# Verificar endpoints
kubectl get endpoints -n salepartido

# Verificar selectors
kubectl describe service salepartido-backend -n salepartido

# Port forward para debug
kubectl port-forward service/salepartido-backend 8080:8080 -n salepartido
curl http://localhost:8080/actuator/health
```

### Problema con persistencia

```bash
# Ver PVCs
kubectl get pvc -n salepartido

# Ver PVs
kubectl get pv

# Describir PVC
kubectl describe pvc postgres-pvc -n salepartido

# Acceder a BD
kubectl port-forward service/postgres 5432:5432 -n salepartido
psql -h localhost -U salepartido_user -d salepartido_database
```

### Rollback deployment

```bash
# Ver historial
kubectl rollout history deployment/salepartido-backend -n salepartido

# Volver a versión anterior
kubectl rollout undo deployment/salepartido-backend -n salepartido

# Volver a versión específica
kubectl rollout undo deployment/salepartido-backend -n salepartido --to-revision=2
```

---

## ⚡ Commands Útiles

```bash
# Desplegar todo desde k8s/
kubectl apply -f k8s/ -n salepartido

# Ver status
kubectl get all -n salepartido
kubectl get pods -n salepartido -w

# Scale manual
kubectl scale deployment salepartido-backend --replicas=5 -n salepartido

# Eliminar deployment
kubectl delete deployment salepartido-backend -n salepartido

# Ejecutar pod temporal
kubectl run -it --rm debug --image=busybox --restart=Never -n salepartido -- sh

# Port forwarding
kubectl port-forward svc/salepartido-backend 8080:8080 -n salepartido
kubectl port-forward svc/postgres 5432:5432 -n salepartido

# Logs
kubectl logs -f deployment/salepartido-backend -n salepartido
kubectl logs -f -l app=salepartido-backend -n salepartido

# Debugging
kubectl describe pod <pod-name> -n salepartido
kubectl exec -it <pod-name> -n salepartido -- /bin/bash

# Limpieza
kubectl delete namespace salepartido
```

---

**Sale Partido en Kubernetes** — Deploy con confianza 🚀

*Última actualización: 14/05/2026*
