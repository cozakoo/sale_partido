# <!-- Encabezado de documentación -->
<div align="center">
    <img src="../assets/logo.png" alt="Logo" width="140" />

    <p>
        <img src="https://img.shields.io/badge/build-passing-brightgreen" alt="build" />
        <img src="https://img.shields.io/badge/coverage-80%25-yellow" alt="coverage" />
    </p>
</div>

---

# Troubleshooting — Problemas Comunes

**Errores más frecuentes y cómo resolverlos.**

---

## Backend (Java + Spring Boot)

### ❌ "Port 8080 already in use"

```bash
# Encontrar qué proceso usa 8080
lsof -i :8080

# Matar el proceso
kill -9 <PID>

# O cambiar puerto
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

---

### ❌ "Connection refused: PostgreSQL"

```bash
# Verificar que PostgreSQL está levantado
docker-compose ps

# Si no está:
docker-compose up -d postgres

# Verificar credenciales en application-dev.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/salepartido_database
spring.datasource.username=admin
spring.datasource.password=****
```

---

### ❌ "Tests fail with 'No qualifying bean'"

**Problema:** Spring no inyecta dependencia en test

**Solución:**
```java
// ❌ Mal
@Test
void test() {
    var service = new CanchaService(); // Error: sin inyección
}

// ✅ Bien
@ExtendWith(MockitoExtension.class)
class CanchaServiceTest {
    @Mock
    private CanchaRepository repo;

    private CanchaService service;

    @BeforeEach
    void setUp() {
        service = new CanchaService(repo); // Inyección manual
    }
}
```

---

### ❌ "N+1 Query detected by SonarQube"

**Problema:** Lazy loading en loop

```java
// ❌ Mal: 101 queries (1 + 100 items)
List<Cancha> canchas = repository.findAll();
for (Cancha c : canchas) {
    c.getLocal().getNombre(); // Query por cada cancha
}

// ✅ Bien: 1 query con JOIN
@Query("SELECT c FROM Cancha c JOIN FETCH c.local")
List<Cancha> findAllWithLocal();
```

---

### ❌ "JaCoCo coverage < 80%"

```bash
# Ver reporte detallado
./mvnw test jacoco:report
# Abre: target/site/jacoco/index.html

# Opción 1: Agregar más tests para líneas faltantes
# Opción 2: Excluir línea con @Generated o @VisibleForTesting
// @VisibleForTesting
private String helperMethod() { }
```

---

### ❌ "SonarQube bloqueador: Code Smell"

**Posibles causas:**
- Función > 50 líneas → Refactorizar
- Parámetro no usado → Remover
- Variable nunca asignada → Remover
- Comentario muerto → Remover

```bash
# Ejecutar SonarQube localmente
./mvnw sonar:sonar
# Panel: http://localhost:9000
```

---

### ❌ "Cannot find symbol: UUID"

```bash
# Verificar import
import java.util.UUID; // ✅ Correcto

# NOT
import com.sun.org.apache.xml.internal.security.utils.Base64;
```

---

### ❌ "Transactional error: No session"

**Problema:** Acceso lazy fuera de transacción

```java
// ❌ Mal
@Service
public class CanchaService {
    @Transactional(readOnly = true)
    public Cancha findById(UUID id) {
        return repo.findById(id).get(); // Lazy loaded
    }

    public void process(Cancha cancha) {
        cancha.getConfiguracionesHorarios(); // ❌ Error: sin transacción
    }
}

// ✅ Bien
@Transactional(readOnly = true)
public Cancha findByIdWithDetails(UUID id) {
    return repo.findByIdWithDetails(id); // Eager loaded
}
```

---

## Frontend (Angular + TypeScript)

### ❌ "Port 4200 already in use"

```bash
ng serve --port 4300
```

---

### ❌ "ng command not found"

```bash
# Instalar Angular CLI globalmente
npm install -g @angular/cli

# O usar local
npx ng serve
```

---

### ❌ "Cannot find module '@angular/...'"

```bash
# Reinstalar dependencias
rm -rf node_modules package-lock.json
npm install

# O solo actualizar
npm update
```

---

### ❌ "Tests fail: 'Expected undefined to equal...'"

**Problema:** Componente no detecta cambios

```typescript
// ❌ Mal
fixture.componentInstance.cancha = newCancha; // Sin detectar cambios
expect(component.cancha).toEqual(newCancha); // Falla

