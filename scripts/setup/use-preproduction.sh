#!/bin/bash
# Cambiar a pre-producción

cp .env.pre-prod .env
echo "✓ Configurado para PRE-PRODUCCIÓN"
echo ""
grep -E "SPRING_PORT|FRONTEND_PORT|SPRING_CORS" .env
echo ""
echo "Próximo paso: docker compose down && docker compose up -d"
