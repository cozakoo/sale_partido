# Setup Completo - Sale Partido

**Guía paso a paso para que el equipo empiece a usar todo**

---

## 📋 Requisitos Previos

Antes de hacer nada, verifica que tengas:

### 1. Git instalado
```bash
git --version
# Debe mostrar: git version 2.x.x o superior
```

### 2. Node.js 18+ instalado
```bash
node --version npm --version
# Debe mostrar: v18.x.x o superior
```

**Si no tienes Node.js:**
- Descarga desde: https://nodejs.org/ (LTS)
- MacOS: `brew install node`
- Linux: `sudo apt-get install nodejs npm`
- Windows: Ejecuta el instalador

### 3. Rama principal actualizada
```bash
git checkout main
git pull origin main
```

---

## 🚀 Setup Inicial (10 minutos)

### Paso 1: Clonar/Actualizar el repo

```bash
# Si no lo has clonado:
git clone https://github.com/cozakoo/sale_partido.git
cd sale_partido

# Si ya lo tienes:
git pull origin main
```

### Paso 2: Ejecutar setup automático

```bash
./scripts/init.sh
```

Este script hace automáticamente:
- Instala dependencias Node.js (octokit, dotenv)
- Configura git hooks (validación automática de commits)
- Crea archivo `.env` (si no existe)

**Si hay error "Permission denied":**
```bash
chmod +x ./scripts/*.sh ./scripts/git-hooks/commit-msg
./scripts/init.sh
```

### Paso 3: Generar GitHub Token (IMPORTANTE)

**¿Por qué?** Para descargar historias de usuario de GitHub Projects

**Instrucciones:**

1. Ve a: https://github.com/settings/tokens/new
2. Rellena:
   - **Token name:** `Sale Partido Sync`
   - **Expiration:** Elige "No expiration" o 90 días
3. Marca estos permisos:
   - `read:project`
   - `read:org`
4. Click "Generate token"
5. **COPIA el token** (aparece una sola vez)

### Paso 4: Configurar token en `.env`

```bash
# Edita el archivo .env creado en Step 2
nano .env  # o usa tu editor favorito
```

Busca esta línea:
```env
GITHUB_TOKEN=ghp_your_token_here
```

Reemplaza `ghp_your_token_here` con tu token real:
```env
GITHUB_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

**Guarda el archivo (Ctrl+S o Cmd+S)**

⚠️ **IMPORTANTE:** El archivo `.env` NO se commitea (está en `.gitignore`)

### Paso 5: Verificar que todo funciona

```bash
# Descarga las historias de usuario
node scripts/sync-github-projects.js
```

**Debe mostrar:**
```
✅ Sincronizado exitosamente!

📊 Estadísticas:
   Total HUs: 25
   ...

