# Scripts de Automatización

**Flujo automático: GitHub Projects → Local → Git Hooks**

---

## ⚙️ Scripts disponibles

| Script | Función | Uso |
|--------|---------|-----|
| `sync-github-projects.js` | Descarga HUs de GitHub Projects | `node scripts/sync-github-projects.js` |
| `download-board.js` | Descarga tablero completo con estados | `node scripts/download-board.js` |
| `validate-hu.js` | Valida HU (AC, INVEST, formato) | `node scripts/validate-hu.js E4-H08` |
| `update-card.js` | Actualiza estado de una tarjeta | `node scripts/update-card.js E4-H08 "In Progress"` |
| `create-branch.sh` | Crea rama automáticamente | `./scripts/create-branch.sh E4-H08 crear-local` |
| `setup-hooks.sh` | Instala git hooks | `./scripts/setup-hooks.sh` |
| `git-hooks/commit-msg` | Valida formato de commits | (automático) |

---

## 🚀 Flujo rápido (primeros pasos)

### Setup inicial (UNA sola vez)

```bash
# Instalar git hooks
./scripts/setup-hooks.sh

# Instalar dependencias (Node.js si no lo tienes)
npm install -g octokit dotenv
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
node scripts/sync-github-projects.js
```

Crea archivo `/doc/data/HUS.json` con todas las HUs.

### Crear rama desde HU

```bash
./scripts/create-branch.sh E4-H08 crear-local
```

Resultado: rama `feature/E4-H08-crear-local`

### Validar HU

```bash
node scripts/validate-hu.js E4-H08
```

Verifica:
- Formato E#-H##
- Título >10 caracteres
- Descripción >50 caracteres
- Criterios de aceptación (≥3)
- AC son testeable
- INVEST (manualmente)

### Codificar

1. Copiar agente correspondiente (`/agentes/`)
2. Pedir a IA que implemente
3. Tests locales: `./mvnw verify` o `npm run test`

### Commit (validado automáticamente)

```bash
git commit -m "feat(E4-H08): crear local con validaciones"
```

**Si el formato es incorrecto:**
```
❌ Commit message inválido!

Formato requerido:
  tipo(E#-H##): descripción

Ejemplo válido:
  feat(E4-H08): crear local con validaciones
```

### Push + PR

```bash
git push origin feature/E4-H08-crear-local
```

En GitHub: crear PR con "Fixes #123" → HU se cierra automáticamente

---

## 🚀 Flujo rápido (con tablero)

```bash
# 1. Descargar tablero completo
node scripts/download-board.js

# 2. Ver estado actual en BOARD.md
cat doc/BOARD.md

# 3. Cambiar estado de una tarjeta
node scripts/update-card.js E4-H08 "In Progress"

# 4. Ver HUs actualizadas
node scripts/sync-github-projects.js
```

---

## 📋 Detalles de cada script

### 1. sync-github-projects.js

**Qué hace:**
- Descarga HUs de GitHub Projects
- Extrae título, descripción, AC, estado, etc
- Guarda en `/doc/data/HUS.json`
- Agrupa por épica

**Requisitos:**
```bash
npm install octokit dotenv
```

**Uso:**
```bash
node scripts/sync-github-projects.js
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

📁 Archivo: doc/HUS.json
⏰ Actualizado: 2026-05-24T15:30:00.000Z
```

### 2. validate-hu.js

