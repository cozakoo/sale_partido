#!/bin/bash

set -e

NAMESPACE="salepartido"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "🚀 Sale Partido - Despliegue en Kubernetes"
echo "========================================="
echo ""

# 1. Buildear imágenes
echo "📦 Buildear imágenes Docker..."
cd "$PROJECT_ROOT"

if [[ "$1" == "--minikube" ]]; then
    eval $(minikube docker-env)
    echo "   ✓ Minikube docker-env activado"
fi

docker build -t salepartido-backend:latest ./backend
echo "   ✓ Backend image creada"

docker build -t salepartido-frontend:latest ./frontend
echo "   ✓ Frontend image creada"

cd "$SCRIPT_DIR"
echo ""

# 2. Crear namespace
echo "📁 Creando namespace..."
kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -
echo "   ✓ Namespace $NAMESPACE listo"
echo ""

# 3. Crear secrets y configmaps
echo "🔐 Configurando Secrets y ConfigMaps..."

# Generar valores seguros si no existen
if ! kubectl get secret salepartido-secret -n $NAMESPACE &>/dev/null; then
    POSTGRES_PASSWORD=$(openssl rand -base64 32)
    JWT_SECRET=$(openssl rand -base64 64)
    REDIS_PASSWORD=$(openssl rand -base64 24)
    
    kubectl create secret generic salepartido-secret \
        --from-literal=POSTGRES_PASSWORD="$POSTGRES_PASSWORD" \
        --from-literal=SPRING_JWT_SECRET_KEY="$JWT_SECRET" \
        --from-literal=REDIS_PASSWORD="$REDIS_PASSWORD" \
        -n $NAMESPACE
    
    echo "   ✓ Secret creado"
else
    echo "   ℹ Secret ya existe"
fi

kubectl apply -f configmap.yaml
echo "   ✓ ConfigMap aplicado"
echo ""

# 4. Desplegar base de datos
echo "🗄️  Desplegando base de datos..."
kubectl apply -f postgres.yaml
kubectl wait --for=condition=Ready pod -l app=postgres -n $NAMESPACE --timeout=300s 2>/dev/null || true
echo "   ✓ PostgreSQL listo"
echo ""

# 5. Desplegar cache
echo "💾 Desplegando cache..."
kubectl apply -f redis.yaml
kubectl wait --for=condition=Ready pod -l app=redis -n $NAMESPACE --timeout=300s 2>/dev/null || true
echo "   ✓ Redis listo"
echo ""

# 6. Desplegar backend
echo "🔧 Desplegando backend..."
kubectl apply -f backend.yaml
kubectl rollout status deployment/salepartido-backend -n $NAMESPACE --timeout=300s
echo "   ✓ Backend listo"
echo ""

# 7. Desplegar frontend
echo "🎨 Desplegando frontend..."
kubectl apply -f frontend.yaml
kubectl rollout status deployment/salepartido-frontend -n $NAMESPACE --timeout=300s
echo "   ✓ Frontend listo"
echo ""

# 8. Mostrar información
echo "✅ Despliegue completado!"
echo ""
echo "📊 Estado de los recursos:"
kubectl get all -n $NAMESPACE
echo ""

echo "🔗 Para acceder localmente:"
echo "   Frontend:  kubectl port-forward svc/salepartido-frontend 4200:80 -n $NAMESPACE"
echo "   Backend:   kubectl port-forward svc/salepartido-backend 8080:8080 -n $NAMESPACE"
echo ""

echo "📋 Para ver logs:"
echo "   Frontend:  kubectl logs -f deployment/salepartido-frontend -n $NAMESPACE"
echo "   Backend:   kubectl logs -f deployment/salepartido-backend -n $NAMESPACE"
echo ""