// ✅ Bien
component.cancha = newCancha;
fixture.detectChanges(); // Ahora sí
expect(component.cancha).toEqual(newCancha);
```

---

### ❌ "E2E test hangs"

```bash
# Timeout por defecto: 30s
# Aumentar en playwright.config.ts
timeout: 60000,

# O usar waits explícitos
await page.waitForSelector('[data-testid="loaded"]', { timeout: 10000 });
```

---

### ❌ "Error: Cannot find @Input() value"

**Problema:** Input no está siendo pasado

```typescript
// ❌ Mal
<app-cancha-card></app-cancha-card> <!-- Sin input -->

// ✅ Bien
<app-cancha-card [cancha]="selectedCancha"></app-cancha-card>
```

---

### ❌ "Unsubscribe memory leak warning"

**Problema:** Suscripción no limpiada

```typescript
// ❌ Mal
ngOnInit() {
    this.service.getCanchas().subscribe(data => {
        this.canchas = data;
    }); // Nunca se desuscribe
}

// ✅ Bien
private destroyRef = inject(DestroyRef);

ngOnInit() {
    this.service.getCanchas()
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe(data => {
            this.canchas = data;
        });
}
```

---

### ❌ "Lint error: Unexpected any type"

```bash
# Verificar tsconfig.json
"strict": true,
"noImplicitAny": true,

// Opción 1: Agregar tipo explícito
const data: Cancha[] = [];

// Opción 2: Ignorar línea (último recurso)
// @ts-ignore
const data = [];
```

---

### ❌ "Build fails: 'src/assets not found'"

```bash
# Crear carpeta faltante
mkdir -p src/assets
mkdir -p src/assets/images

# O actualizar angular.json
"assets": [
  "src/favicon.ico",
  "src/assets"
]
```

---

## DevOps / Infraestructura

### ❌ "Docker container exits immediately"

```bash
# Ver logs
docker logs <container_id>

# Ver procesos
docker-compose logs postgres

# Reiniciar servicios
docker-compose down
docker-compose up -d
```

---

### ❌ "Kubernetes pod CrashLoopBackOff"

```bash
# Ver eventos
kubectl describe pod <pod_name> -n salepartido

# Ver logs
kubectl logs <pod_name> -n salepartido

# Editar e inspeccionar
kubectl edit pod <pod_name> -n salepartido
```

---

### ❌ "CI/CD pipeline falla en GitHub Actions"

```bash
# Ejecutar localmente lo que hace el runner
./mvnw clean verify

# Verificar secrets están configurados
# Dashboard → Settings → Secrets

# Ver logs en GitHub
# Repo → Actions → Last workflow
```

---

### ❌ "containerd image not found"

**Problema:** Docker construyó imagen, pero K8s no la ve

```bash
# Verificar imagen está en Docker
docker image ls | grep salepartido

# Exportar imagen a containerd
docker save salepartido-backend:latest -o /tmp/backend.tar
sudo ctr -n k8s.io images import /tmp/backend.tar

# Verificar en K8s
sudo ctr -n k8s.io images ls
```

---

## Git / GitHub

### ❌ "Permission denied: git@github.com"

```bash
# Verificar SSH key
cat ~/.ssh/id_rsa.pub

# Agregar a GitHub → Settings → SSH Keys

# O usar HTTPS (menos recomendado)
git remote set-url origin https://github.com/user/repo.git
```

---

### ❌ "Merge conflict"

```bash
# Ver conflictos
git status

# Editar archivos conflictados manualmente
# Buscar: <<<<<<< HEAD
#         =======
#         >>>>>>> branch-name

# Resolver y hacer commit
git add .
git commit -m "Resolver merge conflict"
```

---

### ❌ "Accidentally pushed to main"

```bash
# Opción 1: Revert (preferido)
git revert <commit_hash>
git push origin main

# Opción 2: Force push (⚠️ destructivo)
git reset --hard HEAD~1
git push origin main --force
# ⚠️ SOLO si nadie más clonó el repo
```

---

## General

### 📊 Ver logs de todo

```bash
# Backend
./mvnw clean install -DskipTests

# Frontend
npm run build

# Ambos
docker-compose logs -f
```

### 🔍 Buscar errores rápido

```bash
# Backend error en logs
docker-compose logs backend | grep -i "error"

# Frontend error en console
npm run build 2>&1 | grep -i "error"
```

### 🆘 Último recurso: Factory reset

```bash
# Backend
cd backend && ./mvnw clean

# Frontend
cd frontend && rm -rf node_modules dist && npm install

# Contenedores
docker-compose down -v
docker-compose up -d
```

---

**¿Problema no está aquí?** Abre issue con logs y reproduce steps.
