# ⚡ Quick Start - Sale Partido

**Tiempo total: 5 minutos**

---

> ℹ️ **¿PRIMERA VEZ?** Lee `SETUP.md` para setup completo con todas las instrucciones.
>
> **Esta guía es para los que ya tienen todo configurado.**

---

## Clonar el repo

```bash
git clone https://github.com/cozakoo/sale-partido.git
cd sale-partido
```

---

## Obtener GitHub Token

1. Ir a: https://github.com/settings/tokens/new
2. Nombre: `Sale Partido Sync`
3. Marcar: `read:project` y `read:org`
4. Click "Generate token"
5. **Copiar el token** (se muestra una sola vez)

---

## Ejecutar Setup Automático

```bash
./scripts/init.sh
```

Este script:
- Crea `.env` desde `.env.example`
- Instala git hooks (validación automática de commits)
- Hace ejecutables todos los scripts
- Verifica Node.js y dependencias

---

## Configurar GitHub Token

Editar `.env` y reemplazar:

```env
GITHUB_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxxx
```

Con el token que copiaste en el paso 2.

---

## Sincronizar Historias de Usuario

```bash
node scripts/sync-github-projects.js
```

Resultado:
- 📁 Se crea `/doc/data/HUS.json` con todas las HUs
- 📊 Ve las épicas y estado en consola

---

## (Opcional) Ver Tablero Actual

```bash
node scripts/download-board.js
```

Genera:
- 📄 `/doc/data/BOARD.json` - JSON del tablero
- 📋 `/doc/data/BOARD.md` - Vista legible

---

## Comenzar a Codificar

### a) Validar HU

```bash
node scripts/validate-hu.js E4-H08
```

Verifica:
- Formato E#-H##
- Título >10 caracteres
- Descripción >50 caracteres
- AC ≥3 y testeables

### b) Crear rama

```bash
./scripts/create-branch.sh E4-H08 crear-local
```

Crea: `feature/E4-H08-crear-local`

### c) Copiar agente IA

Abre el agente correspondiente:
- **Backend:** `agentes/backend.md` → Copia TODO
- **Frontend:** `agentes/frontend.md` → Copia TODO
- **DevOps:** `agentes/infra.md` → Copia TODO

Pega en tu IA (Claude, ChatGPT, Gemini) como contexto.

### d) Pedir a IA que codifique

La IA lee `/doc/data/HUS.json` y codifica respetando:
- Stack (Java/Spring Boot o Angular)
- Patrones DDD/SOLID
- Acceptance Criteria
- Testing (80% coverage)

### e) Validar y Commit

```bash
git commit -m "feat(E4-H08): crear local con validaciones"
```

Git hook valida automáticamente:
- Formato: `tipo(E#-H##): descripción`
- Tipos válidos: feat, fix, test, docs, refactor, chore, perf, ci

Si falla → error claro + vuelves a intentar.

### f) Push + PR

```bash
git push origin feature/E4-H08-crear-local
```

En GitHub:
- Abre PR
- Agrega "Fixes #123" en descripción
- HU se cierra automáticamente al mergear

---

## 📖 Documentación Completa

- `scripts/README.md` — Todos los scripts disponibles
- `agentes/ONBOARDING.md` — Flujo detallado
- `doc/TESTING.md` — Estrategia de testing
- `doc/STACK.md` — Stack técnico exacto
- `doc/TROUBLESHOOTING.md` — Errores comunes

---

## 🚀 Comando "Haz Todo"

Si quieres saltarte pasos:

```bash
# 1. Setup automático
./scripts/init.sh

# 2. Editar .env manualmente con tu GITHUB_TOKEN

# 3. Sincronizar HUs
node scripts/sync-github-projects.js

# 4. Descargar tablero
node scripts/download-board.js

# 5. Validar una HU
node scripts/validate-hu.js E4-H08

# 6. Crear rama
./scripts/create-branch.sh E4-H08 mi-descripcion

# 7. Codificar con IA (copiar agente)

# 8. Commit automático (git hook valida)
git commit -m "feat(E4-H08): descripcion"

# 9. Push
git push origin feature/E4-H08-mi-descripcion
```

---

## ✅ Checklist Primeros Pasos

- [ ] GitHub token generado y guardado
- [ ] `./scripts/init.sh` ejecutado exitosamente
- [ ] `.env` configurado con token
- [ ] `node scripts/sync-github-projects.js` corrió sin errores
- [ ] `/doc/data/HUS.json` se creó
- [ ] Descargaste el agente correspondiente
- [ ] Creaste primera rama con `create-branch.sh`
- [ ] Hiciste primer commit (hook validó automáticamente)

**¿Listo?** ¡Vamos a codificar! 🚀
