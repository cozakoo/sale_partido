#!/bin/bash

# Auto-crea ramas con convenciones
#
# Uso:
#   ./scripts/git/create-branch.sh E4-H08 crear-local           (feature/)
#   ./scripts/git/create-branch.sh E4-H08 crear-local feature   (feature/)
#   ./scripts/git/create-branch.sh E4-H08 crear-local bugfix    (bugfix/)
#   ./scripts/git/create-branch.sh E4-H08 crear-local hotfix    (hotfix/)
#   ./scripts/git/create-branch.sh E4-H08 crear-local refactor  (refactor/)
#   ./scripts/git/create-branch.sh E4-H08 crear-local chore     (chore/)
#   ./scripts/git/create-branch.sh E4-H08 crear-local test      (test/)

HU_ID=$1
DESCRIPTION=$2
BRANCH_TYPE=${3:-feature}  # Default: feature

if [ -z "$HU_ID" ] || [ -z "$DESCRIPTION" ]; then
  echo "Uso: ./scripts/git/create-branch.sh <HU_ID> <descripcion> [tipo]"
  echo ""
  echo "Parámetros:"
  echo "  HU_ID:       E#-H## (ej: E4-H08)"
  echo "  descripcion: solo minúsculas, números y guiones (ej: crear-local)"
  echo "  tipo:        feature|bugfix|hotfix|refactor|chore|test (default: feature)"
  echo ""
  echo "Ejemplos:"
  echo "  ./scripts/git/create-branch.sh E4-H08 crear-local"
  echo "  ./scripts/git/create-branch.sh E4-H08 corregir-bug bugfix"
  echo "  ./scripts/git/create-branch.sh E4-H08 refactor-auth refactor"
  echo ""
  exit 1
fi

# Validar formato HU_ID
if ! [[ $HU_ID =~ ^E[1-9]-H[0-9]{2}$ ]]; then
  echo "❌ Formato HU inválido: $HU_ID"
  echo "   Debe ser: E#-H## (ej: E4-H08)"
  exit 1
fi

# Validar descripción
if ! [[ $DESCRIPTION =~ ^[a-z0-9-]+$ ]]; then
  echo "❌ Descripción inválida: $DESCRIPTION"
  echo "   Solo letras minúsculas, números y guiones"
  exit 1
fi

# Validar tipo de rama
if ! [[ $BRANCH_TYPE =~ ^(feature|bugfix|hotfix|refactor|chore|test)$ ]]; then
  echo "❌ Tipo de rama inválido: $BRANCH_TYPE"
  echo "   Tipos válidos: feature, bugfix, hotfix, refactor, chore, test"
  exit 1
fi

BRANCH_NAME="${BRANCH_TYPE}/${HU_ID}-${DESCRIPTION}"

echo "🌿 Creando rama: $BRANCH_NAME"
echo ""

# Asegurar que estamos en dev
git checkout dev 2>/dev/null || {
  echo "❌ Error: No se pudo checkout a dev"
  exit 1
}

git pull origin dev 2>/dev/null || {
  echo "❌ Error: No se pudo pull de origin/dev"
  exit 1
}

# Crear rama
if git checkout -b "$BRANCH_NAME"; then
  echo ""
  echo "✅ Rama creada exitosamente!"
  echo ""
  echo "   Rama: $BRANCH_NAME"
  echo "   HU: $HU_ID"
  echo "   Tipo: $BRANCH_TYPE"
  echo ""

  # Sincronizar GitHub Projects
  echo "Sincronizando GitHub Projects..."
  if node scripts/github/sync-github-projects.js > /dev/null 2>&1; then
    echo "GitHub Projects sincronizado"
    echo ""
  else
    echo "Advertencia: No se pudo sincronizar GitHub Projects"
    echo "   Ejecuta manualmente: ./scripts/run.sh github:sync"
    echo ""
  fi

  echo "   Próximos pasos:"
  echo "   1. Validar HU: ./scripts/run.sh github:validate $HU_ID"
  echo "   2. Copiar agente correspondiente (/agents/)"
  echo "   3. Codificar con IA"
  echo "   4. Commit: git commit -m 'tu mensaje aquí'"
  echo ""
else
  echo "❌ Error creando rama (probablemente ya existe)"
  echo "   Usa: git checkout $BRANCH_NAME"
  exit 1
fi
