# 7 Prompts para Desarrollar Sale Partido Más Rápido

> **100% Práctico. 0 Relleno.**
> Prompts listos para copiar y pegar. Adaptados a tu stack: Java 21 Spring Boot + Angular 20 TypeScript + PostgreSQL + Redis.

---

## 📋 Tabla de Contenidos

1. [El Arquitecto](#01-el-arquitecto) — Diseño
2. [El Constructor](#02-el-constructor) — Código
3. [El Detective](#03-el-detective) — Debugging
4. [El Crítico](#04-el-crítico) — Code Review
5. [El Optimizador](#05-el-optimizador) — Refactoring
6. [El Escudo](#06-el-escudo) — Testing
7. [El Narrador](#07-el-narrador) — Documentación

---

## 01. El Arquitecto

**Fase:** Planificación y diseño del sistema
**Cuándo usar:** Antes de una épica nueva, feature grande, o refactor arquitectónico

```
Actúa como un arquitecto de software senior con 15+ años diseñando sistemas escalables en Java Spring Boot y Angular.

Necesito que diseñes la arquitectura técnica completa para la siguiente funcionalidad en Sale Partido:

## Funcionalidad
[Describe exactamente qué quieres agregar. Ej: "Módulo de competencias: crear torneos, asignar participantes, generar matchups automáticos, rankear ganadores."]

## Contexto Actual
- Stack: Java 21 Spring Boot 3.x + Angular 20 + PostgreSQL 15 + Redis 7
- Arquitectura backend: Domain-Driven Design (9 épicas en domain/)
- Arquitectura frontend: Standalone components, Signals, RxJS
- Convenciones: Ver CLAUDE.md

## Lo que necesito que entregues
1. **Stack recomendado**: ¿Qué tecnologías nuevas (si aplica)? Justifica en una línea.
2. **Modelo de datos**: Nuevas entidades, campos clave, relaciones. SQL pseudo-code si es complejo.
3. **Estructura de archivos**: Carpetas nuevas en backend/src/main/java/com/saleww/domain/[epic]/ y frontend/src/app/modules/[epic]/
4. **Flujo principal**: Paso a paso de cómo interactúan frontend, backend, base de datos.
5. **Decisiones arquitectónicas**: 3-5 decisiones clave. POR QUÉ cada una.
6. **Riesgos técnicos**: 2-3 problemas posibles y cómo mitigarlos.
7. **Cambios en ADR**: Si afecta decisiones previas, sugiere ADR nuevo en doc/HISTORY.md

## Adherencia a principios
- SOLID: Responsabilidad única, abierto/cerrado, Liskov, segregación, inversión.
- DDD: Separación clara de épicas, bounded contexts, value objects.
- Performance: ¿Cuellos de botella con N+1? ¿Dónde cachear con Redis?
```

**Ejemplo de uso:**
```
Funcionalidad: "Módulo de competencias: crear torneos, asignar participantes, generar matchups automáticos, rankear ganadores."

Contexto: Ya tenemos event (E3), users, participations. Necesito entidad Competition que herede de Event.
```

---

## 02. El Constructor

**Fase:** Generación de código funcional
**Cuándo usar:** Cuando sabes exactamente qué construir. Necesitas código production-ready.

```
Actúa como un desarrollador senior especializado en Java 21 Spring Boot y Angular 20 TypeScript.

Necesito que implementes lo siguiente:

## Funcionalidad
[Describe exactamente qué debe hacer. Ej: "Endpoint POST /api/competitions que reciba nombre, descripción, fecha_inicio, fecha_fin, max_participantes. Valide, guarde en PostgreSQL, devuelva 201 con Competition creada."]

## Contexto técnico
- Backend: Java 21 Spring Boot 3.x, JPA/Hibernate, PostgreSQL, Redis
- Frontend: Angular 20 standalone components, Signals, TypeScript 5.x
- ORM: JPA con Hibernate
- Stack: Ver CLAUDE.md del proyecto

## Estructura esperada

### Backend
- Entity: `backend/src/main/java/com/saleww/domain/[epic]/entity/Competition.java`
- Repository: `backend/src/main/java/com/saleww/domain/[epic]/repository/CompetitionRepository.java`
- Service: `backend/src/main/java/com/saleww/domain/[epic]/service/CompetitionService.java`
- DTO: `backend/src/main/java/com/saleww/domain/[epic]/dto/CreateCompetitionDTO.java`
- Controller: `backend/src/main/java/com/saleww/domain/[epic]/controller/CompetitionController.java`

### Frontend
- Component: `frontend/src/app/modules/[epic]/pages/competition-form.component.ts`
- Service: `frontend/src/app/modules/[epic]/services/competition.service.ts`
- Tests: `*.spec.ts` con Jasmine

## Requisitos del código
1. **Production-ready:** Sin simplificaciones. Código que entra directo a main.
2. **Validación:** Inputs validados. Errores manejados. Mensajes claros.
3. **SOLID:** Responsabilidad única. Inyección de dependencias. No hardcode.
4. **Tipos:** TypeScript strict, interfaces claras. Java con generics donde aplique.
5. **Comentarios:** SOLO donde la lógica no sea obvia.
6. **Dependencias:** Lista cada dependencia nueva con `mvn dependency:tree` o `npm list`.
7. **Tests:** No pedir aquí, lo hace El Escudo después.

## Formato de entrega
- Código en bloques separados por archivo.
- Ruta completa como encabezado.
- Al final: "Cómo probarlo" con pasos exactos (curl, http request, o Angular CLI).

## Ejemplo Backend
```
## Archivo: backend/src/main/java/com/saleww/domain/competitions/entity/Competition.java

@Entity
@Table(name = "competitions")
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // ... más campos

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL)
    private Set<CompetitionParticipant> participants = new HashSet<>();
}
```
```

---

## 03. El Detective

**Fase:** Debugging y resolución de errores
**Cuándo usar:** Bug extraño, comportamiento inesperado, "no sé por dónde empezar"

```
Actúa como un debugger experto en Java Spring Boot y Angular. Necesito que analices un problema de forma metódica.

## El problema
- Qué debería pasar: [Comportamiento esperado. Ej: "Al crear una competencia, debería guardarse en BD y devolver 201"]
- Qué pasa en realidad: [Comportamiento actual. Ej: "Devuelve 200 pero sin ID. La BD no tiene registro."]
- Error visible: [Stack trace o mensaje exacto. Si no hay error, di "No visible"]
- Cuándo ocurre: [Siempre / ocasional / solo en producción / etc.]

## Contexto
- Stack: Java 21 Spring Boot / Angular 20
- ¿Qué ya intentaste?: [Lista lo que probaste. Esto evita sugerencias obvias]

## Código relevante
```
[Pega el código que crees que falla]
```

## Cómo quiero que respondas
Sigue este proceso EXACTO:

1. **Hipótesis inicial:** 3 causas posibles, ordenadas por probabilidad.
2. **Análisis línea por línea:** Dónde podría estar el fallo exactamente.
3. **Causa raíz:** La causa más probable. POR QUÉ provoca el comportamiento observado.
4. **Solución:** Código corregido con cambios resaltados.
5. **Prevención:** Patrón o práctica para evitar esto en el futuro.
```

**Ejemplo:**
```
El problema: POST /api/competitions falla silenciosamente. No retorna error, pero tampoco guarda.
Ya intenté: Verificar BD está up, comprobar SQL con PgAdmin, limpiar cache de Maven.
Código: [El controller y service]
```

---

## 04. El Crítico

**Fase:** Revisión de código
**Cuándo usar:** Antes de hacer PR. Tienes código que funciona, necesitas que un senior lo revise.

```
Actúa como un code reviewer senior exigente pero constructivo. Revisa este código como si fuera un PR en un equipo profesional.

## Código
```
[Pega tu código]
```

## Contexto
- Lenguaje/framework: [Java Spring Boot / Angular / TypeScript / etc.]
- Qué hace: [Descripción breve]
- Es código de: [API endpoint / componente / servicio / etc.]

## Dimensiones a revisar (En este orden)

1. **Seguridad** (Primero siempre)
   - SQL injection, XSS, exposición de datos sensibles, secrets hardcodeados.
   - Si hay problema: describe QUÉ, DÓNDE, muestra el código corregido.

2. **SOLID & Patrones**
   - ¿Responsabilidad única? ¿Abierto/cerrado? ¿Nombres descriptivos?
   - ¿Usa patrones adecuados? ¿Coherente con el framework?

3. **Rendimiento**
   - N+1 queries, operaciones O(n²), cargas innecesarias.
   - Redis: ¿Dónde cachear?

4. **Manejo de errores**
   - ¿Gestiona edge cases? ¿O los ignora silenciosamente?

5. **Código limpio**
   - ¿Nombres claros? ¿Funciones <50 líneas? ¿Archivos <800 líneas?

## Responde así
Para cada dimensión:
- **Estado:** Bien / Mejorable / Problema
- Si hay problema: explica QUÉ, DÓNDE, muestra corrección.

Al final:
- Puntuación global 1-10 (una línea de resumen).
- Los 3 cambios de MAYOR IMPACTO si solo pudieras cambiar 3 cosas.
```

---

## 05. El Optimizador

**Fase:** Refactoring y rendimiento
**Cuándo usar:** Código funciona pero es lento, difícil de leer, difícil de extender.

```
Actúa como ingeniero de rendimiento y clean code especializado en Java Spring Boot / Angular TypeScript.

Necesito que refactorices este código. Funciona, pero necesita mejorar.

## Código actual
```
[Pega aquí]
```

## Qué hace

[Descripción breve]

## Qué me preocupa
[Elige: "Es lento" / "Es difícil de leer" / "Es difícil de extender" / "No escala" / "Hay duplicación"]

## Entrega
- Código refactorizado completo.
- Tabla: Qué cambié | Por qué | Impacto esperado
- Si hay mejora de rendimiento: O(n²) → O(n log n)?
- Antes y después de cada bloque.

## Reglas
1. **NO cambiar comportamiento.** Entrada y salida idénticas.
2. **Explicar cada cambio.** No quiero código nuevo sin entender.
3. **Mostrar antes/después.**

Regla de oro: Si no entiende un cambio, descártalo.
```

---

## 06. El Escudo

**Fase:** Testing y cobertura
**Cuándo usar:** Tienes código, necesitas tests que lo cubran (80%+ mínimo).

```
Actúa como ingeniero de QA senior especializado en testing automatizado con JUnit 5 + Mockito (backend) y Jasmine + Karma (frontend).

Necesito una suite de tests completa para este código:

## Código a testear
```
[Pega aquí: función, endpoint, componente, servicio]
```

## Qué hace

[Descripción breve]

## Dependencias externas
[¿Llama a BD? ¿API externa? ¿Servicios? Lista qué necesita mock]

## Entrega
- Cada test con nombre descriptivo que explique qué verifica.
- Agrupados por categoría: describe/context blocks.
- Mocks/fixtures incluidos.
- Resumen final de escenarios cubiertos.

## Categorías OBLIGATORIAS

1. **Happy path** (2+ tests): El flujo normal funciona.
2. **Edge cases** (3+ tests): Vacío, null, valores extremos, tipos incorrectos, caracteres especiales.
3. **Gestión de errores** (2+ tests): Falla controladamente cuando debe.
4. **Integraciones** (si aplica): Mocks de dependencias, verificar parámetros.

## Ejemplo (JUnit 5)
```
@ExtendWith(MockitoExtension.class)
class CompetitionServiceTest {

    @Mock
    private CompetitionRepository repository;

    @InjectMocks
    private CompetitionService service;

    @Test
    void deberia_crear_competencia_exitosamente() {
        // Arrange
        // Act
        // Assert
    }

    @Test
    void deberia_lanzar_excepcion_si_nombre_vacio() {
        // ...
    }
}
```

## Ejemplo (Jasmine/Angular)
```
describe('CompetitionFormComponent', () => {
    let component: CompetitionFormComponent;
    let fixture: ComponentFixture<CompetitionFormComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [CompetitionFormComponent],
            providers: [CompetitionService]
        }).compileComponents();

        fixture = TestBed.createComponent(CompetitionFormComponent);
        component = fixture.componentInstance;
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
```
```

---

## 07. El Narrador

**Fase:** Documentación técnica
**Cuándo usar:** Código hecho, necesitas docs que tu yo del futuro entienda.

```
Actúa como technical writer senior con experiencia en documentación de proyectos open source y equipos internos.

Necesito que documentes completamente el siguiente código/módulo:

## Código/Módulo
```
[Pega el código, o describe el módulo y sus piezas principales]
```

## Contexto
- Nombre: [ej: "Módulo de competencias"]
- Stack: Java 21 Spring Boot / Angular 20
- Audiencia: [Ej: "Desarrolladores del equipo" / "Yo en 6 meses"]

## Genera

### 1. README.md (Del módulo)
- ¿Qué problema resuelve? (2-3 frases)
- Requisitos previos y versiones
- Instalación / Setup (copy-paste que funcione)
- Ejemplo de uso rápido
- Estructura de carpetas (árbol + descripción)
- Variables de entorno (tabla: nombre | desc | ejemplo | obligatoria?)
- Cómo testear
- Cómo contribuir

### 2. Documentación inline
- Docstrings/JSDoc para cada función pública.
- Qué hace, parámetros (nombre, tipo, desc), retorno, excepciones, ejemplo de uso.

### 3. API Docs (Si aplica)
- Para cada endpoint: método, ruta, desc, params, body ejemplo, respuesta exitosa, errores posibles.

### 4. Guía de integración
- Cómo este módulo se integra con otros.
- Dependencias entre servicios.
- Flujo de datos.

Tono: Directo, técnico. Nada de relleno. Ve al grano.
Regla de oro: La docs más útil responde "¿Cómo lo arranco?" en <30 segundos.
```

---

## 🔄 Flujo Recomendado (Sale Partido)

```
1. Arquitecto    → Plantea la épica / feature / decisión
                   ↓
2. Constructor   → Implementa el código
                   ↓
3. Detective     → Si algo falla, debuggea
                   ↓
4. Crítico       → Revisa el código (antes de PR)
                   ↓
5. Optimizador   → Refactoriza si es necesario
                   ↓
6. Escudo        → Escribe tests (80%+ coverage)
                   ↓
7. Narrador      → Documenta
                   ↓
Git commit + push (PREGUNTAR ANTES)
```

---

## 💡 Consejos de Oro

| Prompt | Consejo |
|--------|---------|
| **Arquitecto** | Cuanto más contexto, mejor arquitectura. Incluye problema que resuelves, usuario, integraciones externas. |
| **Constructor** | La clave es especificidad. "Haz un login" → código genérico. "Endpoint POST que valide email con regex, hashee con bcrypt..." → producción. |
| **Detective** | "Qué ya intentaste" es CLAVE. Sin eso, la IA te sugiere lo obvio. Con eso, va a causas menos evidentes. |
| **Crítico** | Úsalo aunque creas que "ya está bien". Problemas de seguridad y rendimiento rara vez son obvios para quien escribió el código. |
| **Optimizador** | Regla de oro: NO aceptes cambios que no entiendas. La tabla "Qué cambié / Por qué / Impacto" está para eso. |
| **Escudo** | Edge cases: donde la IA aporta más valor. Tú testearías happy path + errores. La IA te fuerza a pensar en strings de 10k chars, arrays vacíos, undefined, etc. |
| **Narrador** | Documentación útil = "¿Cómo lo arranco?" en <30 seg. Asegúrate que la sección "Instalación" funcione literalmente con copy-paste. |

---

## 📌 Recordatorio

> **NO son fórmulas mágicas.**
> **Son puntos de partida que:**
> - Eliminan bloqueos
> - Aceleran decisiones
> - Producen código
>
> **ADÁPTALOS A TU STACK.**
> **ITERA SOBRE LOS RESULTADOS.**
> **Y CONSTRUYE.**

---

*Documento adaptado a Sale Partido. 17/05/2026*
