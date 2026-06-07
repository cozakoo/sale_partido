# AGENTS.md

Quick orientation for AI agents working in this repo. Repo-specific facts that
are easy to miss and that took reading multiple files to figure out.

> Full role-specific guidance lives in `agents/{backend,frontend,infra,qa}.md`.
> Architecture deep-dive in `documentation/architecture/STACK.md`.

## TL;DR commands

```bash
# Backend (Java 21, Spring Boot 4.0.6, Maven wrapper)
cd backend
./mvnw spring-boot:run              # dev server on :8080
./mvnw test                         # unit tests
./mvnw verify                       # tests + SonarQube
./mvnw test jacoco:report           # coverage (target/site/jacoco/index.html)

# Frontend (Angular 20, TypeScript 5.9)
cd frontend
npm install
npm start                           # ng serve on :4200
npm run test-unit                   # NOT "npm test" — see below
npm run test-e2e                    # Playwright (NOT "npm run e2e")

# Stack local (Postgres + Redis + backend + frontend)
docker compose up -d
# Swagger: http://localhost:8080/swagger-ui.html

# One-shot full env (backend + frontend + db)
node scripts/dev/start.js            # or: backend-only / frontend-only / with-db
```

## Repo layout

```
sale_partido/
├── backend/                        Spring Boot — DDD by epic
│   └── src/main/java/io/github/salepartido/api/
│       ├── domain/[epic]/{model,repository,service,controller,exception}/
│       │   └── controller/{dto,mapper,validator}/
│       ├── infrastructure/{config,error}/
│       ├── security/               JwtAuthenticationFilter
│       └── devtools/               DevController, SeedService (profile=dev)
├── frontend/                       Angular 20 standalone
│   └── src/app/
│       ├── core/Constantes.ts      ALL API endpoints live here
│       └── features/[epic]/{pages,components,services,models,routes.ts}
├── k8s/                            K8s manifests (containerd, not Docker)
├── scripts/{setup,git,github,dev,sql,changelog}/
├── agents/                         role-specific guides (backend, frontend, infra, qa)
├── documentation/                  decisions, workflow, architecture, qa
├── docker-compose.yml              local stack
├── config/sonar-project.properties Sonar config
└── .env / .env.example             ALL ports and secrets
```

## Things that will trip you up

### 1. Backend package and folders (stale docs)
Real package is `io.github.salepartido.api`, NOT `com.saleww` (which
`agents/backend.md` still says). Use `model/` for entities, NOT `entity/`.
The real folder structure is in the layout above — trust the tree, not the prose.

### 2. Frontend npm scripts are different from what the docs say
`package.json` exposes `test-unit` and `test-e2e`, not `test` and `e2e`.
README and root CLAUDE.md are wrong here. Use the real script names.

### 3. Security is currently open
`SecurityConfiguration.java` has `anyRequest().permitAll()`. The JWT filter
exists and is wired in, but the authorization chain is commented out. Don't
assume endpoints are protected; don't add code that depends on a real
authenticated principal.

### 4. Backend tests use H2, not Testcontainers
Despite what `agents/qa.md` says, tests run against H2 in-memory configured in
`backend/src/test/resources/application.properties`. `ApiApplicationTests`
mocks `StringRedisTemplate` with `@MockitoBean` so the context loads without
Redis. Two seed files exist: `data.sql` (Postgres, `ON CONFLICT`) and
`data-h2.sql` (H2, `MERGE`) — keep their syntaxes separate.

### 5. Git hook is NOT installed by default
The validator lives at `scripts/git/hooks/commit-msg`. Run
`./scripts/git/setup-hooks.sh` to install it. The hook is bash — on Windows,
install Git Bash or WSL, or commits silently bypass the check.

### 6. Commit message format is strict
`scripts/git/hooks/commit-msg` accepts only:
- `^(feat|fix|test|docs|refactor|perf)\(E[1-9]-H[0-9]{2}\): desc` for HUs
- `^(chore|ci)\(scope\): desc` for tech tasks

Anything else fails. The pre-push checklist in README/CLAUDE.md is enforced
by this hook.

### 7. Kubernetes uses containerd, not Docker daemon
Cluster runtime is containerd 2.2.3. `docker build` puts images in Docker's
registry, which K8s cannot see. Deploy workflow:
`docker save X | sudo ctr -n k8s.io images import X.tar`.
Skip this and the rollout keeps using the cached old image.
`agents/infra.md` and `.github/workflows/deploy.yml` document the dance.

