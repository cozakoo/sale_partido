# Scripts de Automatización

**Flujo automático: GitHub Projects → Local → Git Hooks**

---

## 📁 Estructura

```
scripts/
├── setup/          # 🚀 Configuración del proyecto
│   ├── init.sh                    # Setup inicial completo
│   ├── setup-local.sh             # Setup local (backend + frontend)
│   ├── setup-env-preproduction.sh # Setup pre-producción
│   ├── use-preproduction.sh       # Usar ambiente pre-producción
│   └── use-production.sh          # Usar ambiente producción
├── github/         # 🔗 GitHub Projects integration
│   ├── sync-github-projects.js    # Descarga HUs
│   ├── download-board.js          # Descarga tablero completo
│   ├── update-card.js             # Actualiza estado de tarjeta
│   └── sync-subissues.js          # Sincroniza sub-issues
├── git/            # 🌿 Git workflow
│   ├── create-branch.sh           # Crea rama automáticamente
│   ├── setup-hooks.sh             # Instala git hooks
│   └── hooks/
│       └── commit-msg             # Valida formato de commits (automático)
├── changelog/      # 📝 Changelog generation
│   └── generate-changelog.sh       # Genera CHANGELOG.md
├── dev/            # 💻 Desarrollo (Node.js)
│   ├── db.js                       # Ejecutar SQL desde archivos
│   ├── start.js                    # Levantar ambiente (backend + frontend + db)
│   ├── test.js                     # Ejecutar tests
│   ├── staging.js                  # Staging por comando (git add)
│   └── README.md                   # Documentación de dev scripts
├── sql/            # 🗄️ Data initialization
│   └── seed-locales.sql           # Data seeds
└── README.md       # Este archivo
```

---

## ⚙️ Scripts disponibles (por categoría)

### 🚀 Setup inicial
| Script | Función | Uso |
|--------|---------|-----|
| `setup/init.sh` | Setup completo (deps, hooks, .env) | `./scripts/setup/init.sh` |
| `setup/setup-local.sh` | Setup local (backend + frontend) | `./scripts/setup/setup-local.sh` |
| `setup/setup-env-preproduction.sh` | Setup pre-producción | `./scripts/setup/setup-env-preproduction.sh` |
| `setup/use-preproduction.sh` | Usar ambiente pre-prod | `./scripts/setup/use-preproduction.sh` |
| `setup/use-production.sh` | Usar ambiente producción | `./scripts/setup/use-production.sh` |

### 🔗 GitHub Projects
| Script | Función | Uso |
|--------|---------|-----|
| `github/sync-github-projects.js` | Descarga HUs de GitHub Projects | `node scripts/github/sync-github-projects.js` |
| `github/download-board.js` | Descarga tablero completo con estados | `node scripts/github/download-board.js` |
| `github/update-card.js` | Actualiza estado de una tarjeta | `node scripts/github/update-card.js E4-H08 "In Progress"` |
| `github/sync-subissues.js` | Sincroniza sub-issues | `node scripts/github/sync-subissues.js` |

### 🌿 Git workflow
| Script | Función | Uso |
|--------|---------|-----|
| `git/create-branch.sh` | Crea rama con tipo (feature, bugfix, hotfix, refactor, chore, test) | `./scripts/git/create-branch.sh E4-H08 crear-local [tipo]` |
| `git/setup-hooks.sh` | Setup de git (sin validación activa) | `./scripts/git/setup-hooks.sh` |

### 📝 Changelog
| Script | Función | Uso |
|--------|---------|-----|
| `changelog/generate-changelog.sh` | Genera CHANGELOG.md | `./scripts/changelog/generate-changelog.sh` |

### 💻 Desarrollo (Node.js)
| Script | Función | Uso |
|--------|---------|-----|
| `dev/db.js` | Ejecutar SQL desde archivos | `node scripts/dev/db.js run scripts/sql/seed-locales.sql` |
| `dev/start.js` | Levantar backend + frontend + db | `node scripts/dev/start.js` |
| `dev/test.js` | Ejecutar tests (backend + frontend) | `node scripts/dev/test.js all --coverage` |
| `dev/staging.js` | Staging por comando (git add) | `node scripts/dev/staging.js backend` |

**Ver más:** `scripts/dev/README.md`

### 🗄️ Database
| Script | Función | Uso |
|--------|---------|-----|
| `sql/seed-locales.sql` | Data seeds iniciales | `psql -f scripts/sql/seed-locales.sql` |

---

## 🚀 Flujo rápido (primeros pasos)

### Setup inicial (UNA sola vez)

```bash
# Instalar dependencias (Node.js si no lo tienes)
npm install -g octokit dotenv

# (Sin git hooks - commits libres, sin validación)
```

