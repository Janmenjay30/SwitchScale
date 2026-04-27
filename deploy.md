# Blinkit Full Deployment Guide

Last updated: 2026-04-27
Workspace root: `d:/E Drive/programming/Java Project/Blinkit`

## 1) Project Deployment Topology (Current)

This repository is a multi-service Java + React workspace.

### Backend services (Spring Boot)

- eureka-server (service discovery) - port 8761
- userservice - port 8001
- catalog - port 8002
- inventory - port 8003
- cart - port 8004
- order - port 8005
- payment - port 8006
- delivery - port 8007
- notification - port 8008

### Frontends

- switchscale-frontend (customer app, Vite)
- darkstore_frontend (manager app, Vite)

### Orchestration helper already present

- `start-blinkit-services.ps1` starts all backend services.
- It can also start both frontends using `-IncludeFrontends`.
- It loads variables from root `.env` if present.

## 2) Current Situation Audit (Per Service)

This section is based on current files in the workspace.

### eureka-server

- Path: `eureka-server/`
- Type: Spring Boot + Spring Cloud Netflix Eureka Server
- Port: 8761 (`eureka-server/src/main/resources/application.properties`)
- Registry mode: does not register/fetch itself (correct for Eureka server)
- Build status snapshot: `target/` exists (no packaged jar currently visible in `target/`)
- Deployment artifact support: no Dockerfile in module

### userservice

- Path: `userservice/`
- Type: Spring Boot service
- Port: 8001 (`userservice/src/main/resources/application.yml`)
- Database: PostgreSQL at `jdbc:postgresql://localhost:5432/userservice`
- Discovery: Eureka client enabled by default
- Config import: optional file `.env` + optional config server
- Build status snapshot: `target/` exists (no packaged jar currently visible)
- Deployment artifact support: has `userservice/Dockerfile` (multi-stage build)

### catalog

- Path: `catalog/`
- Type: Spring Boot service
- Port: 8002 (`catalog/src/main/resources/application.yml`)
- Database: MongoDB at `mongodb://localhost:27017/catalog`
- Discovery: Eureka client enabled by default
- Build status snapshot: `target/` exists (no packaged jar currently visible)
- Deployment artifact support: no Dockerfile in module

### inventory

- Path: `inventory/`
- Type: Spring Boot service
- Port: 8003 from `application.properties`
- Data stores: PostgreSQL + Redis
- Redisson dependency: present in `inventory/pom.xml`
- Discovery: Eureka client enabled by default
- Build status snapshot: `target/` exists (no packaged jar currently visible)
- Deployment artifact support:
  - Has `inventory/docker-compose.yml` for PostgreSQL and Redis
  - No Dockerfile for inventory app itself
- Important config note:
  - Both `inventory/src/main/resources/application.properties` and `inventory/src/main/resources/application.yml` exist with overlapping datasource values.
  - This can cause confusion during deployment because the files define different PostgreSQL endpoints/databases.

### cart

- Path: `cart/`
- Type: Spring Boot service
- Port: 8004 (`cart/src/main/resources/application.properties`)
- Data dependencies: Redis
- Messaging dependency: Kafka client settings present
- Service-to-service calls: OpenFeign enabled
- Discovery: Eureka client enabled by default
- Build status snapshot: `target/` exists (no packaged jar currently visible)
- Deployment artifact support: no Dockerfile in module

### order

- Path: `order/`
- Type: Spring Boot service
- Port: 8005 (`order/src/main/resources/application.properties`)
- Default DB: file-based H2 (`./data/orderdb`) with PostgreSQL compatibility mode
- Optional messaging: Kafka properties present; event publishing toggle defaults to disabled
- Calls to other services via URLs:
  - cart: `http://localhost:8004`
  - payment: `http://localhost:8006`
  - delivery: `http://localhost:8007`
  - notification: `http://localhost:8008`
- Discovery: Eureka client enabled by default
- Build status snapshot: `target/` exists (no packaged jar currently visible)
- Deployment artifact support: no Dockerfile in module

### payment

