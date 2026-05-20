# 🏆 Sale Partido - Guía Completa del Proyecto

> Plataforma integral de gestión y reserva de espacios deportivos  
> Stack: **Java 21 Spring Boot** + **Angular 20 TypeScript** + **PostgreSQL** + **Redis** + **Kubernetes**

---

## 📋 Tabla de Contenidos

1. [Visión General](#visión-general)
2. [Stack Técnico](#stack-técnico)
3. [Estructura del Proyecto](#estructura-del-proyecto)
4. [Épicas & Módulos](#épicas--módulos)
5. [Arquitectura Técnica](#arquitectura-técnica)
6. [Desarrollo Backend](#desarrollo-backend)
7. [Desarrollo Frontend](#desarrollo-frontend)
8. [Testing & Calidad](#testing--calidad)
9. [Seguridad](#seguridad)
10. [Docker & Containers](#docker--containers)
11. [Kubernetes Deployment](#kubernetes-deployment)
12. [CI/CD Pipeline](#cicd-pipeline)
13. [Flujo de Trabajo](#flujo-de-trabajo)
14. [Troubleshooting](#troubleshooting)
15. [Comandos Rápidos](#comandos-rápidos)

---

## 🎯 Visión General

**Sale Partido** es una plataforma que conecta jugadores, propietarios de espacios deportivos y organizadores de eventos. Permite descubrir espacios, reservar, participar en eventos y competencias con sistema de notificaciones y pagos integrado.

### Características Principales
- 🔍 **Exploración:** Búsqueda de espacios con filtros y mapa
- 📅 **Reservas:** Sistema completo de reserva y cancelación
- 🎮 **Eventos:** Creación y gestión de eventos deportivos
- 🏆 **Competencias:** Torneos y rankings
- 📢 **Notificaciones:** Push, email y preferencias
- 💳 **Pagos:** Cobros integrados y reconciliación
- 📊 **Analytics:** Estadísticas y reportes

---

## 🛠️ Stack Técnico

| Componente | Tecnología | Versión |
|-----------|-----------|---------|
| **Backend** | Java + Spring Boot | 21 + 3.x |
| **Frontend** | Angular + TypeScript | 20 + 5.x |
| **Estilos** | SCSS + TailwindCSS | Latest |
| **BD Principal** | PostgreSQL | 15+ |
| **Caché** | Redis | 7+ |
| **Auth** | JWT (HS256) | Spring Security |
| **API** | REST + Swagger/OpenAPI | Auto-generated |
| **Contenedores** | Docker + Docker Compose | Latest |
| **Orquestación** | Kubernetes | 1.24+ |
| **CI/CD** | GitHub Actions | Workflows |
| **Testing** | JUnit 5 + Jasmine/Karma | Latest |
| **Build Tools** | Maven + npm/ng | Latest |
| **Monitoreo** | Prometheus + Grafana | Optional |

---

## 📁 Estructura del Proyecto

```
sale-partido/
├── backend/                          # Spring Boot backend
│   ├── src/main/java/com/saleww/
│   │   ├── domain/                   # Domain-Driven Design
│   │   │   ├── exploration/          # E1: Búsqueda
│   │   │   ├── participation/        # E2: Participación
│   │   │   ├── events/               # E3: Eventos
│   │   │   ├── spaces/               # E4: Espacios
│   │   │   ├── reservations/         # E5: Reservas
│   │   │   ├── notifications/        # E6: Notificaciones
│   │   │   ├── payments/             # E7: Pagos
│   │   │   ├── competitions/         # E8: Competencias
│   │   │   └── analytics/            # E9: Analytics
│   │   ├── shared/                   # Código común
│   │   ├── infrastructure/           # Integraciones
│   │   └── config/                   # Configuración
│   ├── src/test/                     # Tests
│   ├── Dockerfile                    # Para desarrollo
│   ├── pom.xml                       # Dependencias Maven
│   └── mvnw                          # Maven wrapper
│
├── frontend/                         # Angular frontend
│   ├── src/app/
│   │   ├── core/                     # Guards, interceptors
│   │   ├── shared/                   # Componentes reutilizables
│   │   └── modules/                  # Features por épica
│   ├── src/assets/
│   ├── src/environments/             # Config por entorno
│   ├── Dockerfile                    # Para producción
│   ├── angular.json
│   ├── package.json
│   ├── tsconfig.json
│   └── .claude/                      # Guía específica de Angular
│
├── k8s/                              # Manifiestos Kubernetes
│   ├── namespace.yaml                # Namespace salepartido
│   ├── configmap.yaml                # Variables de configuración
│   ├── secret.yaml                   # Secretos (credenciales)
│   ├── postgres.yaml                 # Base de datos
│   ├── redis.yaml                    # Cache
│   ├── backend.yaml                  # Deployment + Service
│   ├── frontend.yaml                 # Deployment + Service
│   ├── ingress.yaml                  # Routing de tráfico
│   └── README.md                     # Guía K8s
│
├── docker-compose.yml                # Stack local (dev)
├── .github/workflows/                # CI/CD pipelines
│   ├── test.yml                      # Tests automáticos
│   ├── build.yml                     # Build docker images
│   └── deploy.yml                    # Deploy a k8s
│
├── .env.example                      # Variables de entorno
├── README.md                         # Documentación general
└── CLAUDE.md                         # Este archivo

```

---

## 🎭 Épicas & Módulos

El proyecto está organizado en **9 épicas** de Domain-Driven Design:

| Épica | Nombre | Descripción | Módulo Backend | Módulo Frontend |
|-------|--------|-------------|----------------|-----------------|
| **E1** | Exploración | Búsqueda y filtrado de espacios | `domain/exploration` | `modules/exploration` |
| **E2** | Participación | Inscripción, reseñas, calificaciones | `domain/participation` | `modules/participation` |
| **E3** | Eventos | Creación y gestión de eventos | `domain/events` | `modules/events` |
| **E4** | Espacios | ABM espacios, horarios | `domain/spaces` | `modules/spaces` |
| **E5** | Reservas | Reserva, confirmación, cancelación | `domain/reservations` | `modules/reservations` |
| **E6** | Notificaciones | Push, email, preferencias | `domain/notifications` | `modules/notifications` |
| **E7** | Pagos | Cobros, transferencias | `domain/payments` | `modules/payments` |
| **E8** | Competencias | Torneos y rankings | `domain/competitions` | `modules/competitions` |
| **E9** | Analytics | Estadísticas y reportes | `domain/analytics` | `modules/analytics` |

---

## 🏗️ Arquitectura Técnica

### Backend Architecture (Spring Boot)

```
Controller Layer (REST API)
        ↓
Service Layer (Business Logic)
        ↓
Repository Layer (Data Access)
        ↓
Domain Models (DDD Entities)
        ↓
PostgreSQL Database + Redis Cache
```

**Patrones:**
- ✅ **Domain-Driven Design (DDD)**: Separación por épica/dominio
- ✅ **Layered Architecture**: Controllers → Services → Repositories
- ✅ **Dependency Injection**: Spring IoC container
- ✅ **Exception Handling**: Custom exceptions + Global @ControllerAdvice
- ✅ **Logging**: SLF4J + Logback

### Frontend Architecture (Angular)

```
Components (Presentational)
        ↓
Services (Data Access & Logic)
        ↓
State (Signals/Computed)
        ↓
HTTP Client (API Calls)
        ↓
Backend REST API
```

**Patrones:**
- ✅ **Standalone Components**: Default en Angular 20+
- ✅ **Signals**: Estado reactivo moderno
- ✅ **Services**: Inyección de dependencias
- ✅ **Guards**: Protección de rutas
- ✅ **Interceptors**: Manejo centralizado de requests

### Database Schema (PostgreSQL)

Basado en épicas y relaciones:
```sql
-- Core
users (id, email, password, name, role)
spaces (id, owner_id, name, location, capacity, price_per_hour)
reservations (id, space_id, user_id, date, time_slot, status)

-- Events & Competitions
events (id, organizer_id, name, date, space_id)
competitions (id, event_id, name, tournament_type)
participations (id, competition_id, user_id, status)

-- Payments & Notifications
payments (id, reservation_id, amount, status, method)
notifications (id, user_id, type, message, read_at)
```

---

## 💻 Desarrollo Backend

### Setup Inicial

```bash
# Instalar Java 21 (si no lo tienes)
# En macOS: brew install java@21
# En Linux: apt-get install openjdk-21-jdk

# Clonar y entrar al proyecto
cd backend

# Instalar dependencias (Maven)
./mvnw clean install

# Ejecutar en desarrollo
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Estructura de Código Backend

```
src/main/java/com/saleww/
├── domain/
│   └── [epic]/                    # Por cada épica
│       ├── entity/                # Entidades JPA
│       ├── repository/            # JPA Repositories
│       ├── service/               # Business logic
│       ├── dto/                   # DTOs
│       ├── mapper/                # Entity ↔ DTO mapping
│       └── exception/             # Excepciones custom
├── shared/
│   ├── config/                    # Configuración global
│   ├── utils/                     # Utilidades
│   ├── exception/                 # Excepciones base
│   └── dto/                       # DTOs comunes
├── infrastructure/
│   ├── persistence/               # JPA config
│   ├── security/                  # JWT, Spring Security
│   ├── cache/                     # Redis config
│   └── external/                  # Integraciones (pagos, etc.)
└── SpringBootApplication.java
```

### Convenciones Backend

**Nombres:**
```java
// Controllers
@RestController
@RequestMapping("/api/[epic]/[resource]")
public class UserController { }

// Services
@Service
public class UserService { }

// Repositories
@Repository
public interface UserRepository extends JpaRepository<User, Long> { }

// Entities
@Entity
@Table(name = "users")
public class User { }

// DTOs
public record CreateUserDTO(String email, String name) { }
```

**Exception Handling:**
```java
// Crear custom exceptions
public class UserNotFoundException extends RuntimeException { }

// Usar en service
public User getUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
}

// Manejar globalmente
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", e.getMessage()));
    }
}
```

**Testing Backend:**
```bash
# Tests unitarios
./mvnw test

# Tests unitarios + integración
./mvnw verify

# Cobertura de código
./mvnw test jacoco:report
# Ver reporte: target/site/jacoco/index.html

# SonarQube
./mvnw sonar:sonar -Dsonar.host.url=http://localhost:9000
```

---

## 🎨 Desarrollo Frontend

### Setup Inicial

```bash
cd frontend

# Instalar dependencias
npm install

# Ejecutar en desarrollo
ng serve
# Acceder en http://localhost:4200
```

### Estructura de Código Frontend

```
src/app/
├── core/                          # Crítico, no reutilizable
│   ├── interceptors/
│   │   └── auth.interceptor.ts    # JWT injection
│   ├── guards/
│   │   └── auth.guard.ts          # Route protection
│   └── services/
│       └── auth.service.ts        # Core logic
├── shared/                        # Reutilizable
│   ├── components/
│   │   ├── navbar.component.ts
│   │   └── footer.component.ts
│   ├── pipes/
│   └── directives/
└── modules/                       # Features por épica
    ├── exploration/
    │   ├── pages/
    │   ├── components/
    │   └── services/
    ├── reservations/
    └── [other epics]/
```

### Convenciones Frontend (Angular 20)

**Standalone Components:**
```typescript
import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-user-card',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `<div>{{ user().name }}</div>`,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserCardComponent {
  user = input.required<User>();
  onDelete = output<void>();
  
  handleDelete() {
    this.onDelete.emit();
  }
}
```

**Services con Injectable:**
```typescript
@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private http: HttpClient) {}
  
  getUser(id: number) {
    return this.http.get<User>(`/api/users/${id}`);
  }
}
```

**Estado con Signals:**
```typescript
export class UserListComponent {
  users = signal<User[]>([]);
  loading = signal(false);
  
  // Computed value
  userCount = computed(() => this.users().length);
  
  loadUsers() {
    this.loading.set(true);
    this.userService.getUsers().subscribe({
      next: (data) => {
        this.users.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error(err);
        this.loading.set(false);
      }
    });
  }
}
```

**Testing Frontend:**
```bash
# Tests unitarios
npm run test

# Tests E2E (Playwright)
npm run e2e

# Lint
npm run lint

# Build para producción
npm run build
```

---

## ✅ Testing & Calidad

### Testing Strategy (Test Pyramid)

```
        /\
       /  \  E2E Tests (5-10%)
      /────\
     /      \
    /  Unit  \  Integration Tests (15-25%)
   /  Tests   \
  /            \
 /──────────────\
 (70-80%)
```

### Backend - Testing

**Cobertura requerida:** ≥80% para código de negocio

```bash
# Estructura de tests
backend/src/test/java/com/saleww/
├── domain/
│   └── [epic]/
│       ├── service/
│       │   └── [Entity]ServiceTest.java
│       └── repository/
│           └── [Entity]RepositoryTest.java
└── integration/
    └── [Feature]IntegrationTest.java
```

**Ejemplo Test Unitario:**
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void testGetUserById_Success() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        
        // Act
        User result = userService.getUserById(userId);
        
        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).findById(userId);
    }
    
    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        
        assertThrows(UserNotFoundException.class, 
            () -> userService.getUserById(1L));
    }
}
```

**Ejemplo Test de Integración:**
```java
@SpringBootTest
@ActiveProfiles("test")
class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testCreateUser_Success() throws Exception {
        CreateUserDTO dto = new CreateUserDTO("test@email.com", "Test User");
        
        mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated());
        
        assertEquals(1, userRepository.count());
    }
}
```

### Frontend - Testing

**Cobertura requerida:** ≥75% para código de negocio

```typescript
// Ejemplo Test de Componente
describe('UserCardComponent', () => {
  let component: UserCardComponent;
  let fixture: ComponentFixture<UserCardComponent>;
  
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserCardComponent],
      providers: [UserService]
    }).compileComponents();
    
    fixture = TestBed.createComponent(UserCardComponent);
    component = fixture.componentInstance;
  });
  
  it('should display user name', () => {
    component.user.set({ id: 1, name: 'John Doe' });
    fixture.detectChanges();
    
    expect(fixture.nativeElement.textContent).toContain('John Doe');
  });
  
  it('should emit onDelete event', () => {
    spyOn(component.onDelete, 'emit');
    
    component.handleDelete();
    
    expect(component.onDelete.emit).toHaveBeenCalled();
  });
});
```

### SonarQube Integration

```bash
# Backend
./mvnw clean verify sonar:sonar \
  -Dsonar.projectKey=sale-partido \
  -Dsonar.sources=src/main \
  -Dsonar.tests=src/test \
  -Dsonar.host.url=http://localhost:9000

# Frontend
cd frontend
npm run sonar
```

---

## 🔐 Seguridad

### Authentication & Authorization

**JWT (HS256):**
```java
// Backend - Generar token
public String generateToken(String email) {
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hora
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
}

// Validar token
public String validateToken(String token) {
    return Jwts.parser()
        .setSigningKey(secretKey)
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
}
```

**Frontend - Manejo de JWT:**
```typescript
// auth.interceptor.ts
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}
  
  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const token = sessionStorage.getItem('access_token');
    
    if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
    
    return next.handle(req).pipe(
      catchError((err) => {
        if (err.status === 401) {
          this.authService.logout();
        }
        return throwError(() => err);
      })
    );
  }
}
```

### Protecciones Implementadas

| Protección | Nivel | Descripción |
|-----------|-------|-------------|
| **Rate Limiting** | Backend | 100 req/min por IP |
| **CORS** | Backend | Solo orígenes conocidos |
| **Input Validation** | Backend | Todos los endpoints |
| **SQL Injection** | Backend | JPA parameterized queries |
| **XSS Prevention** | Frontend | Angular HTML escaping |
| **HTTPS** | All | Obligatorio en producción |
| **Password Hashing** | Backend | bcrypt con salt |
| **CSRF Token** | Frontend | En formularios críticos |

### Checklist de Seguridad

- [ ] Tokens en `sessionStorage` (NO localStorage)
- [ ] Rate limiting activado
- [ ] CORS configurado correctamente
- [ ] Contraseñas hasheadas con bcrypt
- [ ] Logs de auditoría para acciones críticas
- [ ] HTTPS en producción
- [ ] Secretos en variables de entorno
- [ ] Input validation en todos los endpoints
- [ ] OWASP ZAP scan antes de deploy

---

## 🐳 Docker & Containers

### Estructura Docker

```
backend/Dockerfile          # Image para desarrollo
frontend/Dockerfile         # Image para producción  
docker-compose.yml          # Stack local
```

### Backend Dockerfile (Desarrollo)

```dockerfile
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# El código entra via bind-mount en docker-compose
```

### Frontend Dockerfile (Producción)

```dockerfile
# Build stage
FROM node:20 AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Runtime stage
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### Docker Compose (Local Development)

```bash
# Levantar stack
docker-compose up -d

# Ver logs
docker-compose logs -f [service]

# Detener
docker-compose down

# Eliminar volúmenes
docker-compose down -v
```

**Servicios disponibles:**
- Backend: `http://localhost:8080`
- Frontend: `http://localhost:4200`
- PostgreSQL: `localhost:5432`
- Redis: `localhost:6379`

### Buildear imágenes manualmente

```bash
# Backend
docker build -t salepartido-backend:latest ./backend

# Frontend
docker build -t salepartido-frontend:latest ./frontend

# Push a registry (si usas)
docker tag salepartido-backend:latest myregistry.azurecr.io/salepartido-backend:latest
docker push myregistry.azurecr.io/salepartido-backend:latest
```

---

## ☸️ Kubernetes Deployment

### Arquitectura K8s

```
┌─────────────────────────────────────────────┐
│         Kubernetes Cluster                  │
│  ┌─────────────────────────────────────┐   │
│  │   Namespace: salepartido            │   │
│  │  ┌──────────────────────────────┐  │   │
│  │  │ Deployments                  │  │   │
│  │  │ • Backend (Replicas: 2-3)    │  │   │
│  │  │ • Frontend (Replicas: 2-3)   │  │   │
│  │  │ • PostgreSQL (StatefulSet)   │  │   │
│  │  │ • Redis (StatefulSet)        │  │   │
│  │  └──────────────────────────────┘  │   │
│  │  ┌──────────────────────────────┐  │   │
│  │  │ Services                     │  │   │
│  │  │ • backend-service (ClusterIP)│  │   │
│  │  │ • frontend-service (NodePort)│  │   │
│  │  │ • postgres-service           │  │   │
│  │  │ • redis-service              │  │   │
│  │  └──────────────────────────────┘  │   │
│  │  ┌──────────────────────────────┐  │   │
│  │  │ Ingress                      │  │   │
│  │  │ sale-partido.com/            │  │   │
│  │  │ sale-partido.com/api         │  │   │
│  │  └──────────────────────────────┘  │   │
│  └─────────────────────────────────────┘   │
└─────────────────────────────────────────────┘
```

### K8s Manifests

**1. Namespace**
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: salepartido
```

**2. ConfigMap (Variables)**
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: salepartido-config
  namespace: salepartido
data:
  POSTGRES_USER: salepartido_user
  POSTGRES_DB: salepartido_database
  REDIS_HOST: salepartido-redis
  REDIS_PORT: "6379"
  SPRING_PORT: "8080"
  SPRING_CORS: "http://localhost:4200,https://sale-partido.com"
```

**3. Secret (Credenciales)**
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: salepartido-secret
  namespace: salepartido
type: Opaque
stringData:
  POSTGRES_PASSWORD: "tu-contraseña-segura"
  SPRING_JWT_SECRET_KEY: "tu-jwt-secret-muy-largo"
  REDIS_PASSWORD: "tu-redis-password"
```

**4. Backend Deployment**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: salepartido-backend
  namespace: salepartido
spec:
  replicas: 3
  selector:
    matchLabels:
      app: salepartido-backend
  template:
    metadata:
      labels:
        app: salepartido-backend
    spec:
      containers:
      - name: backend
        image: salepartido-backend:latest
        ports:
        - containerPort: 8080
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: salepartido-backend
  namespace: salepartido
spec:
  selector:
    app: salepartido-backend
  ports:
  - protocol: TCP
    port: 8080
    targetPort: 8080
  type: ClusterIP
```

**5. Ingress**
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: salepartido-ingress
  namespace: salepartido
  annotations:
    cert-manager.io/cluster-issuer: letsencrypt-prod
spec:
  ingressClassName: nginx
  rules:
  - host: sale-partido.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: salepartido-frontend
            port:
              number: 80
      - path: /api
        pathType: Prefix
        backend:
          service:
            name: salepartido-backend
            port:
              number: 8080
  tls:
  - hosts:
    - sale-partido.com
    secretName: salepartido-tls
```

### Desplegar en Kubernetes

```bash
# 1. Crear namespace
kubectl create namespace salepartido

# 2. Crear ConfigMap y Secret
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml

# 3. Desplegar PostgreSQL
kubectl apply -f k8s/postgres.yaml

# 4. Desplegar Redis
kubectl apply -f k8s/redis.yaml

# 5. Buildear e importar imágenes
docker build -t salepartido-backend:latest ./backend
docker build -t salepartido-frontend:latest ./frontend

# En Minikube:
minikube image load salepartido-backend:latest
minikube image load salepartido-frontend:latest

# 6. Desplegar Backend y Frontend
kubectl apply -f k8s/backend.yaml
kubectl apply -f k8s/frontend.yaml

# 7. Desplegar Ingress
kubectl apply -f k8s/ingress.yaml

# Verificar deployments
kubectl get deployments -n salepartido
kubectl get pods -n salepartido
kubectl get services -n salepartido
kubectl get ingress -n salepartido
```

### Monitoreo en K8s

```bash
# Ver pods en ejecución
kubectl get pods -n salepartido -w

# Ver logs
kubectl logs -n salepartido salepartido-backend-[pod-id]
kubectl logs -n salepartido salepartido-backend-[pod-id] -c backend

# Port forward
kubectl port-forward -n salepartido service/salepartido-backend 8080:8080
kubectl port-forward -n salepartido service/salepartido-frontend 4200:80

# Acceder a base de datos
kubectl port-forward -n salepartido service/salepartido-postgres 5432:5432
psql -h localhost -U salepartido_user -d salepartido_database

# Shell en pod
kubectl exec -it -n salepartido [pod-name] -- /bin/bash

# Describir recursos
kubectl describe pod -n salepartido [pod-name]
kubectl describe service -n salepartido salepartido-backend

# Eliminar deployment
kubectl delete deployment salepartido-backend -n salepartido
```

### Escalado en K8s

```bash
# Escalar manualmente
kubectl scale deployment salepartido-backend --replicas=5 -n salepartido

# Auto-scaling (HPA)
kubectl autoscale deployment salepartido-backend \
  --min=2 --max=10 \
  --cpu-percent=80 \
  -n salepartido
```

---

## 🔄 CI/CD Pipeline

### GitHub Actions Workflows

**.github/workflows/test.yml** - Tests automáticos
```yaml
name: Test
on: [push, pull_request]
jobs:
  test-backend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: 21
      - run: cd backend && ./mvnw test
      
  test-frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: 20
      - run: cd frontend && npm ci && npm run test
```

**.github/workflows/build.yml** - Buildear imágenes
```yaml
name: Build
on:
  push:
    branches: [dev, staging, main]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: docker/setup-buildx-action@v2
      
      - name: Build backend
        uses: docker/build-push-action@v4
        with:
          context: ./backend
          push: false
          tags: salepartido-backend:latest
      
      - name: Build frontend
        uses: docker/build-push-action@v4
        with:
          context: ./frontend
          push: false
          tags: salepartido-frontend:latest
```

**.github/workflows/deploy.yml** - Deploy a K8s (Multienv)
```yaml
name: Deploy
on:
  push:
    branches: [main, pre_prod]
jobs:
  deploy:
    environment: ${{ github.ref == 'refs/heads/main' && 'production' || 'pre_prod' }}
    env:
      NAMESPACE: ${{ github.ref == 'refs/heads/main' && 'salepartido' || 'salepartido-preprod' }}
    steps:
      - uses: actions/checkout@v4
      - name: Deploy
        run: |
          # El workflow detecta la rama y despliega al namespace correspondiente
          # main -> salepartido (Producción)
          # pre_prod -> salepartido-preprod (Demos/Testing)
          kubectl apply -f k8s/ -n ${{ env.NAMESPACE }}
```

---

## 🔀 Flujo de Trabajo

### Git Branching Strategy

main (producción) -> Despliegue automático
  ↑
  ├─ pre_prod (demos/pre-producción) -> Despliegue automático
  │   ↑
  │   └─ dev (desarrollo/historias de usuario) -> Manual/Local
  │       ↑
  │       ├─ feature/E1-H03-vista-mapa
  │       ├─ feature/E5-H21-reservas
  │       └─ hotfix/E1-bug-filtros
```

### Proceso de Desarrollo

**1. Crear rama feature**
```bash
git checkout -b feature/E1-H03-vista-mapa

# Nomenclatura:
# feature/[E#-H##]-descripcion
# E# = Épica
# H## = Historia de usuario
# descripcion = breve descripción en kebab-case
```

**2. Desarrollar con TDD**
```bash
# Backend
./mvnw test -Dtest=UserServiceTest

# Frontend
npm run test -- --watch

# Comitear frecuentemente
git commit -m "[E1-H03] Implementar vista en mapa

- Agregar MapComponent con Leaflet
- Integrar con SpaceService
- Tests: 85% coverage"
```

**3. Antes de hacer push**
```bash
# Ejecutar tests completos
./mvnw verify           # Backend
npm run test && npm run lint  # Frontend

# Actualizar rama con main/dev
git fetch origin
git rebase origin/dev

# Verificar con último main
git diff origin/main..HEAD
```

**4. Push y Pull Request**
```bash
git push origin feature/E1-H03-vista-mapa

# En GitHub, crear PR con descripción:
# - Qué cambió
# - Por qué
# - Testing realizado
# - Screenshots (si aplica)
```

**5. Code Review**
- Mínimo 2 aprobaciones
- CI/CD debe pasar ✅
- No hay conflictos

**6. Merge a dev**
```bash
git checkout dev
git pull origin dev
git merge --squash origin/feature/E1-H03-vista-mapa
git commit -m "[E1-H03] Vista en mapa"
git push origin dev
```

### Commit Message Style

```
[E#-H##] Descripción breve (máx 50 caracteres)

Descripción más detallada si es necesario.
Explica QUÉ cambió y POR QUÉ.

- Punto 1
- Punto 2
- Punto 3

Fixes: #123 (Si cierra un issue)
```

---

## 🛠️ Troubleshooting

### Backend Issues

**Error: `error: no se puede encontrar o cargar la clase principal`**
```bash
# Solución: Falta compilar
./mvnw clean compile
./mvnw spring-boot:run
```

**Error: `Connection refused: localhost:5432`**
```bash
# PostgreSQL no está corriendo
docker-compose up -d database

# O verificar puerto
lsof -i :5432
```

**Error: `java.lang.OutOfMemoryError`**
```bash
# Aumentar memoria JVM
export MAVEN_OPTS="-Xmx1024m"
./mvnw spring-boot:run
```

### Frontend Issues

**Error: `npm ERR! 404 Not Found`**
```bash
# Limpiar cache de npm
npm cache clean --force
rm -rf node_modules package-lock.json
npm install
```

**Error: `Port 4200 already in use`**
```bash
# Usar otro puerto
ng serve --port 4300
```

**Componente no se renderiza**
```typescript
// Verificar que esté en imports
@Component({
  imports: [CommonModule, YourComponent], // ✅
  standalone: true
})
```

### Docker Issues

**Error: `docker: command not found`**
```bash
# Instalar Docker
# macOS: brew install docker --cask
# Linux: apt-get install docker.io
# Windows: Descargar Docker Desktop
```

**Error: `EACCES: permission denied`**
```bash
# Agregar usuario al grupo docker
sudo usermod -aG docker $USER
newgrp docker
```

### Kubernetes Issues

**Pod en estado `Pending`**
```bash
kubectl describe pod [pod-name] -n salepartido
# Ver por qué no tiene recursos
```

**Service no accesible**
```bash
# Verificar endpoints
kubectl get endpoints -n salepartido

# Port forward si necesitas acceder
kubectl port-forward service/salepartido-backend 8080:8080 -n salepartido
```

**Imágenes no encontradas**
```bash
# En Minikube, usar eval
eval $(minikube docker-env)
docker build -t salepartido-backend:latest ./backend

# O usar imagePullPolicy: Never en manifests
```

---

## ⚡ Comandos Rápidos

### Desarrollo Local

```bash
# Setup completo
git clone https://github.com/tu-org/sale-partido.git
cd sale-partido

# Levantar stack Docker
docker-compose up -d

# Terminal 1: Backend
cd backend && ./mvnw spring-boot:run

# Terminal 2: Frontend
cd frontend && ng serve

# Verificar servicios
# Backend: http://localhost:8080
# Frontend: http://localhost:4200
# Swagger: http://localhost:8080/swagger-ui.html
```

### Testing

```bash
# Backend - Tests unitarios
./mvnw test

# Backend - Tests + integración
./mvnw verify

# Backend - Cobertura
./mvnw test jacoco:report

# Frontend - Tests
npm run test

# Frontend - E2E
npm run e2e

# Frontend - Lint
npm run lint
```

### Docker

```bash
# Ver imágenes
docker images | grep salepartido

# Ver containers en ejecución
docker ps

# Logs
docker logs -f salepartido-backend
docker logs -f salepartido-frontend

# Detener y eliminar todo
docker-compose down -v
```

### Kubernetes

```bash
# Crear namespace y desplegar todo
kubectl create namespace salepartido
kubectl apply -f k8s/

# Ver estado
kubectl get all -n salepartido

# Logs
kubectl logs -f deployment/salepartido-backend -n salepartido

# Port forward
kubectl port-forward service/salepartido-backend 8080:8080 -n salepartido

# Escalar
kubectl scale deployment salepartido-backend --replicas=5 -n salepartido

# Eliminar
kubectl delete namespace salepartido
```

### Git

```bash
# Crear rama feature
git checkout -b feature/E1-H03-vista-mapa

# Comitear
git add .
git commit -m "[E1-H03] Vista en mapa"

# Push a feature branch
git push origin feature/E1-H03-vista-mapa

# Merge a dev (después de PR)
git checkout dev
git merge feature/E1-H03-vista-mapa
git push origin dev
```

---

## 📚 Referencias Útiles

### Documentación Oficial
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Angular](https://angular.io/docs)
- [PostgreSQL](https://www.postgresql.org/docs)
- [Kubernetes](https://kubernetes.io/docs)
- [Docker](https://docs.docker.com)

### Herramientas Recomendadas
- **IDE Backend**: IntelliJ IDEA Community + Spring support
- **IDE Frontend**: VS Code + Angular Language Service
- **Kubernetes**: kubectl + kubectx + k9s
- **API Testing**: Postman o Insomnia
- **DB Client**: pgAdmin para PostgreSQL

### Comunidad
- Slack del equipo
- GitHub Issues para bugs
- PRs para code review

---

**Sale Partido** — Arquitectura robusta, desarrollo ágil, deployment sin estrés 🚀

*Última actualización: 14/05/2026*
