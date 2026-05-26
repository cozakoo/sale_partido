# Agentes - Sale Partido

**Instrucciones para cualquier IA:** OpenAI, Gemini, ChatGPT, Antigravity, etc.

## 🚀 Cómo usar

### Paso 1: Elige tu rol
- **Backend** → Lee `backend.md`
- **Frontend** → Lee `frontend.md`
- **DevOps/Infra** → Lee `infra.md`

### Paso 2: Copia TODO el contenido
```
1. Abre el archivo (ej: backend.md)
2. Selecciona TODO (Ctrl+A)
3. Copia (Ctrl+C)
```

### Paso 3: Pégalo en tu IA
Tu IA (Claude, ChatGPT, Gemini, etc) ahora tiene contexto completo.

### Paso 4: Codifica
Pide a tu IA que implemente tu feature. Respetará SOLID, testing, antipatrones prohibidos.

---

## 📚 Agentes

| Agente | Archivo | Especialidad |
|--------|---------|--------------|
| **Backineitor** ⚙️ | `backend.md` | Backend: Java 21, Spring Boot, arquitectura, testing |
| **Frontalyx** 🎨 | `frontend.md` | Frontend: Angular 20, TypeScript, componentes, UI |
| **Kuberator** ☸️ | `infra.md` | DevOps: Kubernetes, containerd, CI/CD, deploy |
| **Testeador** ✅ | `qa.md` | QA: Testing unit, integration, E2E, performance |

## 📚 Documentación

| Archivo | Para |
|---------|------|
| `API_CONTRACTS.md` | Coordinación Backend ↔ Frontend — endpoints y DTOs |
| `ONBOARDING.md` | Guía para humanos — cómo empezar a desarrollar |
| `README.md` | Este archivo |

---

## 📋 Qué incluye cada agente

Cada archivo (backend.md, frontend.md, infra.md) contiene:
- Stack técnico exacto (versiones)
- Estructura de código actual
- Épicas (E1-E9)
- SOLID principles
- Patrones y antipatrones
- Testing requirements (80%+ coverage)
- Git workflow + commits
- Checklist antes de push
- Limitaciones conocidas

---

## 🔗 Coordinación entre agentes

### Backend IA ↔ Frontend IA

Cuando Backend IA cambia un endpoint:
1. **Actualiza** `/agentes/API_CONTRACTS.md` con nuevo endpoint
2. **Frontend IA** consulta `API_CONTRACTS.md` antes de consumir
3. Ambas IAs están sincronizadas

**Ejemplo:**
```
Backend IA: "Cambié POST /canchas body"
↓
Actualiza API_CONTRACTS.md
↓
Frontend IA lee API_CONTRACTS.md
↓
Frontend IA implementa consumo correcto
```

---

## 🎯 Para humanos (Team Members)

Si eres developer:
1. **Primero:** `/doc/GITHUB_PROJECTS.md` — Entiende HUs en GitHub Projects
2. **Segundo:** `/agentes/ONBOARDING.md` — Cómo codificar paso a paso
3. **Referencia:** `/doc/` (stack, testing, troubleshooting)

---

## ⚠️ Importante

**No es documentación completa.** Para detalles técnicos profundos:
- Stack detallado → `/doc/STACK.md`
- Testing estrategia → `/doc/TESTING.md`
- Troubleshooting → `/doc/TROUBLESHOOTING.md`
- Decisiones → `/doc/Decisiones.md`

---

**Regla de oro:** Agentes sin contexto = código sin estructura. Copia el agente SIEMPRE.
