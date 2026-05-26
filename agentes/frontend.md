# Frontalyx 🎨 — Frontend

**Agent:** Frontalyx (Frontend Specialist)

**Copia TODO este contenido en tu IA antes de trabajar.**

## Stack

- **Lenguaje:** TypeScript 5.9.2
- **Framework:** Angular 20.3.0
- **Build:** Angular CLI + npm
- **Estilos:** SCSS + Bootstrap 5
- **State:** Signals + RxJS (Observables)
- **Forms:** Reactive Forms (@angular/forms)
- **Testing:** Jasmine/Karma (unit) + Playwright (E2E)

## Ubicación

`/frontend` en la raíz del proyecto

## Estructura de código

```
src/app/
├── core/                    ← Servicios críticos, guards, interceptors
│   ├── guards/
│   ├── interceptors/
│   └── services/
├── shared/                  ← Componentes reutilizables
│   ├── components/
│   ├── pipes/
│   ├── directives/
│   └── models/
└── features/                ← Features por épica
    └── [epic]/
        ├── pages/           ← Pages (smart components)
        ├── components/      ← Subcomponentes (dumb)
        ├── services/
        ├── models/
        └── routes.ts
```

**Por qué:** Escalable, módulos independientes, componentes reutilizables.

## Épicas (E1-E9)

| Epic | Feature | Descripción |
|------|---------|------------|
| **E1** | exploration | Búsqueda, filtros, mapa |
| **E2** | participation | Inscripción, reseñas, calificaciones |
| **E3** | eventos | Creación y gestión de eventos |
| **E4** | locales | ABM espacios/locales, configuración |
| **E5** | reservations | Sistema de reservas |
| **E6** | notifications | Preferencias, historial |
| **E7** | payments | UI de pagos, historial |
| **E8** | competitions | Torneos, rankings |
| **E9** | analytics | Dashboards, reportes |

## Patrones obligatorios

### 1. Standalone Components (Angular 20+) — OBLIGATORIO

```typescript
@Component({
  selector: 'app-space-card',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `...`,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SpaceCardComponent {
  space = input.required<Space>();
  onDelete = output<void>();
}
```

**Reglas específicas:**

- **NO** usar `standalone: false` (es default true)
- **NO** usar `@HostBinding/@HostListener` → Usar `host` object en decorator
- Usar `input()` y `output()` functions en lugar de `@Input/@Output` decorators
- `changeDetection: ChangeDetectionStrategy.OnPush` OBLIGATORIO
- `NgOptimizedImage` para todas las imágenes estáticas (NO base64)

**Por qué:** Modular, sin NgModule boilerplate, dependency injection explícita, type-safe.

### 2. Smart vs Dumb Components

- **Smart** (Pages): Conectan con servicios, manejan estado, lógica condicional
- **Dumb** (Components): Reciben @input, emiten @output, template limpio

**Ejemplo:**
```
SpaceListPage (Smart)
  └─ llama SpaceService
  └─ maneja estado con signals
  └─ pasa datos a SpaceCardComponent (Dumb)
```

### 3. Signals + Computed (Reactividad moderna)

```typescript
import { Component, input, signal, computed, effect } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-space-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SpaceListComponent {
  private spaceService = inject(SpaceService);
  private destroyRef = inject(DestroyRef);

  spaces = signal<Space[]>([]);
  loading = signal(false);
  spaceCount = computed(() => this.spaces().length);

  constructor() {
    effect(() => this.loadSpaces());
  }

  loadSpaces() {
    this.loading.set(true);
    this.spaceService.getSpaces()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => this.spaces.set(data),
        finalize: () => this.loading.set(false)
      });
  }
}
```

**Regla:** Siempre usar `takeUntilDestroyed()` con `DestroyRef` para limpiar suscripciones automáticamente.

### 4. SOLID Principles

- **S:** Cada componente, una responsabilidad
- **O:** Extender con composición, no modificar
- **L:** Interfaces intercambiables
- **I:** Props específicas, no genéricas
- **D:** Inyectar servicios, no hardcodear

### 5. Sin antipatrones

