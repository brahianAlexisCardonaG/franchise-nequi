# Franchise API

Reactive REST API to manage a network of franchises, their branches and the products each branch sells.
Built end to end without blocking calls: Spring WebFlux functional endpoints, R2DBC with PostgreSQL and
Resilience4j, following a hexagonal (ports and adapters) architecture.

- **Franchise**: name and a list of branches
- **Branch**: name and a list of products
- **Product**: name and stock

## Table of contents

1. [Tech stack](#tech-stack)
2. [Architecture](#architecture)
3. [Endpoints](#endpoints)
4. [Run locally](#run-locally)
5. [Configuration](#configuration)
6. [Tests and coverage](#tests-and-coverage)
7. [Error handling](#error-handling)
8. [Resilience](#resilience)
9. [Deployment on AWS](#deployment-on-aws)

## Tech stack

| Concern | Choice |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5, Spring WebFlux (RouterFunction + handlers) |
| Persistence | Spring Data R2DBC + PostgreSQL |
| Resilience | Resilience4j circuit breaker, Reactor timeout, retry with backoff |
| Validation | Jakarta Bean Validation |
| Mapping | MapStruct, Lombok |
| API docs | springdoc-openapi (OpenAPI 3 + Swagger UI) |
| Tests | JUnit 5, Mockito, Reactor StepVerifier, WebTestClient, JaCoCo |
| Containers | Docker multi-stage build, Docker Compose |

## Architecture

The code is split in three layers. Dependencies always point to the domain, and the domain does not import
Spring or any other framework.

```mermaid
flowchart LR
    client([HTTP client]) --> router

    subgraph entrypoints [infrastructure / entrypoints]
        router[RouterFunction] --> handler[Handlers]
        handler --> validator[RequestValidator]
        handler --> errors[ApplyErrorHandler]
    end

    subgraph domain [domain]
        api[[Input ports: one per use case]] --> usecase[Use cases]
        usecase --> spi[[Output ports]]
    end

    subgraph adapters [infrastructure / adapters]
        adapter[Persistence adapters] --> resilience[PersistenceResilience]
        resilience --> repository[R2DBC repositories]
    end

    handler --> api
    spi -.implemented by.-> adapter
    repository --> db[(PostgreSQL)]
```

```
src/main/java/com/franchise/project
├── application/config        Spring wiring: one @Bean per use case and adapter
├── domain                    Framework-free business core
│   ├── <aggregate>/api       Input ports (one interface per use case)
│   ├── <aggregate>/spi       Output ports (persistence contracts)
│   ├── <aggregate>/usecase   Business rules
│   ├── <aggregate>/model     Entities
│   └── exception, enums, util
└── infrastructure
    ├── entrypoints           Routers, handlers, request/response records, error mapping
    └── adapters              R2DBC entities, repositories, adapters and resilience
```

Business rules:

- Franchise names are unique. Branch names are unique within their franchise and product names are unique
  within their branch (also enforced by database constraints).
- Stock must be greater than or equal to zero.
- Deleting a product removes it permanently.

## Endpoints

Base URL when running locally: `http://localhost:8085`

| # | Operation | Method | Path |
|---|---|---|---|
| 1 | Create a franchise | `POST` | `/api/v1/franchise` |
| 2 | Add a branch to a franchise | `POST` | `/api/v1/branch` |
| 3 | Add a product to a branch | `POST` | `/api/v1/product` |
| 4 | Delete a product from its branch | `DELETE` | `/api/v1/product/{productId}` |
| 5 | Update the stock of a product | `PUT` | `/api/v1/product/stock` |
| 6 | Largest stock product of each branch of a franchise | `GET` | `/api/v1/franchise/{franchiseId}/top-stock-products` |
| 7 | Update the name of a franchise | `PUT` | `/api/v1/franchise/name` |
| 8 | Update the name of a branch | `PUT` | `/api/v1/branch/name` |
| 9 | Update the name of a product | `PUT` | `/api/v1/product/name` |

Interactive documentation with request/response examples and every error code:

- Swagger UI: `http://localhost:8085/webjars/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8085/v3/api-docs`

Example flow:

```bash
curl -X POST localhost:8085/api/v1/franchise -H "Content-Type: application/json" -d '{"name":"Coffee House"}'
curl -X POST localhost:8085/api/v1/branch -H "Content-Type: application/json" -d '{"name":"Downtown","franchiseId":1}'
curl -X POST localhost:8085/api/v1/product -H "Content-Type: application/json" -d '{"name":"Espresso","stock":25,"branchId":1}'
curl -X PUT localhost:8085/api/v1/product/stock -H "Content-Type: application/json" -d '{"id":1,"stock":40}'
curl localhost:8085/api/v1/franchise/1/top-stock-products
curl -X DELETE localhost:8085/api/v1/product/1
```

## Run locally

### Prerequisites

| Tool | Version | Needed for |
|---|---|---|
| Docker + Docker Compose | Docker 24+ | Running the database and the API |
| Java (JDK) | 17 | Running the API or the tests outside Docker |
| AWS CLI and Terraform | AWS CLI 2, Terraform 1.6+ | Only for the AWS deployment |

Gradle does not need to be installed: the project ships with the Gradle wrapper.

### Option A: everything with Docker Compose

```bash
cp .env.example .env            # then set a password in .env
docker compose up --build
```

Compose starts PostgreSQL, waits for it to be healthy, builds the API image and starts it. The schema in
`src/main/resources/schema.sql` is applied on startup (`SPRING_SQL_INIT_MODE=always`).

Check that it is up:

```bash
curl localhost:8085/actuator/health      # {"status":"UP"}
```

Stop it with `docker compose down` (add `-v` to also delete the database volume).

### Option B: database in Docker, API from the IDE or Gradle

```bash
cp .env.example .env
docker compose up -d postgres
export $(grep -v '^#' .env | xargs)      # Windows PowerShell: set the SPRING_R2DBC_* variables manually
SPRING_SQL_INIT_MODE=always ./gradlew bootRun
```

## Configuration

All settings come from environment variables. Defaults are only meant for local development.

| Variable | Default | Description |
|---|---|---|
| `SPRING_R2DBC_URL` | `r2dbc:postgresql://localhost:5432/franchise` | Database URL (add `?sslmode=require` for managed databases) |
| `SPRING_R2DBC_USERNAME` | `franchise` | Database user |
| `SPRING_R2DBC_PASSWORD` | `change-me` | Database password |
| `SPRING_SQL_INIT_MODE` | `never` | `always` applies `schema.sql` on startup |
| `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD` | see `.env.example` | Used by Docker Compose for the database container |

Resilience thresholds live in `application.yml` under `resilience4j` and `persistence.resilience`.
`.env` and `*.tfvars` are ignored by Git; only the `*.example` files are versioned.

## Tests and coverage

```bash
./gradlew test                    # run the test suite
./gradlew check                   # tests + JaCoCo report + 70% line coverage verification
```

The coverage report is generated at `build/reports/jacoco/test/html/index.html`.
Lombok-generated code and MapStruct implementations are excluded from the coverage figures.

What is covered:

- **Use cases**: one test class per use case with the real `ValidationCondition`, covering the happy path and
  business errors (not found, duplicated name, negative stock) with `StepVerifier`.
- **Handlers**: real router, validator, mappers and error handler through `WebTestClient`, asserting
  200/201/400/404/409/500/503.
- **Resilience**: retry with backoff, no retry for writes, timeout and circuit opening using virtual time.
- **Application**: context startup, OpenAPI contract of the nine endpoints and Actuator exposure.

## Error handling

Errors are resolved inside the reactive chain with `onErrorResume`, one branch per exception type, and always
return the same body shape:

```json
{"code":"404","message":"The franchise does not exist","date":"2026-01-01T12:00:00Z",
 "errors":[{"code":"404","message":"The franchise does not exist"}]}
```

| Status | When |
|---|---|
| 400 | Missing or blank fields, non numeric identifiers, negative stock, malformed JSON |
| 404 | The franchise, branch or product does not exist |
| 409 | Duplicated name in the same scope |
| 500 | Unexpected error (no internal details are returned) |
| 503 | Database timeout or circuit breaker open |

## Resilience

Every persistence call goes through `PersistenceResilience`:

1. **Timeout** (`2s`) on each attempt.
2. **Circuit breaker** (Resilience4j): opens when 50% of the last 20 calls fail (minimum 10), stays open
   10 seconds and allows 3 trial calls in half-open state.
3. **Retry with exponential backoff** (2 retries starting at 200 ms) only for reads, because they are
   idempotent, and only for transient errors or timeouts.
4. **Backpressure**: read ports return `Flux` straight from R2DBC and `limitRate` bounds the demand sent to
   the driver, so results are never fully loaded in memory.

## Deployment on AWS

The infrastructure is defined with Terraform in the `terraform/` folder. Copy
`terraform/terraform.tfvars.example` to `terraform/terraform.tfvars` and fill in your values before running
any Terraform command.

The Terraform code is being refactored into reusable modules (networking, ECR, ECS Fargate, ALB, Secrets
Manager, IAM and auto scaling) with remote state and separate environments. The step-by-step deployment guide
will be added together with that refactor.

**Deployed URL:** pending.