- Path: `payment/`
- Type: Spring Boot service
- Port: 8006 (`payment/src/main/resources/application.properties`)
- Default DB: file-based H2 (`./data/paymentdb`) with PostgreSQL compatibility mode
- Discovery: Eureka client enabled by default
- Build status snapshot: `target/` exists (no packaged jar currently visible)
- Deployment artifact support: no Dockerfile in module

### delivery

- Path: `delivery/`
- Type: Spring Boot service
- Port: 8007 (`delivery/src/main/resources/application.properties`)
- Default DB: file-based H2 (`./data/deliverydb`) with PostgreSQL compatibility mode
- Discovery: Eureka client enabled by default
- Build status snapshot: `target/` exists (no packaged jar currently visible)
- Deployment artifact support: no Dockerfile in module

### notification

- Path: `notification/`
- Type: Spring Boot service
- Port: 8008 (`notification/src/main/resources/application.properties`)
- External dependency: SMTP settings for Gmail mail sender
- Discovery: Eureka client enabled by default
- Build status snapshot: `target/` exists (no packaged jar currently visible)
- Deployment artifact support: no Dockerfile in module

### fastdelivery (special case)

- Path: `fastdelivery/`
- Current state: only `target/` folder is present in current workspace snapshot
- No `pom.xml` / source files detected in the visible structure
- Not included in `start-blinkit-services.ps1`
- Recommendation: treat as inactive/out-of-scope until source module is restored

### switchscale-frontend

- Path: `switchscale-frontend/`
- Type: React + Vite
- Scripts: `dev`, `build`, `preview`, `lint`
- Dev proxy mappings:
  - `/users` -> `http://localhost:8001`
  - `/products` -> `http://localhost:8002`
  - `/categories` -> `http://localhost:8002`
  - `/cart` -> `http://localhost:8004`
  - `/orders` -> `http://localhost:8005`
- Build state snapshot: `node_modules/` and `dist/` are present

### darkstore_frontend

- Path: `darkstore_frontend/`
- Type: React + Vite
- Scripts: `dev`, `build`, `preview`
- Dev server port: 5180
- Dev proxy mapping:
  - `/catalog-api` -> `http://localhost:8002`
- Build state snapshot: `node_modules/` and `dist/` are present

## 3) Step-by-Step: Deploy the Whole Project (Local/VM)

This is the most practical full deployment path for current repo state.

### Step 1. Install prerequisites

On deployment machine (Windows/Linux/macOS):

- Java 25
- Maven 3.9+
- Node.js 20+
- npm
- Docker Desktop (or Docker Engine)

Verify:

```powershell
java -version
mvn -version
node -v
npm -v
docker --version
```

### Step 2. Prepare infrastructure services

Current backend configuration needs:

- Eureka (in-project service)
- PostgreSQL (userservice + inventory)
- Redis (inventory + cart)
- MongoDB (catalog)
- Optional: Kafka (if you enable/use Kafka flows deeply)

#### 2A. Start PostgreSQL + Redis quickly using existing compose file

```powershell
cd "d:/E Drive/programming/Java Project/Blinkit/inventory"
docker compose up -d
```

This starts:

- PostgreSQL on host port 15432 (from compose)
- Redis on host port 6379

#### 2B. Start MongoDB

```powershell
docker run -d --name blinkit-mongo -p 27017:27017 mongo:7
```

#### 2C. Optional Kafka (if needed for end-to-end async flows)

You can add a Kafka+Zookeeper compose stack later. For initial bring-up, keep order event publishing disabled (already default false in order service).

### Step 3. Align environment and config

Create root `.env` file if you need custom values.

Suggested `.env` keys:

```env
EUREKA_ENABLED=true
EUREKA_DEFAULT_ZONE=http://localhost:8761/eureka/
SPRING_CLOUD_CONFIG_ENABLED=false
CONFIG_SERVER_URL=http://localhost:8888

# DB password used by services that read DB_PASSWORD
DB_PASSWORD=abc123

# Optional mail for notification service
MAIL_USERNAME=
MAIL_PASSWORD=
```

Important for current inventory setup:

- `inventory/application.properties` points to `localhost:5432/inventory`.
- `inventory/docker-compose.yml` exposes PostgreSQL as host `15432` and DB `switchscale_db`.