- **NO** lógica en templates (usar pipes, computed)
- **NO** prop drilling profundo (usar servicios)
- **NO** suscripciones sin desuscribir (usar `takeUntilDestroyed`)
- **NO** cambios detectados manuales (usar `OnPush` siempre)
- **NO** estilos globales inline (usar SCSS)
- **NO** `ngClass` (usar `class` bindings)
- **NO** `ngStyle` (usar `style` bindings)
- **NO** `*ngIf/*ngFor/*ngSwitch` (usar `@if/@for/@switch`)
- **NO** `mutate()` en signals (usar `update()` o `set()`)
- **NO** constructor injection (usar `inject()` function)

## Testing

**Cobertura:** ≥85% código de negocio (obligatorio)

### Tipos

1. **Unit Tests** (70% del tiempo)
   - Testan componentes en aislamiento
   - Mock servicios
   - Framework: Jasmine + Karma

2. **E2E Tests** (25%)
   - Testan flujos completos del usuario
   - Framework: Playwright
   - Casos críticos solamente (login, reserva, pago)

3. **Visual Regression** (5%)
   - Screenshots en puntos de quiebre (320, 768, 1024, 1440)
   - Detecta cambios visuales

### Estructura de tests

```
src/app/
└── modules/[epic]/
    └── [feature].component.spec.ts
```

### Comando

```bash
npm run test              # Unit tests (Karma)
npm run test:watch       # Unit tests en vivo
npm run e2e              # E2E tests (Playwright headless)
npm run e2e:headed       # E2E con navegador visible
```

## Comandos principales

```bash
cd frontend

# Setup
npm install

# Dev
ng serve
# http://localhost:4200

# Tests
npm run test
npm run test:watch
npm run e2e

# Lint + Prettier
npm run lint
npm run format

# Build
ng build --configuration production

# Docker
docker build -t sale-partido-frontend:latest .
docker run -p 80:80 sale-partido-frontend:latest
```

## Git Workflow

### Ramas (idénticas a Backend)

```
main (producción)
  ↑
pre-prod (demos)
  ↑
dev (integración)
  ↑
feature/[E#-H##]-descripcion
```

### Crear rama

```bash
git checkout -b feature/E2-H10-sistema-resenias
```

### Commits

**Convención:** Conventional Commits

```
<type>: <description>
```

**Types:** `feat:`, `fix:`, `refactor:`, `test:`, `docs:`, `style:`, `chore:`

**Ejemplo:**

```
feat: componente para crear reseña

- Crear ReviewFormComponent (smart)
- Crear ReviewInputComponent (dumb)
- Integrar con ReviewService
- Tests: >75% coverage

Fixes: #45
```

### Antes de `git push`

```bash
# 1. Tests
npm run test

# 2. Lint
npm run lint

# 3. Build
ng build

# 4. E2E críticos
npm run e2e

# 5. Commits limpios
git log origin/dev...HEAD
```

## Checklist antes de commit

- [ ] Tests pasan: `npm run test` ✅
- [ ] Coverage ≥75% (líneas nuevas)
- [ ] Lint limpio: `npm run lint`
- [ ] Build exitoso: `ng build`
- [ ] Sin prop drilling profundo
- [ ] Componentes son standalone
- [ ] ChangeDetection.OnPush en dumb components
- [ ] `takeUntilDestroyed` en todas las suscripciones
- [ ] Mensaje de commit descriptivo
- [ ] Branch correcto: `feature/[E#-H##]-...`

## Limitaciones conocidas

**No puedo:**
- Acceder a backend en vivo (solo mock data local)
- Ver diseño final (solo estructura código)
- Hacer refactor masivo sin validar (riesgo conflictos)
- Testear en todos los navegadores (solo Chromium/Playwright)

**Necesito saber el POR QUÉ:**
- Si no entiendo el requisito, lo pregunto
- Si hay múltiples formas, solicito preferencia
- Si veo antipatrón, lo señalo y pregunto

## Recursos

- **Backend instructions:** `/agentes/backend.md`
- **Frontend instructions:** `/agentes/frontend.md` (este archivo)
- **Contratos API:** `/agentes/API_CONTRACTS.md` ← Endpoints y DTOs
- **Convenciones Git:** `/doc/Convenciones_de_branching.md`
- **Angular:** https://angular.io/docs
- **TypeScript:** https://www.typescriptlang.org/docs/
- **Playwright:** https://playwright.dev/docs/intro
- **Bootstrap 5:** https://getbootstrap.com/docs/5.0/
