<!-- Encabezado de documentación -->
<div align="center">
    <img src="assets/logo.png" alt="Logo" width="140" />

    <p>
        <img src="https://img.shields.io/badge/build-passing-brightgreen" alt="build" />
        <img src="https://img.shields.io/badge/coverage-80%25-yellow" alt="coverage" />
    </p>
</div>

---

# 📚 Documentación - Sale Partido

**Índice centralizado de toda la documentación del proyecto**

---

## 🏗️ Arquitectura

Decisiones técnicas, stack y diseño del proyecto.

| Documento | Descripción |
|-----------|-------------|
| [Decisiones.md](./architecture/Decisiones.md) | Decisiones arquitectónicas (ADRs) |
| [STACK.md](./architecture/STACK.md) | Stack técnico detallado con versiones |

---

## 🔄 Workflow

Convenciones, flujos de trabajo y procesos.

| Documento | Descripción |
|-----------|-------------|
| [GITHUB_PROJECTS.md](./workflow/GITHUB_PROJECTS.md) | Cómo trabajar con GitHub Projects |
| [Convenciones_de_branching.md](./workflow/Convenciones_de_branching.md) | Naming de ramas y commits |
| [TESTING.md](./workflow/TESTING.md) | Estrategia de testing (unit, integration, E2E) |

---

## 📖 Guías Prácticas

Solución de problemas y referencias rápidas.

| Documento | Descripción |
|-----------|-------------|
| [TROUBLESHOOTING.md](./guides/TROUBLESHOOTING.md) | Errores comunes y soluciones |

---

## 📊 Datos Generados (Automáticos)

Estos archivos se actualizan automáticamente con los scripts:

| Archivo | Generado por | Descripción |
|---------|--------------|-------------|
| `data/HUS.json` | `sync-github-projects.js` | Historias de usuario sincronizadas |
| `data/BOARD.json` | `download-board.js` | Tablero de GitHub Projects (JSON) |
| `data/BOARD.md` | `download-board.js` | Tablero legible (Markdown) |
| `data/SUBISSUES.json` | `sync-subissues.js` | Sub-issues vinculadas |

**Cómo actualizarlos:**
```bash
node scripts/sync-github-projects.js   # Descarga HUs
node scripts/download-board.js         # Descarga tablero
node scripts/sync-subissues.js         # Descarga sub-issues
```

---

## 🚀 Para Empezar Rápido

1. **Lee primero:** [GITHUB_PROJECTS.md](./workflow/GITHUB_PROJECTS.md)
2. **Entiende el stack:** [STACK.md](./architecture/STACK.md)
3. **Testing:** [TESTING.md](./workflow/TESTING.md)
4. **Si hay problemas:** [TROUBLESHOOTING.md](./guides/TROUBLESHOOTING.md)

---

## 📁 Estructura

```
doc/
├── README.md                               # Este archivo
├── architecture/                           # Decisiones y diseño
│   ├── Decisiones.md
│   └── STACK.md
├── workflow/                               # Flujos de trabajo
│   ├── GITHUB_PROJECTS.md
│   ├── Convenciones_de_branching.md
│   └── TESTING.md
├── guides/                                 # Guías prácticas
│   └── TROUBLESHOOTING.md
└── data/                                   # Datos generados automáticamente
    ├── BOARD.json
    ├── BOARD.md
    ├── HUS.json
    └── SUBISSUES.json
```

---

*Última actualización: 2026-05-25*
