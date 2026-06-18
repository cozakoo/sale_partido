# <!-- Encabezado de documentación -->
<div align="center">
	<img src="assets/logo.png" alt="Logo" width="140" />

	<p>
		<img src="https://img.shields.io/badge/build-passing-brightgreen" alt="build" />
		<img src="https://img.shields.io/badge/coverage-80%25-yellow" alt="coverage" />
	</p>
</div>

---

# ⚡ Quick Start - Sale Partido

**Tiempo total: 5 minutos**

---

> ℹ️ **¿PRIMERA VEZ?** Lee `SETUP.md` para setup completo con todas las instrucciones.
>
**Esta guía es para los que ya tienen todo configurado.**

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
- Instala dependencias Node.js (octokit, dotenv)
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
- 📁 Se crea `doc/.local/data/HUS.json` con todas las HUs
- 📊 Ve las épicas y estado en consola

---

## (Opcional) Ver Tablero Actual

```bash
node scripts/download-board.js
```

Genera:
- 📄 `doc/.local/data/BOARD.json` - JSON del tablero
- 📋 `doc/.local/data/BOARD.md` - Vista legible

---

## Comenzar a Codificar

### a) Crear rama

```bash
./scripts/create-branch.sh E4-H08 crear-local
```

Crea: `feature/E4-H08-crear-local`

**Nota:** Automáticamente sincroniza GitHub Projects después de crear la rama

### b) Copiar agente IA

Abre el agente correspondiente:
- **Backend:** `agentes/backend.md` → Copia TODO
- **Frontend:** `agentes/frontend.md` → Copia TODO
- **DevOps:** `agentes/infra.md` → Copia TODO

Pega en tu IA (Claude, ChatGPT, Gemini) como contexto.

### c) Pedir a IA que codifique

La IA lee `doc/.local/data/HUS.json` y codifica respetando:
- Stack (Java/Spring Boot o Angular)
- Patrones DDD/SOLID
- Acceptance Criteria
- Testing (80% coverage)

### d) Validar y Commit

```bash
git commit -m "feat(E4-H08): crear local con validaciones"
```

**Nota:** Los commits son libres, sin validación automática de formato.

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

# 5. Crear rama
./scripts/create-branch.sh E4-H08 mi-descripcion

# 7. Codificar con IA (copiar agente)

# 8. Commit
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
- [ ] `doc/.local/data/HUS.json` se creó
- [ ] Descargaste el agente correspondiente
- [ ] Creaste primera rama con `create-branch.sh`
- [ ] Hiciste primer commit

**¿Listo?** ¡Vamos a codificar! 🚀
