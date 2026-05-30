# Setup Interactivo - QA E5-H01

Script automatizado para setup del entorno de QA.

## Uso

```bash
# Opción 1: Ejecutar script (recomendado)
bash ./doc/qa/scripts/setup-local.sh

# Opción 2: Manual paso a paso
# Seguir SETUP_LOCAL.md
```

## Flujo del script

1. **Verificar requisitos**
   - Node.js v18+
   - Java 21+
   - PostgreSQL
   - Redis

2. **Clonar/actualizar rama**
   - `git fetch && git checkout feature/E5-H01/verificacion-funcional`

3. **Backend**
   - `./mvnw clean install`
   - `./mvnw flyway:migrate`
   - Iniciar en background

4. **Frontend**
   - `npm ci`
   - Copiar `environment.example.ts` → `environment.ts`
   - Iniciar en background

5. **Verificaciones**
   - Health check backend (GET /actuator/health)
   - Verificar datos de prueba (GET /locales)
   - Abrir frontend en navegador

6. **Resumen**
   - URLs disponibles
   - Logs de procesos
   - Próximos pasos

## Requisitos para ejecutar

```bash
# Verificar dependencias
node --version        # v18+
java --version        # 21+
npm --version         # 8+
psql --version        # 12+
redis-cli --version   # 7+

# Alternativa (Docker)
docker --version      # Para BD/Redis en containers
```

## Crear base de datos (si no existe)

```bash
# Opción 1: PostgreSQL directo
createdb sale_partido
psql -d sale_partido -c "CREATE SCHEMA IF NOT EXISTS public"

# Opción 2: Docker
docker run -d --name pg-test \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=sale_partido \
  -p 5432:5432 \
  postgres:15

# Opción 3: Docker Compose
docker-compose -f docker-compose.yml up -d
```

## Limpiar entorno

```bash
# Detener procesos
pkill -f "spring-boot:run"
pkill -f "ng serve"

# Limpiar datos (opcional)
rm -rf backend/target
rm -rf frontend/node_modules
rm -rf frontend/dist

# Reset BD
dropdb sale_partido
createdb sale_partido
./mvnw flyway:migrate
```