### Configurar token GitHub

Crear archivo `.env` en raíz del proyecto:

```env
GITHUB_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxxx
```

**Generar token:**
1. Ir a https://github.com/settings/tokens
2. Click "Generate new token" (classic)
3. Permisos: `read:project`, `read:org`
4. Copiar token → `.env`

⚠️ **NO commitear `.env`** (ya está en `.gitignore`)

### Sincronizar HUs

```bash
node scripts/github/sync-github-projects.js
```

Crea archivo `documentation/.local/data/HUS.json` con todas las HUs.

### Crear rama desde HU

```bash
# Default: feature/
./scripts/git/create-branch.sh E4-H08 crear-local
# Resultado: feature/E4-H08-crear-local

# Con tipo específico
./scripts/git/create-branch.sh E4-H08 corregir-bug bugfix
# Resultado: bugfix/E4-H08-corregir-bug

./scripts/git/create-branch.sh E4-H08 fix-critico hotfix
# Resultado: hotfix/E4-H08-fix-critico

./scripts/git/create-branch.sh E4-H08 mejorar-auth refactor
# Resultado: refactor/E4-H08-mejorar-auth
```

**Tipos disponibles:** feature, bugfix, hotfix, refactor, chore, test

### Codificar

1. Copiar agente correspondiente (`/agents/`)
2. Pedir a IA que implemente
3. Tests locales: `./mvnw verify` o `npm run test`

### Commit

```bash
git commit -m "Tu mensaje de commit aquí"
```

**Sin validación automática** — Los commits son libres sin restricciones de formato

### Push + PR

```bash
git push origin feature/E4-H08-crear-local
```

En GitHub: crear PR con "Fixes #123" → HU se cierra automáticamente

---

## 🚀 Flujo rápido (con tablero)

```bash
# 1. Descargar tablero completo
node scripts/github/download-board.js

# 2. Ver estado actual en BOARD.md
cat documentation/.local/data/BOARD.md

# 3. Cambiar estado de una tarjeta
node scripts/github/update-card.js E4-H08 "In Progress"

# 4. Ver HUs actualizadas
node scripts/github/sync-github-projects.js
```

---

## 📋 Detalles de cada script

### 1. sync-github-projects.js

**Qué hace:**
- Descarga HUs de GitHub Projects
- Extrae título, descripción, AC, estado, etc
- Guarda en `/documentation/.local/data/HUS.json`
- Agrupa por épica

**Requisitos:**
```bash
npm install octokit dotenv
```

**Uso:**
```bash
node scripts/github/sync-github-projects.js
```

**Salida:**
```
📡 Sincronizando HUs de GitHub Projects...

✅ Sincronizado exitosamente!

📊 Estadísticas:
   Total HUs: 25
   Por epic:
     E1: 5
     E2: 4
     E3: 3
     E4: 8
     ...

📁 Archivo: documentation/.local/data/HUS.json
⏰ Actualizado: 2026-05-24T15:30:00.000Z
```

### 2. create-branch.sh

**Qué hace:**
- Checkout a `dev`
- Pull latest
- Crea rama `feature/E#-H##-descripcion` (u otro tipo)
- **Sincroniza automáticamente GitHub Projects** 📡

**Uso:**
```bash
./scripts/git/create-branch.sh E4-H08 crear-local           # feature (default)
./scripts/git/create-branch.sh E4-H08 corregir-bug bugfix   # bugfix
./scripts/git/create-branch.sh E4-H08 mejorar-auth refactor # refactor
```

**Tipos de rama soportados:** feature, bugfix, hotfix, refactor, chore, test

**Requisitos:**
- Git instalado
- Rama `dev` existente
- GITHUB_TOKEN configurado en `.env` (para sincronización)

**Salida:**
```
🌿 Creando rama: feature/E4-H08-crear-local

✅ Rama creada exitosamente!

   Rama: feature/E4-H08-crear-local
   HU: E4-H08
   Tipo: feature

📡 Sincronizando GitHub Projects...
✅ GitHub Projects sincronizado

   Próximos pasos:
   1. Copiar agente correspondiente (/agents/)
   2. Codificar con IA
   3. Commit: git commit -m 'tu mensaje aquí'
```

### 3. setup-hooks.sh

**Estado:**
- No hay hooks de validación activos
- Los commits son libres sin restricciones

**Uso (opcional):**
```bash
./scripts/git/setup-hooks.sh
```

**Nota:**
En el futuro se pueden agregar hooks cuando se establezcan convenciones de commits

### 4. download-board.js

