# Sale Partido

**Plataforma de gestión y reserva de espacios deportivos con participación comunitaria**

> Trabajo práctico integrador de Ingeniería de Software (IF015 — UNPSJB)

---

## 📋 Descripción

**Sale Partido** es una plataforma integral que conecta jugadores, propietarios de espacios deportivos y organizadores de eventos. Permite descubrir espacios, reservar, participar en eventos y competencias, todo con un sistema de notificaciones y pagos integrado.

### Características principales

- **Exploración:** Descubrimiento y búsqueda de espacios deportivos con filtros y vista en mapa
- **Reservas:** Sistema completo de reserva, confirmación y cancelación
- **Eventos:** Creación y gestión de eventos deportivos
- **Participación:** Inscripción, reseñas y calificaciones
- **Competencias:** Torneos, rankings y tablas de posiciones
- **Notificaciones:** Sistema de alertas por push, email y preferencias
- **Pagos:** Cobros integrados, transferencias y reconciliación
- **Analytics:** Estadísticas, reportes y sugerencias automáticas

---

## 🛠️ Stack Técnico

| Capa | Tecnología |
|------|-----------|
| **Backend** | Java 21 + Spring Boot 3.x + Maven |
| **Frontend** | TypeScript 5.x + Angular 17+ + SCSS + TailwindCSS |
| **Base de Datos** | PostgreSQL 15+ |
| **Caché** | Redis 7+ |
| **Autenticación** | JWT (HS256) + Spring Security |
| **API** | REST + Swagger/OpenAPI |
| **Contenedores** | Docker + Docker Compose |
| **CI/CD** | GitHub Actions |
| **Testing** | JUnit 5 + Jasmine/Karma + Playwright (E2E) |
| **Calidad** | SonarQube + Checkstyle + ESLint |



## Requisitos previos

> Se asume que **Git** ya está instalado en el sistema.

### Docker

El entorno de desarrollo corre sobre contenedores, por lo que Docker es indispensable.

**Verificar instalación:**
```bash
docker info         # Muestra info de la instalación
docker run hello-world  # Confirma que está activo y funcionando
```

**Si no está instalado:**

| SO | Solución recomendada | Guía |
|---|---|---|
| Windows | Docker Desktop (obligatorio, requiere VM) | https://docs.docker.com/desktop/setup/install/windows-install/ |
| macOS | Docker Desktop (obligatorio, requiere VM) | https://docs.docker.com/desktop/setup/install/windows-install/ |
| Linux | Docker Engine CE (suficiente) | https://docs.docker.com/engine/install/ |

> En Linux también existe Docker Desktop, pero solo está soportado para Debian, Ubuntu, Fedora y RHEL.

---

### Java 21 (LTS)

El backend corre dentro de un contenedor con Java 21, pero también es necesario tenerlo instalado localmente para que el editor funcione correctamente sin advertencias falsas.

**Verificar instalación** (importante que aparezca `jdk` en el nombre, no solo `jre`):

```bash
# Windows
where java
where javac

# Linux
update-alternatives --config java
update-alternatives --config javac
```

**Si no está instalado:**

| SO | Guía |
|---|---|
| Windows | https://adoptium.net/installation/windows |
| Linux | https://adoptium.net/installation/linux |

> Las guías pueden mostrar Java 25 como versión sugerida. Simplemente reemplazá el número `25` por `21` en los comandos u opciones que aparezcan.

---

### Node 22 (LTS)

Para garantizar reproducibilidad, todos los integrantes del equipo deberían usar la misma versión de Node (lo que también unifica la versión de npm).

**Verificar versión instalada:**
```bash
node -v   # Debería mostrar v22.x.x
```

**Si no está instalado o la versión es distinta:**

