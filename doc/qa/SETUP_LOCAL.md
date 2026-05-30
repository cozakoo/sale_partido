# Setup Local - QA E5-H01

## Requisitos previos

- **Node.js:** v18+ (verificar con `node --version`)
- **Java:** 21+ (verificar con `java --version`)
- **PostgreSQL:** 15+ (local o Docker)
- **Redis:** 7+ (local o Docker)
- **Git:** configurado

## 1. Clonar/Actualizar repositorio

```bash
cd ~/Documents/Proyectos/sale_partido
git fetch origin
git checkout feature/E5-H01/verificacion-funcional
git pull origin feature/E5-H01/verificacion-funcional
```

## 2. Setup Backend (Java/Spring Boot)

```bash
cd backend

# Instalar dependencias
./mvnw clean install

# Configurar variables de entorno
export SPRING_PROFILES_ACTIVE=dev
export DATABASE_URL=postgresql://localhost:5432/sale_partido
export DATABASE_USER=postgres
export DATABASE_PASSWORD=postgres
export REDIS_HOST=localhost
export REDIS_PORT=6379

# Ejecutar migraciones
./mvnw flyway:migrate

# Iniciar backend
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

Backend disponible en: **http://localhost:8080**

## 3. Setup Frontend (Angular)

```bash
cd frontend

# Instalar dependencias
npm ci

# Configurar archivo de entorno
cp src/environments/environment.example.ts src/environments/environment.ts

# Editar si es necesario
# src/environments/environment.ts -> apiUrl: 'http://localhost:8080'

# Iniciar servidor dev
npm start
```

Frontend disponible en: **http://localhost:4200**

## 4. Datos de prueba

El backend cargará datos iniciales en `dev` profile:
- 2 Locales predefinidos
- 4 Canchas
- Horarios configurados
- Turnos de ejemplo (algunos disponibles, otros ocupados)

## 5. Verificar conexión

```bash
# Backend health
curl http://localhost:8080/actuator/health

# Frontend home
open http://localhost:4200

# API de locales
curl http://localhost:8080/locales
```

## Troubleshooting

### Base de datos no conecta
```bash
# Crear BD manualmente (si no existe)
createdb sale_partido
```

### Puerto 8080 en uso
```bash
# Cambiar puerto en backend/application-dev.yml
server.port=8081
```

### Puerto 4200 en uso
```bash
npm start -- --port 4300
```

### Redis no responde
```bash
# Inicia Redis en Docker
docker run -d -p 6379:6379 redis:7
```
