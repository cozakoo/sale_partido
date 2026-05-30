#!/bin/bash
set -e

# Script para generar CHANGELOG.md automáticamente desde commits
# Uso: ./scripts/generate-changelog.sh [version]
# Si no se pasa versión, se calcula automáticamente desde el último tag

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
    echo -e "${YELLOW}No tags found. This will be the first release.${NC}"
    VERSION="${1:-v0.1.0}"
else
    echo -e "${BLUE}Last tag: $LAST_TAG${NC}"

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
echo -e "${GREEN}Generating changelog for version: $VERSION${NC}"

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
get_commits_by_type "feat" "✨" "Added" >> /tmp/changelog_entry.txt 2>&1 || true

# Bug fixes
get_commits_by_type "fix" "🐛" "Fixed" >> /tmp/changelog_entry.txt 2>&1 || true

# Refactor
get_commits_by_type "refactor" "♻️" "Changed" >> /tmp/changelog_entry.txt 2>&1 || true

# Docs
get_commits_by_type "docs" "📖" "Documentation" >> /tmp/changelog_entry.txt 2>&1 || true

# Performance
get_commits_by_type "perf" "⚡" "Performance" >> /tmp/changelog_entry.txt 2>&1 || true

# Chore
get_commits_by_type "chore" "🔧" "Chore" >> /tmp/changelog_entry.txt 2>&1 || true

# Leer entrada del changelog generada
if [ -f /tmp/changelog_entry.txt ]; then
    CHANGELOG_ENTRY=$(cat /tmp/changelog_entry.txt)
    rm /tmp/changelog_entry.txt
fi

# Si no hay commits, crear entry mínima
if [ -z "$CHANGELOG_ENTRY" ] || [ "$CHANGELOG_ENTRY" = "" ]; then
    CHANGELOG_ENTRY="## [$VERSION_NUMBER] - $(date +%Y-%m-%d)\n\nNo significant changes.\n\n"
fi

# Leer CHANGELOG actual
if [ -f CHANGELOG.md ]; then
    # Buscar línea de [Unreleased] y reemplazar
    CURRENT_CHANGELOG=$(cat CHANGELOG.md)

    # Insertar nueva versión después de [Unreleased] section
    UPDATED_CHANGELOG=$(echo -e "$CURRENT_CHANGELOG" | awk -v entry="$CHANGELOG_ENTRY" '
        /^## \[Unreleased\]$/ {
            print $0
            print ""
            print entry
            next
        }
        { print }
    ')

    # También actualizar links al final del archivo
    UPDATED_CHANGELOG=$(echo -e "$UPDATED_CHANGELOG" | sed "s|\[Unreleased\]:|[Unreleased]: https://github.com/cozakoo/sale-partido/compare/v${VERSION_NUMBER}...HEAD\n[${VERSION_NUMBER}]: https://github.com/cozakoo/sale-partido/releases/tag/v${VERSION_NUMBER}|")

    echo -e "$UPDATED_CHANGELOG" > CHANGELOG.md
else
    # Crear CHANGELOG si no existe
    cat > CHANGELOG.md << EOF
# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

---

$CHANGELOG_ENTRY

---

[Unreleased]: https://github.com/cozakoo/sale-partido/compare/v${VERSION_NUMBER}...HEAD
[${VERSION_NUMBER}]: https://github.com/cozakoo/sale-partido/releases/tag/v${VERSION_NUMBER}
EOF
fi

echo -e "${GREEN}✅ CHANGELOG.md updated with version $VERSION${NC}"
echo -e "${BLUE}Next steps:${NC}"
echo "  1. Review CHANGELOG.md"
echo "  2. Update version in pom.xml and package.json"
echo "  3. Create git tag: git tag -a $VERSION -m 'Release $VERSION'"
echo "  4. Push tag: git push origin $VERSION"
