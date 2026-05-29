#!/bin/bash

# Auto-crea rama feature desde HU ID
#
# Uso: ./scripts/create-branch.sh E4-H08 "crear-local"
# Resultado: branch feature/E4-H08-crear-local

HU_ID=$1
DESCRIPTION=$2

if [ -z "$HU_ID" ] || [ -z "$DESCRIPTION" ]; then
  echo "Uso: ./scripts/create-branch.sh <HU_ID> <descripcion>"
  echo "Ejemplo: ./scripts/create-branch.sh E4-H08 crear-local"
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

BRANCH_NAME="feature/${HU_ID}-${DESCRIPTION}"

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
  echo ""
  echo "   Próximos pasos:"
  echo "   1. Validar HU: node scripts/validate-hu.js $HU_ID"
  echo "   2. Copiar agente correspondiente (/agentes/)"
  echo "   3. Codificar con IA"
  echo "   4. Commit: git commit -m 'feat($HU_ID): descripción'"
  echo ""
else
  echo "❌ Error creando rama (probablemente ya existe)"
  echo "   Usa: git checkout $BRANCH_NAME"
  exit 1
fi
