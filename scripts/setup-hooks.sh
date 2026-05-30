#!/bin/bash

# Setup: Instala git hooks para validación automática
#
# Uso: ./scripts/setup-hooks.sh

echo "🔧 Instalando git hooks..."
echo ""

# Crear directorio .git/hooks si no existe
mkdir -p .git/hooks

# Copiar hook de commit-msg
cp scripts/git-hooks/commit-msg .git/hooks/commit-msg
chmod +x .git/hooks/commit-msg

echo "✅ Git hook instalado: commit-msg"
echo ""
echo "   Los commits serán validados automáticamente."
echo "   Formato requerido: feat(E#-H##): descripción"
echo ""
echo "📝 Próximos pasos:"
echo "   1. Sincronizar HUs: node scripts/sync-github-projects.js"
echo "   2. Crear rama: ./scripts/create-branch.sh E4-H08 crear-local"
echo "   3. Validar HU: node scripts/validate-hu.js E4-H08"
echo "   4. Codificar + commit"
echo ""
