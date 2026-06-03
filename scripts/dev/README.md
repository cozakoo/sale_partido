# Scripts de Desarrollo

Suite de scripts Node.js para automatizar tareas comunes en desarrollo.

Funcionan en Windows, Linux y macOS sin dependencias adicionales (Node.js + npm).

---

## Scripts disponibles

## 1. db.js - Ejecutar SQL desde archivos

Ejecuta archivos SQL directamente sin necesidad de shell o cliente.

```bash
node scripts/dev/db.js run scripts/sql/seed-locales.sql
node scripts/dev/db.js run migracion.sql
```

**Requisitos:**
- PostgreSQL instalado y `psql` disponible en PATH
- Variables de entorno en `.env`:
  - `POSTGRES_USER`
  - `POSTGRES_PASSWORD`
  - `POSTGRES_HOST`
  - `POSTGRES_PORT`
  - `POSTGRES_DATABASE_NAME`

**Ejemplo:**
```bash
# Ejecutar seed de locales
node scripts/dev/db.js run scripts/sql/seed-locales.sql
```

---

## 2. start.js - Levantar ambiente completo

Levanta backend (Spring Boot) + frontend (Angular) + base de datos (Docker).

```bash
# Levanta todo
node scripts/dev/start.js

# Solo backend
node scripts/dev/start.js backend-only

# Solo frontend
node scripts/dev/start.js frontend-only

# Backend + frontend + base de datos
node scripts/dev/start.js with-db
```

**Puertos:**
- Backend: http://localhost:8080
- Frontend: http://localhost:4200
- API Docs: http://localhost:8080/swagger-ui.html

**Requisitos:**
- Java 21 + Maven (para backend)
- Node.js 18+ + npm (para frontend)
- Docker (opcional, para base de datos)

**Ejemplo:**
```bash
# Levantar todo en un comando
node scripts/dev/start.js

# Presiona Ctrl+C para detener
```

---

## 3. test.js - Ejecutar tests

Corre tests de backend (JUnit) y frontend (Angular) con reportes opcionales.

```bash
# Todos los tests
node scripts/dev/test.js

# Solo backend
node scripts/dev/test.js backend

# Solo frontend
node scripts/dev/test.js frontend

# Con reporte de cobertura
node scripts/dev/test.js backend --coverage
node scripts/dev/test.js frontend --coverage

# Frontend en modo watch
node scripts/dev/test.js frontend --watch
```

**Backend:** Usa Maven (`mvnw verify`)
**Frontend:** Usa Angular CLI (`npm test`)

**Ejemplo:**
```bash
# Ejecutar tests y generar cobertura
node scripts/dev/test.js all --coverage

# Ver resultados en:
# - Backend: backend/target/site/jacoco/index.html
# - Frontend: frontend/coverage/index.html
```

---

## 4. staging.js - Staging por comando

Alternativa a `git add` para stagear archivos.

```bash
# Stagear todo
node scripts/dev/staging.js all

# Stagear por categoría
node scripts/dev/staging.js backend
node scripts/dev/staging.js frontend
node scripts/dev/staging.js docs
node scripts/dev/staging.js scripts

# Stagear archivo específico
node scripts/dev/staging.js src/main/java/MyClass.java

# Ver qué está stageado
node scripts/dev/staging.js --status

# Limpiar staging
node scripts/dev/staging.js --reset
```

**Ejemplo:**
```bash
# Flujo típico:
node scripts/dev/staging.js backend    # Stagear cambios de backend
node scripts/dev/staging.js --status   # Verificar
git commit -m "feat(E4-H08): nueva funcionalidad"
git push origin feature/E4-H08-nueva-funcionalidad
```

---

## Flujos comunes

### Flujo de desarrollo local

```bash
# 1. Levantar ambiente
node scripts/dev/start.js

# 2. En otra terminal, hacer cambios...

# 3. Ejecutar tests
node scripts/dev/test.js

# 4. Stagear cambios
node scripts/dev/staging.js backend

# 5. Commitear
git commit -m "feat(E4-H08): descripción"

# 6. Push
git push origin feature/E4-H08-descripcion
```

### Flujo de SQL

```bash
# 1. Crear archivo SQL
echo "INSERT INTO locales VALUES (...)" > migracion.sql

# 2. Ejecutar
node scripts/dev/db.js run migracion.sql

# 3. Verificar cambios en base de datos
```

### Flujo de testing

```bash
# 1. Ejecutar tests con cobertura
node scripts/dev/test.js all --coverage

# 2. Ver reportes
# Backend: open backend/target/site/jacoco/index.html
# Frontend: open frontend/coverage/index.html

# 3. Si todo pasa, stagear y commitear
node scripts/dev/staging.js all
git commit -m "test(E4-H08): agregar tests de validación"
```

---

## Requisitos globales

| Herramienta | Versión | Uso |
|---|---|---|
| Node.js | 18+ | Ejecutar scripts |
| npm | 9+ | Dependencias frontend |
| Java | 21+ | Backend (Maven) |
| Docker | Latest | Base de datos (opcional) |
| PostgreSQL | 14+ | Para `db.js` |
| Git | 2.x | Control de versiones |

---

## Estructura esperada

```
sale_partido/
├── backend/           # Spring Boot
│   ├── pom.xml
│   └── src/
├── frontend/          # Angular
│   ├── package.json
│   └── src/
├── scripts/
│   ├── dev/          # <- ESTOS SCRIPTS
│   │   ├── db.js
│   │   ├── start.js
│   │   ├── test.js
│   │   ├── staging.js
│   │   └── README.md
│   └── ...
└── .env              # Configuración
```

---

## Troubleshooting

### "psql not found"
```bash
# Windows: Agregar PostgreSQL bin a PATH
# Linux: sudo apt-get install postgresql-client
# macOS: brew install postgresql
```

### "mvnw not found"
```bash
# Asegurar que backend existe en la ruta correcta
# y tiene mvnw ejecutable (chmod +x mvnw)
```

### "npm: command not found"
```bash
# Instalar Node.js desde nodejs.org
```

### "Docker daemon not running"
```bash
# Iniciar Docker Desktop (macOS/Windows) o systemctl start docker (Linux)
```

---

## Roadmap de mejoras

- [ ] Script de linting automático (`lint.js`)
- [ ] Script de build para producción (`build.js`)
- [ ] Script de reseteo de ambiente (`reset.js`)
- [ ] Script de backups de base de datos (`backup.js`)
- [ ] Integración con prettier/eslint

---

## Soporte

Si tienes problemas:
1. Verifica que Node.js está instalado: `node --version`
2. Verifica `.env` tiene valores correctos
3. Lee el output del script (contiene detalles útiles)
4. Abre un issue en GitHub