**Qué hace:**
- Descarga el tablero completo de GitHub Projects
- Genera `/documentation/.local/data/BOARD.json` (JSON)
- Genera `/documentation/.local/data/BOARD.md` (vista legible)
- Muestra estado por columna (Backlog, Todo, In Progress, etc.)

**Uso:**
```bash
node scripts/github/download-board.js
```

**Salida:**
```
📥 Descargando tablero de GitHub Projects...

✅ Tablero descargado exitosamente!

📊 Estado actual:

   Backlog: 3
   In Progress: 5
   In Review: 2
   Done: 15

📁 Archivos generados:
   documentation/.local/data/BOARD.json
   documentation/.local/data/BOARD.md
```

**Archivo generado (`documentation/.local/data/BOARD.md`):**
```markdown
## In Progress

- **[E4-H08]** Crear local con validaciones
  - Prioridad: High
  - Asignado: cozakoo
  - [Ver en GitHub](...)

## Done

- **[E4-H01]** ABM espacios
  - Prioridad: High
  - Asignado: cozakoo
  - [Ver en GitHub](...)
```

---

### 6. update-card.js

**Qué hace:**
- Actualiza estado de una tarjeta en GitHub Projects
- Asigna usuarios
- Muestra estado actual

**Uso:**
```bash
# Cambiar estado
node scripts/github/update-card.js E4-H08 "In Progress"
node scripts/github/update-card.js E4-H08 Done
node scripts/github/update-card.js E4-H08 "In Review"

# Asignar usuario
node scripts/github/update-card.js E4-H08 --assign cozakoo

# Ver ayuda
node scripts/github/update-card.js --help
```

**Salida:**
```
📝 Actualizando: E4-H08 - Crear local con validaciones
   Estado: Backlog → In Progress

✅ Estado actualizado a: In Progress
   Ver en GitHub: https://github.com/cozakoo/sale-partido/issues/123
```

**Estados válidos:**
```
Backlog
Todo
In Progress
In Review
Done
```

---

---

## 🔄 Workflow completo

```
1. GitHub Projects: Creas HU (E4-H08)
   ↓
2. Local: node scripts/github/sync-github-projects.js
   (descarga HU)
   ↓
3. Local: ./scripts/git/create-branch.sh E4-H08 crear-local
   (crea rama automáticamente)
   ↓
5. Copiar agente → IA codifica
   ↓
6. Local: git commit -m "Tu mensaje aquí"
   (sin validación automática)
   ↓
7. GitHub: Push + PR
   ↓
8. GitHub: Cerrar HU manualmente o con "Fixes #123"
```

---

## 📊 Archivo HUS.json

Estructura del archivo generado:

```json
[
  {
    "huId": "E4-H08",
    "number": 123,
    "title": "[E4-H08] Crear local con validaciones",
    "description": "Contexto...",
    "status": "In Progress",
    "epic": "E4",
    "priority": "High",
    "owner": "cozakoo",
    "labels": ["backend", "testing"],
    "acceptanceCriteria": [
      {
        "text": "POST /locales endpoint",
        "done": false
      },
      {
        "text": "Validar campos obligatorios",
        "done": false
      }
    ],
    "url": "https://github.com/cozakoo/sale-partido/issues/123",
    "createdAt": "2026-05-20T...",
    "updatedAt": "2026-05-24T..."
  }
]
```

**Uso en IAs:**
- IAs leen `documentation/.local/data/HUS.json`
- Extraen descripción + AC
- Codifican respetando AC

---

## 🐛 Troubleshooting

### "GITHUB_TOKEN no configurado"

```bash
# Crear .env
echo 'GITHUB_TOKEN=ghp_xxxxxxxxxxxxx' > .env

# Generar token en https://github.com/settings/tokens
```

### "Permission denied" (create-branch.sh)

```bash
chmod +x ./scripts/git/create-branch.sh
chmod +x ./scripts/git/setup-hooks.sh
chmod +x ./scripts/git/hooks/commit-msg
```

### "Rama no se crea"

```bash
# Verificar que estás en repo git
git status

# Verificar rama dev existe
git branch -a | grep dev

# Si no existe, crearla
git checkout -b dev origin/dev
```

### "Git hook no se ejecuta"

```bash
# Verificar que .git/hooks/commit-msg existe
ls -la .git/hooks/commit-msg

# Si no, ejecutar setup
./scripts/git/setup-hooks.sh
```

### "Bypass git hook (emergencia)"

```bash
git commit --no-verify -m "..."
```

⚠️ **Solo si sabes qué haces**

---

## 📚 Recursos

- GitHub Projects GraphQL: https://docs.github.com/en/graphql
- Git hooks: https://git-scm.com/docs/githooks
- Conventional Commits: https://www.conventionalcommits.org/