Before production/local deployment, make these values consistent (recommended) to avoid runtime mismatch.

### Step 4. Build backend services

From each service module:

```powershell
cd "d:/E Drive/programming/Java Project/Blinkit/eureka-server"; mvn clean package -DskipTests
cd "d:/E Drive/programming/Java Project/Blinkit/userservice"; mvn clean package -DskipTests
cd "d:/E Drive/programming/Java Project/Blinkit/catalog"; mvn clean package -DskipTests
cd "d:/E Drive/programming/Java Project/Blinkit/inventory"; mvn clean package -DskipTests
cd "d:/E Drive/programming/Java Project/Blinkit/cart"; mvn clean package -DskipTests
cd "d:/E Drive/programming/Java Project/Blinkit/order"; mvn clean package -DskipTests
cd "d:/E Drive/programming/Java Project/Blinkit/payment"; mvn clean package -DskipTests
cd "d:/E Drive/programming/Java Project/Blinkit/delivery"; mvn clean package -DskipTests
cd "d:/E Drive/programming/Java Project/Blinkit/notification"; mvn clean package -DskipTests
```

### Step 5. Start all backend services

From repo root, easiest method:

```powershell
cd "d:/E Drive/programming/Java Project/Blinkit"
powershell -ExecutionPolicy Bypass -File .\start-blinkit-services.ps1
```

Or with frontends too:

```powershell
powershell -ExecutionPolicy Bypass -File .\start-blinkit-services.ps1 -IncludeFrontends
```

Recommended startup order if starting manually:

1. eureka-server
2. userservice
3. catalog
4. inventory
5. cart
6. payment
7. delivery
8. notification
9. order

### Step 6. Start both frontends (if not using script flag)

```powershell
cd "d:/E Drive/programming/Java Project/Blinkit/switchscale-frontend"
npm install
npm run dev
```

```powershell
cd "d:/E Drive/programming/Java Project/Blinkit/darkstore_frontend"
npm install
npm run dev
```

### Step 7. Validate deployment

Check service health endpoints:

- `http://localhost:8761` (Eureka UI)
- `http://localhost:8001/actuator/health`
- `http://localhost:8002/actuator/health`
- `http://localhost:8003/actuator/health`
- `http://localhost:8004/actuator/health`
- `http://localhost:8005/actuator/health`
- `http://localhost:8006/actuator/health`
- `http://localhost:8007/actuator/health`
- `http://localhost:8008/actuator/health`

Frontend URLs in dev mode:

- switchscale-frontend: typically `http://localhost:5173`
- darkstore_frontend: `http://localhost:5180`

## 4) Production Packaging Path (Current Reality)

Current containerization coverage is partial:

- userservice has Dockerfile
- inventory has compose only for DB/cache
- other backend services do not yet have Dockerfiles

So today, the realistic production path is:

1. Package all Java services to jars (`mvn clean package -DskipTests`)
2. Run jars under a process manager (systemd, NSSM, PM2-for-java wrappers, or Kubernetes sidecar approach)
3. Build frontend static assets (`npm run build`) and serve via Nginx/Apache/CDN
4. Externalize all env vars/secrets (DB, mail, eureka, config)

## 5) Key Risks to Resolve Before Cloud/Prod Deployment

1. Inventory config mismatch between `application.properties` and `application.yml` (DB host/port/database values differ).
2. Incomplete Docker coverage for most backend services.
3. No top-level infra-as-code or unified compose for all dependencies.
4. `fastdelivery` module appears incomplete/inactive in current workspace snapshot.
5. Kafka is partially configured in services but not provisioned in repo-level deployment scripts.

## 6) Recommended Immediate Hardening Tasks

1. Standardize all service configs to one format (`application.yml` or `application.properties`) per service.
2. Add Dockerfiles for all backend services.
3. Add one root `docker-compose.yml` for infra + all services + frontends.
4. Add one CI pipeline to build/test/package all modules.
5. Add centralized environment templates (`.env.example`) with per-env variants (dev/stage/prod).

---

If you want, next I can create:

- a root `docker-compose.yml` that brings up all infra + all services + both frontends, and
- a `.env.example` with all required variables for one-command deployment.
