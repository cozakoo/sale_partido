#!/bin/bash

# Cambiar a producción
# Uso: ./scripts/setup/use-production.sh

cp .env.prod .env
echo "✓ Configurado para PRODUCCIÓN"
echo ""
grep -E "SPRING_PORT|FRONTEND_PORT|SPRING_CORS" .env
echo ""
echo "Próximo paso: docker compose down && docker compose up -d"
