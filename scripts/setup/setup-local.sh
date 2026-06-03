#!/bin/bash

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Title
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}  Sale Partido - Setup Local (Sin Docker)${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

# Check OS
if [[ "$OSTYPE" == "darwin"* ]]; then
    OS="macos"
    INSTALL_CMD="brew install"
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    OS="linux"
    if command -v apt-get &> /dev/null; then
        INSTALL_CMD="sudo apt-get install -y"
    elif command -v yum &> /dev/null; then
        INSTALL_CMD="sudo yum install -y"
    else
        echo -e "${RED}❌ No se detectó un gestor de paquetes compatible${NC}"
        exit 1
    fi
else
    echo -e "${RED}❌ SO no soportado (solo Linux y macOS)${NC}"
    exit 1
fi

echo -e "${YELLOW}🔍 Sistema detectado: $OS${NC}"
echo ""

# Function to check if command exists
command_exists() {
    command -v "$1" &> /dev/null
}

# Function to print step
print_step() {
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
}

# ============================================================
# STEP 1: Java 21
# ============================================================
print_step "PASO 1: Verificar/Instalar Java 21"

if command_exists java; then
    JAVA_VERSION=$(java -version 2>&1 | head -1)
    echo -e "${GREEN}✅ Java ya está instalado: $JAVA_VERSION${NC}"

    if [[ "$JAVA_VERSION" != *"21"* ]]; then
        echo -e "${YELLOW}⚠️ Necesitas Java 21, pero tienes una versión diferente${NC}"
        echo -e "${YELLOW}Instalando Java 21...${NC}"

        if [[ "$OS" == "linux" ]]; then
            if ! command_exists sdk; then
                echo -e "${YELLOW}Instalando SDKMAN...${NC}"
                curl -s "https://get.sdkman.io" | bash
                source "$HOME/.sdkman/bin/sdkman-init.sh"
            fi
            sdk install java 21.0.2-tem --force
        elif [[ "$OS" == "macos" ]]; then
            $INSTALL_CMD openjdk@21
        fi
    fi
else
    echo -e "${YELLOW}Instalando Java 21...${NC}"

    if [[ "$OS" == "linux" ]]; then
        if ! command_exists sdk; then
            echo -e "${YELLOW}Instalando SDKMAN (gestor de versiones Java)...${NC}"
            curl -s "https://get.sdkman.io" | bash
            source "$HOME/.sdkman/bin/sdkman-init.sh"
        fi
        sdk install java 21.0.2-tem
        sdk default java 21.0.2-tem
    elif [[ "$OS" == "macos" ]]; then
        $INSTALL_CMD openjdk@21
    fi
fi

java -version
echo -e "${GREEN}✅ Java 21 listo${NC}"
echo ""

# ============================================================
# STEP 2: Maven
# ============================================================
print_step "PASO 2: Verificar/Instalar Maven"

if command_exists mvn; then
    MVN_VERSION=$(mvn --version 2>&1 | head -1)
    echo -e "${GREEN}✅ Maven ya está instalado: $MVN_VERSION${NC}"
else
    echo -e "${YELLOW}Instalando Maven...${NC}"

    if [[ "$OS" == "linux" ]]; then
        if ! command_exists wget; then
            $INSTALL_CMD wget
        fi

        MAVEN_VERSION="3.9.6"
        MAVEN_URL="https://dlcdn.apache.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz"

        echo -e "${YELLOW}Descargando Maven $MAVEN_VERSION...${NC}"
        wget -q "$MAVEN_URL" -O /tmp/maven.tar.gz

        echo -e "${YELLOW}Extrayendo...${NC}"
        sudo mkdir -p /opt
        sudo tar -xzf /tmp/maven.tar.gz -C /opt/
        sudo ln -sf /opt/apache-maven-$MAVEN_VERSION/bin/mvn /usr/local/bin/mvn

        rm /tmp/maven.tar.gz

    elif [[ "$OS" == "macos" ]]; then
        $INSTALL_CMD maven
    fi
fi

mvn --version
echo -e "${GREEN}✅ Maven listo${NC}"
echo ""

# ============================================================
# STEP 3: Redis
# ============================================================
print_step "PASO 3: Verificar/Instalar Redis"

if command_exists redis-cli; then
    echo -e "${GREEN}✅ Redis ya está instalado${NC}"
else
    echo -e "${YELLOW}Instalando Redis...${NC}"

    if [[ "$OS" == "linux" ]]; then
        $INSTALL_CMD redis-server
        sudo systemctl start redis-server
        sudo systemctl enable redis-server 2>/dev/null || true
    elif [[ "$OS" == "macos" ]]; then
        $INSTALL_CMD redis
        brew services start redis 2>/dev/null || true
    fi
fi

# Check Redis is running
if redis-cli ping &> /dev/null; then
    echo -e "${GREEN}✅ Redis está corriendo${NC}"
else
    echo -e "${YELLOW}⚠️ Redis instalado pero no está corriendo${NC}"
    echo -e "${YELLOW}Para iniciar Redis:${NC}"
    if [[ "$OS" == "linux" ]]; then
        echo -e "  ${YELLOW}sudo systemctl start redis-server${NC}"
    elif [[ "$OS" == "macos" ]]; then
        echo -e "  ${YELLOW}brew services start redis${NC}"
    fi
fi
echo ""

# ============================================================
# STEP 4: PostgreSQL
# ============================================================
print_step "PASO 4: Verificar PostgreSQL"

if command_exists psql; then
    PG_VERSION=$(psql --version)
    echo -e "${GREEN}✅ PostgreSQL está instalado: $PG_VERSION${NC}"
else
    echo -e "${YELLOW}⚠️ PostgreSQL no está instalado${NC}"
    echo -e "${YELLOW}Instálalo manualmente:${NC}"
    if [[ "$OS" == "linux" ]]; then
        echo -e "  ${YELLOW}sudo apt-get install postgresql postgresql-contrib${NC}"
    elif [[ "$OS" == "macos" ]]; then
        echo -e "  ${YELLOW}brew install postgresql${NC}"
    fi
    exit 1
fi

# Check if PostgreSQL service is running
if [[ "$OS" == "linux" ]]; then
    if sudo systemctl is-active --quiet postgresql; then
        echo -e "${GREEN}✅ PostgreSQL está corriendo${NC}"
    else
        echo -e "${YELLOW}⚠️ PostgreSQL instalado pero no está corriendo${NC}"
        echo -e "${YELLOW}Iniciando PostgreSQL...${NC}"
        sudo systemctl start postgresql
        echo -e "${GREEN}✅ PostgreSQL iniciado${NC}"
    fi
elif [[ "$OS" == "macos" ]]; then
    echo -e "${GREEN}✅ PostgreSQL listo (verifica manualmente si es necesario)${NC}"
fi
echo ""

# ============================================================
# STEP 5: Crear base de datos
# ============================================================
print_step "PASO 5: Crear base de datos PostgreSQL"

DB_NAME="sale_partido"
DB_USER="sale_user"
DB_PASS="sale_pass123"

echo -e "${YELLOW}Verificando si la base de datos existe...${NC}"

# Check if database exists
if psql -U postgres -tc "SELECT 1 FROM pg_database WHERE datname = '$DB_NAME'" | grep -q 1; then
    echo -e "${GREEN}✅ Base de datos '$DB_NAME' ya existe${NC}"
else
    echo -e "${YELLOW}Creando base de datos y usuario...${NC}"

    sudo -u postgres psql << EOF
CREATE DATABASE $DB_NAME;
CREATE USER $DB_USER WITH PASSWORD '$DB_PASS';
ALTER ROLE $DB_USER CREATEDB;
GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER;
\c $DB_NAME
GRANT ALL PRIVILEGES ON SCHEMA public TO $DB_USER;
EOF

    echo -e "${GREEN}✅ Base de datos creada exitosamente${NC}"
fi
echo ""

# ============================================================
# STEP 6: Install Node dependencies
# ============================================================
print_step "PASO 6: Instalar dependencias de Frontend"

if [ -d "frontend" ]; then
    cd frontend

    if [ ! -d "node_modules" ]; then
        echo -e "${YELLOW}Instalando dependencias npm...${NC}"
        npm install
        echo -e "${GREEN}✅ Dependencias de frontend instaladas${NC}"
    else
        echo -e "${GREEN}✅ node_modules ya existe${NC}"
    fi

    cd ..
else
    echo -e "${RED}❌ Directorio 'frontend' no encontrado${NC}"
fi
echo ""

# ============================================================
# STEP 7: Summary
# ============================================================
print_step "✅ SETUP COMPLETADO"

echo ""
echo -e "${GREEN}Tu ambiente está listo para correr Sale Partido localmente!${NC}"
echo ""
echo -e "${YELLOW}Los servicios están configurados en:${NC}"
echo -e "  ${GREEN}Backend:${NC}     http://localhost:8080"
echo -e "  ${GREEN}Frontend:${NC}    http://localhost:4200"
echo -e "  ${GREEN}PostgreSQL:${NC}  localhost:5432"
echo -e "  ${GREEN}Redis:${NC}       localhost:6379"
echo ""

echo -e "${YELLOW}Base de datos:${NC}"
echo -e "  ${GREEN}DB Name:${NC}     $DB_NAME"
echo -e "  ${GREEN}Username:${NC}    $DB_USER"
echo -e "  ${GREEN}Password:${NC}    $DB_PASS"
echo ""

echo -e "${BLUE}┌────────────────────────────────────────────────────────┐${NC}"
echo -e "${BLUE}│         EJECUTAR EL PROYECTO EN 3 TERMINALES          │${NC}"
echo -e "${BLUE}└────────────────────────────────────────────────────────┘${NC}"
echo ""

echo -e "${YELLOW}Terminal 1 - Backend (Java):${NC}"
echo -e "  ${GREEN}cd backend${NC}"
echo -e "  ${GREEN}./mvnw clean install  # Primera vez (5 min)${NC}"
echo -e "  ${GREEN}./mvnw spring-boot:run${NC}"
echo ""

echo -e "${YELLOW}Terminal 2 - Frontend (Angular):${NC}"
echo -e "  ${GREEN}cd frontend${NC}"
echo -e "  ${GREEN}ng serve${NC}"
echo ""

echo -e "${YELLOW}Terminal 3 - Redis (opcional):${NC}"
echo -e "  ${GREEN}redis-cli PING${NC}"
echo ""

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}¡Buena suerte! 🚀${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
