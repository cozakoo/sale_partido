# Onboarding — Cómo empezar a codificar

**Lee esto antes de tu primer commit en Sale Partido.**

---

## 1. Entiende la estructura

El proyecto está dividido en **3 agentes independientes** que cooperan:

```
/agents/
├── backend.md           ← Lee PRIMERO si trabajas en backend
├── frontend.md          ← Lee PRIMERO si trabajas en frontend
├── infra.md             ← Lee PRIMERO si trabajas en DevOps
├── API_CONTRACTS.md     ← Contrato entre backend y frontend
├── ONBOARDING.md        ← Este archivo
└── README.md            ← Índice de agentes
```

**Regla de oro:** Copia el contenido del agente en tu IA antes de codificar. No es opcional.

---

## 2. Elige tu agente

| Agente | Archivo | Después |
|--------|---------|---------|
| **Backineitor** ⚙️ (Backend) | `/agents/backend.md` | `/agents/API_CONTRACTS.md` |
| **Frontalyx** 🎨 (Frontend) | `/agents/frontend.md` | `/agents/API_CONTRACTS.md` |
| **Kuberator** ☸️ (DevOps) | `/agents/infra.md` | `/documentation/guides/TROUBLESHOOTING.md` |
| **Testeador** ✅ (QA) | `/agents/qa.md` | `/documentation/workflow/TESTING.md` |

---

## 3. Flujo de trabajo

### Paso 1: Crear rama
```bash
git checkout -b feature/E[1-9]-H[##]-descripcion-corta
```

Ejemplo: `feature/E4-H08-crear-local`

### Paso 2: Leer HU en GitHub Projects

**URL:** https://github.com/users/cozakoo/projects/2/views/3

Antes de codificar:
1. Abre el tablero
2. Busca tu HU (formato: `E#-H##`)
3. Lee descripción + acceptance criteria
4. Asígnate
5. Mueve a "In Progress"

**Ejemplo:**
```
E4-H08 — Crear local con validaciones

Criterios de Aceptación:
- POST /locales endpoint
- Validar campos obligatorios
- Tests: 85% coverage
- SonarQube sin bloqueadores
```

**Ver más:** `/documentation/workflow/GITHUB_PROJECTS.md`

### Paso 3: Usar agente con contexto
```
1. Abre tu agente: backend.md / frontend.md / infra.md
2. Copia TODO el contenido
3. Pégalo en tu IA (Claude, ChatGPT, Gemini, etc)
4. Pide que implemente tu feature
5. La IA codificará respetando SOLID, antipatrones prohibidos, etc
```

**Por qué funciona:** El agente contiene:
- Stack exacto y versiones
- Estructura real del proyecto
- SOLID principles obligatorios
- Testing requirements (80%+ coverage)
- Antipatrones prohibidos
- Git workflow

### Paso 4: Revisar código localmente
```bash
# Backend
./mvnw verify              # Tests + SonarQube
./mvnw test jacoco:report  # Coverage

# Frontend
npm run test
npm run lint
npm run e2e
```

**Si falla:** No commitees. Pide a la IA que corrija.

### Paso 5: Commit descriptivo
```bash
git commit -m "feat(E4): crear local con validaciones

- POST /locales endpoint
- Validar campos obligatorios
- Tests: 85% coverage

Fixes: #42"
```

**Regla:** Conventional Commits + referencia épica

### Paso 6: Push & PR
```bash
git push origin feature/E4-H08-crear-local
```

En GitHub:
- Título: `[E4-H08] Crear local con validaciones`
- Descripción: Qué cambió, cómo testear
- Link a issue/HU

### Paso 7: Code review
- Mínimo 2 aprobaciones
- CI/CD debe pasar ✅
- Sin CRITICAL o HIGH issues

---

## 4. Contratos Backend ↔ Frontend

**Archivo:** `/agents/API_CONTRACTS.md`

Cuando cambias un endpoint:
1. Actualiza `API_CONTRACTS.md` primero
2. Backend IA lo consulta antes de codificar
3. Frontend IA lo consulta para saber qué esperar

**Ejemplo:**
```
Backend IA cambió POST /canchas
→ Actualiza API_CONTRACTS.md con nuevo body
→ Frontend IA lee API_CONTRACTS.md
→ Frontend IA implementa consumo correcto
```

---

## 5. Principios No Negociables

### SOLID
- **S:** Una clase, una responsabilidad
- **O:** Extensible, no modificable
- **L:** Interfaces intercambiables
- **I:** Interfaces específicas
- **D:** Inyectar dependencias

### Antipatrones prohibidos
- Lógica de negocio en controllers
- Queries N+1
- Prop drilling profundo (frontend)
- Hardcodeo de valores
- Suscripciones sin desuscribir (frontend)

### Testing
- **Mínimo:** 80% coverage en lógica de negocio
- **TDD:** Escribe tests PRIMERO
- **Framework:** JUnit 5 + Mockito (backend), Jasmine + Playwright (frontend)

---

## 6. Documentos que consultar

| Documento | Para |
|-----------|------|
| `/documentation/decisions/Decisiones.md` | Decisiones arquitectónicas ya tomadas |
| `/documentation/workflow/Convenciones_de_branching.md` | Convención de commits y ramas |
| `/documentation/workflow/TESTING.md` | Estrategia de testing detallada |
| `/documentation/guides/TROUBLESHOOTING.md` | Errores comunes y soluciones |
| `/documentation/architecture/STACK.md` | Stack técnico detallado |

---

## 7. Checklist antes de hacer push

- [ ] Léí el agente completo (backend/frontend/infra.md)
- [ ] Tests pasan: `./mvnw verify` o `npm run test`
- [ ] Coverage ≥80% (líneas nuevas)
- [ ] Sin linting errors
- [ ] Branch name: `feature/E[#]-H[##]-...`
- [ ] Commit message: Conventional Commits + épica
- [ ] PR con descripción clara
- [ ] Validé que no estoy en antipatrón

---

## 8. Si algo no entiendo

**Pregunta a la IA:** "¿Por qué SOLID aquí?" o "¿Cómo testeo esto?"

**La IA no te dirá:** "Haz lo que quieras". Dirá por qué cada decisión es así.

---

## 9. Recursos rápidos

```bash
# Actualizar agentes con código actual
git pull origin main

# Ver quién hizo cada línea
git blame domain/[epic]/service/[Service].java

# Ver commits de una épica
git log --grep="E4" --oneline

# Ver diferencia con main
git diff origin/main...HEAD
```

---

**Próximo paso:** Lee tu agente. Cópialo a tu IA. Empieza a codificar.
