#!/bin/bash
# Cambiar a producción

cp .env.prod .env
echo "✓ Configurado para PRODUCCIÓN"
echo ""
grep -E "SPRING_PORT|FRONTEND_PORT|SPRING_CORS" .env
echo ""
echo "Próximo paso: docker compose down && docker compose up -d"