| SO | Método recomendado |
|---|---|
| Windows | Instalar [nvm-windows](https://github.com/coreybutler/nvm-windows/releases) y luego ejecutar `nvm install 22` |
| Linux | Seguir la guía oficial: https://nodejs.org/en/download |

> Al instalar Node, npm queda incluido automáticamente.

---

### Angular CLI

```bash
npm install -g @angular/cli
```

---

### PostgreSQL

#### Linux

```bash
sudo apt install postgresql postgresql-contrib
```

**Verificar estado del servicio:**
```bash
sudo systemctl status postgresql
```

Si no está iniciado:
```bash
sudo systemctl start postgresql
```

Habilitar inicio automático:
```bash
sudo systemctl enable postgresql
```

**Acceder a PostgreSQL:**
```bash
sudo -u postgres psql
```

**Crear usuario y base de datos** con las credenciales del archivo `.env`.

**Salir de PostgreSQL:**
```sql
\q
```

**Verificar versión instalada:**
```bash
psql --version
```

#### Windows 10 / 11

**Descargar PostgreSQL:**
- Página oficial: https://www.postgresql.org/download/windows/
- Instalador EDB: https://www.enterprisedb.com/downloads/postgres-postgresql-downloads

**Durante la instalación**, dejar seleccionados:
- PostgreSQL Server
- pgAdmin 4
- Command Line Tools

**Configuración:** crear usuario y base de datos con las credenciales del archivo `.env`.

---

### Editor recomendado: VSCode

#### Extensiones

| Extensión | Link |
|---|---|
| Extension Pack for Java | https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack |
| Angular Language Service | https://marketplace.visualstudio.com/items?itemName=Angular.ng-template |
| Gemini Code Assist *(requiere Google AI Pro o superior)* | https://marketplace.visualstudio.com/items?itemName=Google.geminicodeassist |

#### Configurar la versión de Java del proyecto

Una vez instaladas las extensiones:

1. `Ctrl + Shift + P`
2. Buscar y ejecutar **"Java: Configure Java Runtime"**
3. Seleccionar la versión **21**

---

## Instalación del entorno de desarrollo

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/cozakoo/sale_partido.git
   cd sale-partido
   ```

2. **Copiar el archivo de variables de entorno**

   Copiar el archivo `.env` desde la carpeta compartida del equipo a la raíz del proyecto. Verificar que el archivo quede guardado exactamente como `.env` (sin extensión adicional).

3. **Pararse en la rama del entorno de desarrollo**
   ```bash
   git checkout dev
   ```

4. **Establecer la versión de Node**
   ```bash
   nvm use
   ```

5. **Instalar dependencias del frontend**

   ```bash
   # Linux / Windows (cmd o PowerShell v5 o menor)
   cd frontend && npm install

   # Windows (PowerShell v6 o mayor)
   cd frontend; npm install
   ```

6. **Verificar que todo funciona** levantando la infraestructura (ver sección siguiente).

---

## Uso de la infraestructura de desarrollo

### Al comenzar a trabajar

```bash
# 1. Moverse al directorio raíz del proyecto
cd sale-partido

# 2. Establecer la versión de Node
nvm use
```

### Comandos de Docker (ejecutar desde `sale_partido/`)

Los servicios disponibles son: `backend`, `database` y `redis`.

| Acción | Servicio específico | Todos los servicios |
|---|---|---|
| Levantar | `docker compose up -d [servicio]` | `docker compose up -d` |
| Bajar (detener) | `docker compose stop [servicio]` | `docker compose down` |
| Bajar (eliminar contenedor) | `docker compose rm [servicio]` | `docker compose down` |
| Reiniciar | `docker compose restart [servicio]` | `docker compose restart` |
| Ver estado | — | `docker ps` |
| Ver logs | `docker compose logs -f [servicio]` | `docker compose logs -f` |
| Abrir terminal (bash) | `docker compose exec [servicio] bash` | — |
| Abrir terminal (sh) | `docker compose exec [servicio] sh` | — |

> Se recomienda usar `bash` sobre `sh` siempre que esté disponible.

### Levantar el frontend (ejecutar desde `sale_partido/frontend`)

```bash
ng serve
```


## 📅 Estado

- **Última actualización**: 29 de abril de 2026
- **Sprint actual**: Scrum + 2 semanas de sprints
- **Estado del MVP**: En desarrollo

---

**Sale Partido** — Ingeniería de software profesional en acción ⚽🏀