📁 Archivo: doc/data/HUS.json
```

Si ves esto ✅ **¡Estás listo!**

---

## 💡 Próximos Pasos Según tu Rol

### 👨‍💻 Backend Developer

1. **Lee la guía del agente:**
   ```
   agentes/backend.md
   ```

2. **Descarga el tablero actual:**
   ```bash
   node scripts/download-board.js
   # Ver en: doc/data/BOARD.md
   ```

3. **Busca una HU para trabajar:**
   - Abre GitHub Projects: https://github.com/users/cozakoo/projects/2
   - Encuentra una HU asignada a ti
   - Nota el ID (ej: E4-H08)

4. **Crea una rama:**
   ```bash
   ./scripts/create-branch.sh E4-H08 descripcion-corta
   # Resultado: feature/E4-H08-descripcion-corta
   ```

5. **Pide a IA que codifique:**
   - Abre `agentes/backend.md`
   - Copia TODO el contenido
   - Pégalo en tu IA (ChatGPT, Claude, Gemini, etc.)
   - Pide que implemente la HU

6. **Haz commit automático:**
   ```bash
   git commit -m "feat(E4-H08): tu descripcion aqui"
   # Git hook valida automáticamente el formato
   ```

7. **Push + PR:**
   ```bash
   git push origin feature/E4-H08-descripcion-corta
   # En GitHub: crea PR y agrrega "Fixes #123"
   ```

### 🎨 Frontend Developer

Mismo flujo que Backend, pero:
1. Lee: `agentes/frontend.md`
2. Asegúrate que tienes npm packages instalados:
   ```bash
   cd frontend
   npm install
   ```

### ☸️ DevOps/Infra

1. Lee: `agentes/infra.md`
2. Verifica Kubernetes:
   ```bash
   kubectl version
   ```

### ✅ QA/Tester

1. Lee: `doc/workflow/TESTING.md`
2. Instala Playwright:
   ```bash
   cd frontend
   npm install
   ```

---

## 📚 Documentación Importante

### Setup & Workflow
- **QUICK_START.md** — 5 pasos para empezar (para impacientes)
- **SETUP.md** — Este archivo
- **doc/workflow/GITHUB_PROJECTS.md** — Cómo funcionan las HUs

### Desarrollo
- **agentes/backend.md** — Guía para Backend
- **agentes/frontend.md** — Guía para Frontend
- **agentes/infra.md** — Guía para DevOps
- **doc/workflow/TESTING.md** — Estrategia de testing
- **doc/workflow/CONVENCIONES_DE_BRANCHING.md** — Git workflow

### Referencia
- **doc/architecture/STACK.md** — Stack técnico exacto
- **doc/architecture/Decisiones.md** — Decisiones técnicas
- **doc/guides/TROUBLESHOOTING.md** — Errores comunes

---

## 🐛 Troubleshooting Rápido

### "Permission denied" (scripts)
```bash
chmod +x ./scripts/*.sh ./scripts/git-hooks/*
```

### "GITHUB_TOKEN no configurado"
- Verifica que `.env` existe
- Verifica que `GITHUB_TOKEN=ghp_...` está en el archivo
- NO debe haber espacios alrededor del `=`

### "node command not found"
```bash
node --version
# Si no funciona, instala Node.js desde nodejs.org
```

### "module not found: octokit"
```bash
npm install octokit dotenv
```

### "Git hook no se ejecuta"
```bash
# Verifica que existe
ls -la .git/hooks/commit-msg

# Si no, reinstala
./scripts/setup-hooks.sh
```

### Commit con formato incorrecto
**Error:**
```
❌ Commit message inválido!
Formato requerido:
  tipo(E#-H##): descripción
```

**Solución:** Copia el formato exacto:
```bash
git commit -m "feat(E4-H08): tu descripcion aqui"
#             ↑    ↑      ↑  ↑
#         tipo epic-hu : descripción
```

---

## ✅ Checklist - "Estoy listo"

Cuando hayas completado TODO esto, puedes empezar a codificar:

- [ ] Node.js instalado (`node --version`)
- [ ] Git configurado (`git config user.name`)
- [ ] Repo clonado/actualizado
- [ ] `./scripts/init.sh` ejecutado exitosamente
- [ ] GitHub token generado
- [ ] Token puesto en `.env`
- [ ] `node scripts/sync-github-projects.js` funcionó
- [ ] Leíste tu agente (`agentes/tu-rol.md`)
- [ ] Entiendes el workflow (Ver sección "Próximos Pasos")

**Si marcaste TODO:** ¡Listo para codificar! 🚀

---

## 🎯 Flujo Típico (paso a paso)

```bash
# 1. Actualiza tu rama
git checkout main
git pull origin main

# 2. Descarga HUs actuales
node scripts/sync-github-projects.js

# 3. Descarga el tablero (opcional)
node scripts/download-board.js

# 4. Crea una rama nueva
./scripts/create-branch.sh E4-H08 mi-feature

# 5. Abre el agente correspondiente
cat agentes/backend.md  # o frontend.md, etc

# 6. Copia TODO el contenido del agente
# 7. Pégalo en tu IA como contexto

# 8. Pide que implemente la HU E4-H08
# 9. IA codifica...

# 10. Revisa código localmente
code .  # abre en VSCode
npm run test  # corre tests

# 11. Commit (git hook valida automáticamente)
git commit -m "feat(E4-H08): mi implementacion"

# 12. Push
git push origin feature/E4-H08-mi-feature

# 13. En GitHub: abre PR + agrega "Fixes #123"
# 14. Code review + merge a dev

# ¡Listo! 🎉
```

---

## 📞 Preguntas Frecuentes

**¿Qué pasa si borro `.env`?**
- Puedes regenerarlo: `cp .env.example .env` y agregar token de nuevo

**¿Puedo commitear `.env`?**
- ❌ NO. Ya está en `.gitignore`. Si lo commiteas por error, avisa inmediatamente.

**¿Cómo cambio de rama?**
```bash
git checkout feature/E4-H08-otra-rama
```

**¿Qué sigo si se rompe algo?**
1. Lee: `doc/guides/TROUBLESHOOTING.md`
2. Si no está, escribe en el grupo de WSP

**¿Cuándo actualizo HUs?**
```bash
node scripts/sync-github-projects.js
# Cada vez que alguien agregue una HU nueva en GitHub
```

---

## 🚀 Estás Listo

Si llegaste hasta aquí y completaste el checklist, **tu ambiente está perfecto**.

Comienza con:
```bash
./scripts/create-branch.sh E4-H01 mi-primera-tarea
```

**¡Vamos a codificar!** 💪

---

*Última actualización: 2026-05-25*