### 8. Angular `BUILD_CONFIG` must match `angular.json`
`frontend/Dockerfile.prod` takes `BUILD_CONFIG=production|testing`. Only
`production`, `preprod`, `testing`, `development` are valid Angular build
configurations in `angular.json`. `pre_prod` (with underscore, used in
branch names) is NOT a valid build config — the pre-prod pipeline maps
`pre_prod` → `preprod` and passes `testing`.

### 9. `dev` profile exposes `/dev/seed-db`
Only enabled with `spring.profiles.active=dev`. Generates staging SQL via
`devtools/SeedService`. Don't enable this profile in shared/staging envs.

### 10. Canary deploy is disabled
`.github/workflows/deploy.yml` has the canary job commented out. Production
rollout is a direct replace, not canary. Don't add canary code or scripts
that expect the canary deployments to exist.

### 11. Coverage and bundle budgets are hard
- Backend: JaCoCo report at `target/site/jacoco/jacoco.xml`, fed to SonarQube.
  Coverage target: 80% (business logic).
- Frontend: bundle warning at 500kB / error at 1MB (`angular.json`).
- Tests under `backend/src/test/java/.../features/` are integration tests
  with `@SpringBootTest + @AutoConfigureMockMvc + @Transactional`.

## Git workflow (HU-driven)

Branches: `main` (prod) ← `pre_prod` (demos) ← `dev` (integration) ← yours.

```bash
./scripts/git/create-branch.sh E4-H08 crear-local           # feature/ (default)
./scripts/git/create-branch.sh E4-H08 fix-bug bugfix
./scripts/git/create-branch.sh E4-H08 urgente hotfix        # hotfix/ from main
```

The script pulls latest `dev`, creates the branch, and syncs GitHub Projects
(needs `GITHUB_TOKEN` in `.env` with `read:project, read:org` scopes).

Commit format examples:
```
feat(E4-H08): crear local con validaciones
fix(E5-H01): turnos superpuestos en misma cancha
chore(ci): bump maven cache version
```

## API contracts

`agents/API_CONTRACTS.md` is the single source of truth for endpoint shape
between backend and frontend. **Update it in the same commit that changes
an endpoint.** Frontend constants live in
`frontend/src/app/core/Constantes.ts` and read from `environment.apiUrl`.

## Deploy

- **Local**: `docker compose up -d` (4 services, health-checked Postgres).
- **Pre-prod**: push to `pre_prod` → K8s namespace `salepartido-preprod`,
  NodePort 32329 (backend) / 31168 (frontend), domain `preprod.sale-partido.com`.
- **Prod**: push to `main` → K8s namespace `salepartido`, NodePort 32328 /
  31167, public IP `138.36.96.63`.
- **Manual K8s**: `k8s/deploy.sh [--minikube]`.
- **Self-hosted runners** only — GitHub-hosted runners cannot reach the cluster.

## Where to look for X

| You need to… | Read |
|---|---|
| Add a backend feature | `agents/backend.md` + `agents/API_CONTRACTS.md` |
| Add a frontend feature | `agents/frontend.md` + `agents/API_CONTRACTS.md` |
| Touch K8s / deploy | `agents/infra.md` + `.github/workflows/deploy.yml` |
| Understand architecture decisions | `documentation/decisions/Decisiones.md` |
| Understand stack versions | `documentation/architecture/STACK.md` |
| Sync HUs from GitHub Projects | `node scripts/github/sync-github-projects.js` → `documentation/.local/data/HUS.json` |
| Test the backend | `documentation/workflow/TESTING.md` |
| Reset a stuck env | `node scripts/dev/start.js` / `docker compose down -v` |

## Environment quick ref

| Service | Local | Pre-prod | Prod |
|---|---|---|---|
| Backend port | 8080 | 32329 | 32328 |
| Frontend port | 4200 (ng) / 80 (compose) | 31168 | 31167 |
| Postgres | 5432 (compose) | internal | internal |
| Redis | 6379 (compose) | internal | internal |
| Public IP | localhost | 138.36.96.63 | 138.36.96.63 |
| K8s namespace | n/a | `salepartido-preprod` | `salepartido` |
