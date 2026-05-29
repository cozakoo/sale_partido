#!/bin/bash

# Script de Inicialización - Sale Partido Automation
# Configura git hooks, .env, y todo lo necesario para el workflow automático

set -e  # Exit on error

echo "🚀 Inicializando Sale Partido Automation..."
echo ""

# Color codes
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 1. Hacer scripts ejecutables
echo -e "${BLUE}📝 Configurando permisos...${NC}"
chmod +x ./scripts/setup-hooks.sh
chmod +x ./scripts/create-branch.sh
chmod +x ./scripts/git-hooks/commit-msg
echo -e "${GREEN}✅ Permisos configurados${NC}"
echo ""

# 2. Configurar .env
echo -e "${BLUE}🔑 Configurando variables de entorno...${NC}"
if [ ! -f .env ]; then
  cp .env.example .env
  echo -e "${YELLOW}⚠️  Archivo .env creado${NC}"
  echo -e "${YELLOW}⚠️  IMPORTANTE: Abre .env y reemplaza 'ghp_your_token_here' con tu GitHub token${NC}"
  echo ""
  echo -e "   Para generar token:"
  echo -e "   1. Ve a: https://github.com/settings/tokens"
  echo -e "   2. Click 'Generate new token' (classic)"
  echo -e "   3. Permisos: read:project, read:org"
  echo -e "   4. Copia el token → reemplaza en .env"
  echo ""
else
  echo -e "${GREEN}✅ .env ya existe${NC}"
fi
echo ""

# 3. Instalar git hooks
echo -e "${BLUE}🔧 Instalando git hooks...${NC}"
./scripts/setup-hooks.sh
echo ""

# 4. Verificar Node.js
if ! command -v node &> /dev/null; then
  echo -e "${YELLOW}⚠️  Node.js no encontrado${NC}"
  echo -e "   Instálalo desde: https://nodejs.org/"
  echo ""
else
  echo -e "${GREEN}✅ Node.js detectado${NC}"

  # 5. Verificar dependencias
  echo -e "${BLUE}📦 Verificando dependencias Node.js...${NC}"
  if ! npm list octokit dotenv &>/dev/null; then
    echo -e "${YELLOW}⚠️  Instalando dependencias...${NC}"
    npm install octokit dotenv 2>/dev/null || {
      echo -e "${YELLOW}Nota: npm install necesita ejecutarse manualmente${NC}"
    }
  fi
  echo -e "${GREEN}✅ Dependencias OK${NC}"
  echo ""
fi

# 6. Próximos pasos
echo -e "${GREEN}═══════════════════════════════════════════${NC}"
echo -e "${GREEN}✅ SETUP COMPLETADO${NC}"
echo -e "${GREEN}═══════════════════════════════════════════${NC}"
echo ""
echo -e "${YELLOW}📋 Próximos pasos:${NC}"
echo ""
echo -e "1️⃣  Edita .env y agrega tu GitHub token:"
echo -e "   ${BLUE}GITHUB_TOKEN=ghp_xxxxxxxxxxxx${NC}"
echo ""
echo -e "2️⃣  Sincroniza HUs de GitHub Projects:"
echo -e "   ${BLUE}node scripts/sync-github-projects.js${NC}"
echo ""
echo -e "3️⃣  (Opcional) Descarga el tablero actual:"
echo -e "   ${BLUE}node scripts/download-board.js${NC}"
echo ""
echo -e "4️⃣  Comienza el flujo:"
echo -e "   ${BLUE}node scripts/validate-hu.js E4-H08${NC}"
echo -e "   ${BLUE}./scripts/create-branch.sh E4-H08 crear-local${NC}"
echo -e "   ${BLUE}[codifica...]${NC}"
echo -e "   ${BLUE}git commit -m 'feat(E4-H08): crear local'${NC}"
echo ""
echo -e "📖 Más info: ${BLUE}./scripts/README.md${NC}"
echo ""
