#!/bin/bash

# Sale Partido - Script Launcher
# Orquestador principal para scripts del proyecto

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Functions
print_header() {
  echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
  echo -e "${BLUE}$1${NC}"
  echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
}

print_success() {
  echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
  echo -e "${RED}❌ $1${NC}"
}

print_info() {
  echo -e "${YELLOW}ℹ️  $1${NC}"
}

show_menu() {
  echo ""
  echo "Uso: ./scripts/run.sh <comando>"
  echo ""
  echo -e "${BLUE}🚀 SETUP${NC}"
  echo "  setup:init              Configuración inicial completa"
  echo "  setup:local             Setup local (backend + frontend)"
  echo "  setup:preproduction     Configurar ambiente pre-producción"
  echo ""
  echo -e "${BLUE}🔗 GITHUB PROJECTS${NC}"
  echo "  github:sync             Sincronizar HUs desde GitHub"
  echo "  github:board            Descargar tablero completo"
  echo "  github:update HU_ID ESTADO  Actualizar estado (ej: github:update E4-H08 'In Progress')"
  echo ""
  echo -e "${BLUE}🌿 GIT WORKFLOW${NC}"
  echo "  git:branch HU_ID DESC [TYPE]  Crear rama (ej: git:branch E4-H08 crear-local bugfix)"
  echo "                                Tipos: feature, bugfix, hotfix, refactor, chore, test"
  echo ""
  echo -e "${BLUE}📝 CHANGELOG${NC}"
  echo "  changelog:generate      Generar CHANGELOG.md"
  echo ""
  echo -e "${BLUE}🗄️  DATABASE${NC}"
  echo "  db:seed                 Ejecutar seed de datos"
  echo ""
  echo -e "${BLUE}ℹ️  OTROS${NC}"
  echo "  help                    Mostrar esta ayuda"
  echo ""
}

# Main command handler
case "$1" in
  # Setup
  setup:init)
    print_header "🚀 Configuración inicial"
    bash "$SCRIPT_DIR/setup/init.sh"
    print_success "Setup inicial completado"
    ;;
  setup:local)
    print_header "🚀 Setup local"
    bash "$SCRIPT_DIR/setup/setup-local.sh"
    print_success "Setup local completado"
    ;;
  setup:preproduction)
    print_header "🚀 Setup pre-producción"
    bash "$SCRIPT_DIR/setup/setup-env-preproduction.sh"
    print_success "Setup pre-producción completado"
    ;;

  # GitHub Projects
  github:sync)
    print_header "🔗 Sincronizando HUs desde GitHub"
    node "$SCRIPT_DIR/github/sync-github-projects.js"
    ;;
  github:board)
    print_header "🔗 Descargando tablero"
    node "$SCRIPT_DIR/github/download-board.js"
    ;;
  github:update)
    if [ -z "$2" ] || [ -z "$3" ]; then
      print_error "Debe especificar HU_ID y ESTADO"
      echo "Uso: ./scripts/run.sh github:update E4-H08 'In Progress'"
      exit 1
    fi
    print_header "🔗 Actualizando $2 → $3"
    node "$SCRIPT_DIR/github/update-card.js" "$2" "$3"
    ;;

  # Git workflow
  git:branch)
    if [ -z "$2" ] || [ -z "$3" ]; then
      print_error "Debe especificar HU_ID y DESCRIPCIÓN"
      echo "Uso: ./scripts/run.sh git:branch E4-H08 crear-local [tipo]"
      echo "Tipos: feature, bugfix, hotfix, refactor, chore, test (default: feature)"
      exit 1
    fi
    print_header "🌿 Creando rama"
    bash "$SCRIPT_DIR/git/create-branch.sh" "$2" "$3" "$4"
    ;;
  # Changelog
  changelog:generate)
    print_header "📝 Generando CHANGELOG"
    bash "$SCRIPT_DIR/changelog/generate-changelog.sh"
    print_success "CHANGELOG generado"
    ;;

  # Database
  db:seed)
    print_header "🗄️  Ejecutando seed de datos"
    if command -v psql &> /dev/null; then
      psql -f "$SCRIPT_DIR/sql/seed-locales.sql"
      print_success "Seed ejecutado"
    else
      print_error "psql no está instalado"
      exit 1
    fi
    ;;

  # Help
  help|--help|-h|"")
    show_menu
    ;;

  *)
    print_error "Comando desconocido: $1"
    echo ""
    show_menu
    exit 1
    ;;
esac
