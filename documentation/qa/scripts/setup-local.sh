#!/bin/bash

# Setup Interactivo - QA E5-H01
# Script para automatizar el setup del entorno de pruebas

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  QA E5-H01 - Setup Automatizado${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# 1. Verificar requisitos
echo -e "${YELLOW}[1/6]${NC} Verificando requisitos..."

check_command() {
    if ! command -v $1 &> /dev/null; then
        echo -e "${RED}✗${NC} $1 no encontrado"
        echo "  Instala: $2"
        exit 1
    else
        echo -e "${GREEN}✓${NC} $1 encontrado"
    fi
}

check_command "node" "nodejs (https://nodejs.org)"
check_command "npm" "npm (incluido en nodejs)"
check_command "java" "java 21 (https://www.oracle.com/java/technologies/downloads/)"
check_command "git" "git (https://git-scm.com)"

# Verificar PostgreSQL (puede no estar en PATH si está en Docker)
if ! command -v psql &> /dev/null; then
    echo -e "${YELLOW}⚠${NC} PostgreSQL no encontrado. Asume que está configurado en docker-compose"
fi

# Verificar Redis
if ! command -v redis-cli &> /dev/null; then
    echo -e "${YELLOW}⚠${NC} Redis no encontrado. Asume que está en docker-compose"
fi

echo ""

# 2. Navegar al repositorio
echo -e "${YELLOW}[2/6]${NC} Actualizando rama..."

cd "$(git rev-parse --show-toplevel)"
git fetch origin
git checkout feature/E5-H01/verificacion-funcional 2>/dev/null || git checkout -b feature/E5-H01/verificacion-funcional
git pull origin feature/E5-H01/verificacion-funcional 2>/dev/null || echo "Rama es nueva"

echo -e "${GREEN}✓${NC} Rama actualizada"
echo ""

# 3. Setup Backend
echo -e "${YELLOW}[3/6]${NC} Compilando backend..."

cd backend

echo "  Instalando dependencias Maven..."
./mvnw clean install -DskipTests -q

echo "  Migrando base de datos..."
export SPRING_PROFILES_ACTIVE=dev
./mvnw flyway:migrate -q || echo "  (Migraciones ya aplicadas)"

echo -e "${GREEN}✓${NC} Backend compilado"
echo ""

# 4. Setup Frontend
echo -e "${YELLOW}[4/6]${NC} Instalando frontend..."

cd ../frontend

if [ ! -f src/environments/environment.ts ]; then
    echo "  Creando archivo de entorno..."
    cp src/environments/environment.example.ts src/environments/environment.ts || \
    cat > src/environments/environment.ts << 'EOF'
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'
};
EOF
fi

echo "  Instalando dependencias npm..."
npm ci -q

echo -e "${GREEN}✓${NC} Frontend instalado"
echo ""

# 5. Iniciar servidores en background
echo -e "${YELLOW}[5/6]${NC} Iniciando servidores..."

# Backend en background
cd ../backend
echo "  Iniciando backend en puerto 8080..."
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev" > /tmp/backend.log 2>&1 &
BACKEND_PID=$!
echo "  Backend PID: $BACKEND_PID"

# Esperar a que backend esté listo
echo "  Esperando a que backend esté listo..."
for i in {1..30}; do
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} Backend listo"
        break
    fi
    if [ $i -eq 30 ]; then
        echo -e "${RED}✗${NC} Backend no respondió. Ver /tmp/backend.log"
        exit 1
    fi
    sleep 1
done

# Frontend en background
cd ../frontend
echo "  Iniciando frontend en puerto 4200..."
npm start > /tmp/frontend.log 2>&1 &
FRONTEND_PID=$!
echo "  Frontend PID: $FRONTEND_PID"

# Esperar a que frontend esté listo
echo "  Esperando a que frontend esté listo..."
for i in {1..60}; do
    if curl -s http://localhost:4200 > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} Frontend listo"
        break
    fi
    if [ $i -eq 60 ]; then
        echo -e "${RED}✗${NC} Frontend no respondió. Ver /tmp/frontend.log"
        exit 1
    fi
    sleep 1
done

echo ""

# 6. Verificaciones finales
echo -e "${YELLOW}[6/6]${NC} Verificando conectividad..."

# Backend health
if curl -s http://localhost:8080/actuator/health | grep -q "UP"; then
    echo -e "${GREEN}✓${NC} Backend respondiendo"
else
    echo -e "${RED}✗${NC} Backend no responde"
    exit 1
fi

# Frontend disponible
if curl -s http://localhost:4200 > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} Frontend disponible"
else
    echo -e "${RED}✗${NC} Frontend no responde"
    exit 1
fi

# API de locales
LOCALES=$(curl -s http://localhost:8080/locales | jq length 2>/dev/null || echo "0")
echo -e "${GREEN}✓${NC} API de locales responde ($LOCALES locales encontrados)"

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  ✓ Setup Completado Exitosamente${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

echo "URLs disponibles:"
echo -e "  ${YELLOW}Backend:${NC}  http://localhost:8080"
echo -e "  ${YELLOW}Frontend:${NC} http://localhost:4200"
echo ""

echo "Próximos pasos:"
echo -e "  1. Abrir navegador: ${YELLOW}http://localhost:4200${NC}"
echo -e "  2. Ejecutar pruebas: ${YELLOW}bash doc/qa/scripts/test-scenarios.sh${NC}"
echo -e "  3. Ver logs:"
echo -e "     Backend:  ${YELLOW}tail -f /tmp/backend.log${NC}"
echo -e "     Frontend: ${YELLOW}tail -f /tmp/frontend.log${NC}"
echo ""

echo "Para detener los servidores:"
echo -e "  ${YELLOW}kill $BACKEND_PID $FRONTEND_PID${NC}"
echo ""

# Salvar PIDs en archivo
echo "$BACKEND_PID $FRONTEND_PID" > /tmp/qa-pids.txt

echo -e "${GREEN}✓${NC} PIDs guardados en /tmp/qa-pids.txt"
