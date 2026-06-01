#!/bin/bash

# Script para configurar .env para pre-producción (testing)
# Uso: ./setup-env-preproduction.sh

set -e

ENV_FILE=".env"

if [ ! -f "$ENV_FILE" ]; then
    echo "Error: No se encuentra $ENV_FILE"
    exit 1
fi

echo "Actualizando $ENV_FILE para pre-producción..."

# Actualizar SPRING_PORT a 32329 (pre-prod backend)
sed -i 's/^SPRING_PORT=.*/SPRING_PORT=32329/' "$ENV_FILE"

# Actualizar SPRING_CORS a pre-prod frontend
sed -i 's|^SPRING_CORS=.*|SPRING_CORS=http://138.36.96.63:31168|' "$ENV_FILE"

echo "✓ SPRING_PORT actualizado a 32329"
echo "✓ SPRING_CORS actualizado a http://138.36.96.63:31168"
echo ""
echo "Verificación:"
grep -E "SPRING_PORT|SPRING_CORS" "$ENV_FILE"
echo ""
echo "Listo. Ejecuta: docker compose down -v && docker compose up"