**Qué valida:**
- Formato HU_ID (E#-H##)
- Título >10 caracteres
- Descripción >50 caracteres
- Criterios de aceptación (≥3)
- AC son testeable (verbos: GET, POST, should, returns)
- Asignada a épica válida (E1-E9)
- ⚠️ INVEST (checklist manual)

**Uso:**
```bash
node scripts/validate-hu.js E4-H08
```

**Salida OK:**
```
✅ VALIDACIÓN EXITOSA

La HU cumple con los estándares. Proceder a desarrollar.
```

**Salida ERROR:**
```
❌ VALIDACIÓN FALLIDA (2 errores)

   1. Descripción debe tener >50 caracteres
   2. Mínimo 3 criterios de aceptación

Corrige en GitHub y re-sincroniza:
   node scripts/sync-github-projects.js
```

### 3. create-branch.sh

**Qué hace:**
- Checkout a `dev`
- Pull latest
- Crea rama `feature/E#-H##-descripcion`

**Uso:**
```bash
./scripts/create-branch.sh E4-H08 crear-local
```

**Requisitos:**
- Git instalado
- Rama `dev` existente

**Salida:**
```
🌿 Creando rama: feature/E4-H08-crear-local

✅ Rama creada exitosamente!

   Rama: feature/E4-H08-crear-local
   HU: E4-H08

   Próximos pasos:
   1. Validar HU: node scripts/validate-hu.js E4-H08
   2. Copiar agente correspondiente (/agentes/)
   3. Codificar con IA
   4. Commit: git commit -m 'feat(E4-H08): descripción'
```

### 4. setup-hooks.sh

**Qué instala:**
- Git hook `commit-msg` en `.git/hooks/`
- Valida commits automáticamente

**Uso (UNA sola vez):**
```bash
./scripts/setup-hooks.sh
```

**Salida:**
```
🔧 Instalando git hooks...

✅ Git hook instalado: commit-msg

   Los commits serán validados automáticamente.
   Formato requerido: feat(E#-H##): descripción
```

### 5. download-board.js

**Qué hace:**
- Descarga el tablero completo de GitHub Projects
- Genera `/doc/data/BOARD.json` (JSON)
- Genera `/doc/data/BOARD.md` (vista legible)
- Muestra estado por columna (Backlog, Todo, In Progress, etc.)

**Uso:**
```bash
node scripts/download-board.js
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
   doc/BOARD.json
   doc/BOARD.md
```

**Archivo generado (`/doc/data/BOARD.md`):**
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
node scripts/update-card.js E4-H08 "In Progress"
node scripts/update-card.js E4-H08 Done
node scripts/update-card.js E4-H08 "In Review"

# Asignar usuario
node scripts/update-card.js E4-H08 --assign cozakoo

# Ver ayuda
node scripts/update-card.js --help
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

### 7. git-hooks/commit-msg

**Qué valida:**
- Formato: `tipo(E#-H##): descripción`
- Tipos: feat, fix, test, docs, refactor, chore, perf, ci
- E#-H##: E1-E9, H01-H99

**Se ejecuta automáticamente al hacer:**
```bash
git commit -m "..."
```

**Si falla:**
```
❌ Commit message inválido!

Formato requerido:
  tipo(E#-H##): descripción

Ejemplo válido:
  feat(E4-H08): crear local con validaciones
```

**Para bypassear (NO RECOMENDADO):**
```bash
git commit --no-verify -m "..."
```

---

## 🔄 Workflow automático completo

```
1. GitHub Projects: Creas HU (E4-H08)
   ↓
2. Local: node scripts/sync-github-projects.js
   (descarga HU)
   ↓
3. Local: node scripts/validate-hu.js E4-H08
   (valida que HU esté bien formada)
   ↓
4. Local: ./scripts/create-branch.sh E4-H08 crear-local
   (crea rama automáticamente)
   ↓
5. Copiar agente → IA codifica
   ↓
6. Local: git commit -m "feat(E4-H08): crear local"
   (git hook valida automáticamente)
   ↓
7. Si formato OK → commit exitoso
   Si NO → aborta y muestra error
   ↓
8. GitHub: Push + PR con "Fixes #123"
   HU pasa a Done automáticamente
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
- IAs leen `/doc/data/HUS.json`
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
chmod +x ./scripts/create-branch.sh
chmod +x ./scripts/setup-hooks.sh
chmod +x ./scripts/git-hooks/commit-msg
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
./scripts/setup-hooks.sh
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
