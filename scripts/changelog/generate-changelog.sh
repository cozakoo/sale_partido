#!/bin/bash
set -e

# Script para generar CHANGELOG.md automáticamente desde commits
# Uso: ./scripts/run.sh changelog:generate [version]
# Si no se pasa versión, se calcula automáticamente desde el último tag
#
# Ejemplo:
#   ./scripts/run.sh changelog:generate        (calcula automáticamente)
#   ./scripts/run.sh changelog:generate v0.2.0 (usa versión específica)

PROJECT_ROOT="$(git rev-parse --show-toplevel)"
cd "$PROJECT_ROOT"

# Colores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Obtener último tag (vX.Y.Z)
LAST_TAG=$(git describe --tags --match "v*" 2>/dev/null || echo "")

if [ -z "$LAST_TAG" ]; then
    echo -e "${YELLOW}No hay tags. Esta será la primera versión.${NC}"
    VERSION="${1:-v0.1.0}"
else
    echo -e "${BLUE}Último tag: $LAST_TAG${NC}"

    # Calcular siguiente versión si no se pasa como argumento
    if [ -z "$1" ]; then
        # Parsear versión: vX.Y.Z -> X.Y.Z
        VERSION_NUMBER="${LAST_TAG#v}"
        MAJOR=$(echo "$VERSION_NUMBER" | cut -d. -f1)
        MINOR=$(echo "$VERSION_NUMBER" | cut -d. -f2)
        PATCH=$(echo "$VERSION_NUMBER" | cut -d. -f3)

        # Detectar si hay commits feat: (MINOR bump) o solo fix: (PATCH bump)
        COMMITS_SINCE_TAG=$(git rev-list --count ${LAST_TAG}..HEAD)

        if git log ${LAST_TAG}..HEAD --oneline | grep -q "^[^:]*feat("; then
            # Si hay feat commits -> bumpeea MINOR
            MINOR=$((MINOR + 1))
            PATCH=0
        else
            # Si solo hay fix commits -> bumpeea PATCH
            PATCH=$((PATCH + 1))
        fi

        VERSION="v${MAJOR}.${MINOR}.${PATCH}"
    else
        VERSION="$1"
    fi
fi

VERSION_NUMBER="${VERSION#v}"
echo -e "${GREEN}Generando changelog para versión: $VERSION${NC}"

# Función para extraer commits de una categoría
get_commits_by_type() {
    local type=$1
    local emoji=$2

    if [ -z "$LAST_TAG" ]; then
        commits=$(git log --oneline | grep "^[a-f0-9]* ${type}(" || true)
    else
        commits=$(git log ${LAST_TAG}..HEAD --oneline | grep "^[a-f0-9]* ${type}(" || true)
    fi

    if [ ! -z "$commits" ]; then
        echo "### $emoji $3"
        echo "$commits" | while read line; do
            hash=$(echo "$line" | awk '{print $1}')
            message=$(echo "$line" | sed "s/^[a-f0-9]* ${type}(\([^)]*\)): //")
            echo "- $message (\`$hash\`)"
        done
        echo ""
    fi
}

# Generar contenido del changelog
CHANGELOG_ENTRY=""

CHANGELOG_ENTRY+="## [$VERSION_NUMBER] - $(date +%Y-%m-%d)\n\n"

# Features
get_commits_by_type "feat" "✨" "Agregado" >> /tmp/changelog_entry.txt 2>&1 || true

# Bug fixes
get_commits_by_type "fix" "🐛" "Corregido" >> /tmp/changelog_entry.txt 2>&1 || true

# Refactor
get_commits_by_type "refactor" "♻️" "Cambiado" >> /tmp/changelog_entry.txt 2>&1 || true

# Docs
get_commits_by_type "docs" "📖" "Documentación" >> /tmp/changelog_entry.txt 2>&1 || true

# Performance
get_commits_by_type "perf" "⚡" "Rendimiento" >> /tmp/changelog_entry.txt 2>&1 || true

# Chore
get_commits_by_type "chore" "🔧" "Mantenimiento" >> /tmp/changelog_entry.txt 2>&1 || true

# Leer entrada del changelog generada
if [ -f /tmp/changelog_entry.txt ]; then
    CHANGELOG_ENTRY=$(cat /tmp/changelog_entry.txt)
    rm /tmp/changelog_entry.txt
fi

# Si no hay commits, crear entry mínima
if [ -z "$CHANGELOG_ENTRY" ] || [ "$CHANGELOG_ENTRY" = "" ]; then
    CHANGELOG_ENTRY="## [$VERSION_NUMBER] - $(date +%Y-%m-%d)\n\nSin cambios significativos.\n\n"
fi

# Leer CHANGELOG actual
if [ -f CHANGELOG.md ]; then
    # Buscar línea de [Unreleased] y reemplazar
    CURRENT_CHANGELOG=$(cat CHANGELOG.md)

    # Insertar nueva versión después de [Sin Liberar] section
    UPDATED_CHANGELOG=$(echo -e "$CURRENT_CHANGELOG" | awk -v entry="$CHANGELOG_ENTRY" '
        /^## \[Sin Liberar\]$/ {
            print $0
            print ""
            print entry
            next
        }
        { print }
    ')

    # También actualizar links al final del archivo
    UPDATED_CHANGELOG=$(echo -e "$UPDATED_CHANGELOG" | sed "s|\[Sin Liberar\]:|[Sin Liberar]: https://github.com/cozakoo/sale-partido/compare/v${VERSION_NUMBER}...HEAD\n[${VERSION_NUMBER}]: https://github.com/cozakoo/sale-partido/releases/tag/v${VERSION_NUMBER}|")

    echo -e "$UPDATED_CHANGELOG" > CHANGELOG.md
else
    # Crear CHANGELOG si no existe
    cat > CHANGELOG.md << EOF
# Registro de Cambios

Todos los cambios notables de este proyecto serán documentados en este archivo.

El formato se basa en [Keep a Changelog](https://keepachangelog.com/es/1.0.0/),
y este proyecto sigue [Versionado Semántico](https://semver.org/spec/v2.0.0.html).

## [Sin Liberar]

---

$CHANGELOG_ENTRY

---

[Sin Liberar]: https://github.com/cozakoo/sale-partido/compare/v${VERSION_NUMBER}...HEAD
[${VERSION_NUMBER}]: https://github.com/cozakoo/sale-partido/releases/tag/v${VERSION_NUMBER}
EOF
fi

echo -e "${GREEN}✅ CHANGELOG.md actualizado con versión $VERSION${NC}"
echo -e "${BLUE}Próximos pasos:${NC}"
echo "  1. Revisa CHANGELOG.md"
echo "  2. Actualiza versión en pom.xml y package.json"
echo "  3. Crea git tag: git tag -a $VERSION -m 'Release $VERSION'"
echo "  4. Push tag: git push origin $VERSION"
