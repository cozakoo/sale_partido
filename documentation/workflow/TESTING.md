# Testing — Estrategia y ejecución

**Cobertura mínima: 80% código de negocio**

---

## Filosofía: TDD (Test-Driven Development)

```
RED → GREEN → REFACTOR
1. Escribe test (falla)
2. Implementa código (pasa)
3. Refactoriza
```

**Regla:** Test PRIMERO, código después.

---

## Backend — Java + Spring Boot

### Frameworks
- **Unit:** JUnit 5 + Mockito
- **Integration:** TestContainers (PostgreSQL real)
- **Coverage:** JaCoCo

### Ejecutar

```bash
cd backend

# Unit tests
./mvnw test

# Unit + Integration
./mvnw verify

# Ver cobertura
./mvnw test jacoco:report
# Abre: target/site/jacoco/index.html
```

### Estructura
```
src/test/java/io/github/salepartido/api/
├── unit/
│   └── domain/[epic]/service/
│       └── [Entity]ServiceTest.java
└── integration/
    └── domain/[epic]/
        └── [Feature]IntegrationTest.java
```

### Ejemplo: Unit Test
```java
@ExtendWith(MockitoExtension.class)
class CanchaServiceTest {

    @Mock
    private CanchaRepository canchaRepository;

    private CanchaService canchaService;

    @BeforeEach
    void setUp() {
        canchaService = new CanchaService(canchaRepository);
    }

    @Test
    @DisplayName("obtenerTodasLasCanchas retorna lista")
    void obtenerTodasLasCanchas_returnsAllCanchas() {
        var cancha = new Cancha(UUID.randomUUID(), "Cancha A");
        when(canchaRepository.findAll()).thenReturn(List.of(cancha));

        var result = canchaService.obtenerTodasLasCanchas();

        assertThat(result).hasSize(1);
        verify(canchaRepository).findAll();
    }

    @Test
    @DisplayName("buscarCanchaPorId lanza excepción si no existe")
    void buscarCanchaPorId_notFound_throwsException() {
        var id = UUID.randomUUID();
        when(canchaRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> canchaService.buscarCanchaPorId(id))
            .isInstanceOf(CanchaNotFoundException.class);
    }
}
```

### Ejemplo: Integration Test
```java
@Testcontainers
class CanchaRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:15");

    private CanchaRepository repository;

    @BeforeEach
    void setUp() {
        var dataSource = new PGSimpleDataSource();
        dataSource.setUrl(postgres.getJdbcUrl());
        dataSource.setUser(postgres.getUsername());
        dataSource.setPassword(postgres.getPassword());
        repository = new JdbcCanchaRepository(dataSource);
    }

    @Test
    void save_and_findById() {
        var cancha = new Cancha(null, "Cancha A");
        var saved = repository.save(cancha);

        var found = repository.findById(saved.uuid());
        assertThat(found).isPresent().contains(saved);
    }
}
```

### Cobertura mínima
- **Backend:** 80% lógica de negocio
- **Excluir:** Getters/setters, config, main()
- **Enfoque:** Service layer + critical paths

---

## Frontend — Angular + TypeScript

### Frameworks
- **Unit:** Jasmine + Karma
- **E2E:** Playwright
- **Coverage:** nyc (Istanbul)

### Ejecutar

```bash
cd frontend

# Unit tests
npm run test

# Unit tests (watch)
npm run test:watch

# E2E tests (headless)
npm run e2e

# E2E tests (headed — ver navegador)
npm run e2e:headed

# Coverage report
npm run test -- --code-coverage
# Ver: coverage/index.html
```

### Estructura
```
src/app/
└── features/[epic]/
    ├── [feature].component.spec.ts
    ├── services/
    │   └── [feature].service.spec.ts
    └── components/
        └── [component].component.spec.ts
```

### Ejemplo: Unit Test
```typescript
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CanchaListComponent } from './cancha-list.component';
import { CanchaService } from '../services/cancha.service';
import { of } from 'rxjs';

describe('CanchaListComponent', () => {
  let component: CanchaListComponent;
  let fixture: ComponentFixture<CanchaListComponent>;
  let canchService: jasmine.SpyObj<CanchaService>;

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('CanchaService', ['getCanchas']);

    await TestBed.configureTestingModule({
      imports: [CanchaListComponent],
      providers: [{ provide: CanchaService, useValue: spy }]
    }).compileComponents();

    canchService = TestBed.inject(CanchaService) as jasmine.SpyObj<CanchaService>;
    fixture = TestBed.createComponent(CanchaListComponent);
    component = fixture.componentInstance;
  });

  it('should display canchas when loaded', () => {
    const canchas = [{ uuid: 'id1', nombre: 'Cancha A' }];
    canchService.getCanchas.and.returnValue(of(canchas));

    fixture.detectChanges();

    expect(component.canchas()).toEqual(canchas);
  });

  it('should show loading state while fetching', () => {
    canchService.getCanchas.and.returnValue(of([]));

    expect(component.loading()).toBe(true);
    fixture.detectChanges();
    expect(component.loading()).toBe(false);
  });
});
```

### Ejemplo: E2E Test
```typescript
import { test, expect } from '@playwright/test';

test('user can view canchas list', async ({ page }) => {
  await page.goto('/canchas');

  const canchList = await page.locator('[data-testid="cancha-card"]');
  expect(canchList).toHaveCount(3);

  const firstCancha = canchList.first();
  await expect(firstCancha).toContainText('Cancha A');
});

test('user can create cancha', async ({ page }) => {
  await page.goto('/canchas/crear');

  await page.fill('[data-testid="nombre-input"]', 'Cancha Nueva');
  await page.click('[data-testid="crear-btn"]');

  await expect(page.locator('text=Cancha creada')).toBeVisible();
});
```

### Cobertura mínima
- **Frontend:** 75% lógica de negocio
- **Enfoque:** Services, smart components, critical flows
- **E2E:** Solo flujos críticos (login, crear, reservar)

---

## Checklist antes de push

- [ ] Tests ejecutan sin errores
- [ ] Coverage ≥80% (backend) / ≥75% (frontend)
- [ ] Lint sin warnings
- [ ] Build exitoso
- [ ] Sin console.log o debug statements
- [ ] Sin hardcodeo de valores
- [ ] Nombres descriptivos en tests

---

## Troubleshooting

### Backend tests fallan
```bash
# Limpiar cache
./mvnw clean test

# Verificar PostgreSQL está levantado (si usas TestContainers)
docker ps | grep postgres

# Ver logs detallados
./mvnw test -X
```

### Frontend tests timeout
```bash
# Aumentar timeout en karma.conf.js
browserDisconnectTimeout: 10000,
browserDisconnectTolerance: 3,
browserNoActivityTimeout: 60000

# Ejecutar un test específico
npm run test -- --include='**/[feature].spec.ts'
```

### Coverage no refleja cambios
```bash
# Limpiar cache de coverage
rm -rf coverage/ target/site/jacoco/

./mvnw clean test jacoco:report  # Backend
npm run test -- --code-coverage  # Frontend
```

---

## Recursos

- JUnit 5: https://junit.org/junit5/docs/current/user-guide/
- Mockito: https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
- TestContainers: https://www.testcontainers.org/
- Jasmine: https://jasmine.github.io/
- Playwright: https://playwright.dev/docs/intro
- JaCoCo: https://www.eclemma.org/jacoco/
