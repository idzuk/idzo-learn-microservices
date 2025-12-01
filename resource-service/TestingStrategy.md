# Testing Strategy & Stability Plan

> Short, opinionated strategy describing how we’ll ensure **application stability** and **sufficient test coverage** across layers: Unit, Integration, Component, Contract, and End‑to‑End (E2E).

---

## TL;DR (Strategy Snapshot)

- **Test Pyramid**
  - **Unit** (broadest): fast, isolated logic — **~60–70%** of all tests
  - **Integration**: persistence, messaging, AWS SDK, serialization — **~15–20%**
  - **Component (slice/system-in-a-box)**: Spring Boot + real infra via Testcontainers, mocks for cross‑service calls — **~10–15%**
  - **Contract**: service-to-service API reliability — **~5–10%**
  - **E2E/Smoke**: full environment validation — **selective, critical paths only**
- **Goal**: high confidence with fast feedback, focusing on correctness of domain logic and stability of critical flows.

---

## What’s Implemented Today

We already have **component-style** Spring Boot tests using **MockMvc** + **Testcontainers**:

- **Real infrastructure**: PostgreSQL and S3 (via LocalStack) are started by Testcontainers.
- **Spring Boot context** with `@SpringBootTest` and real HTTP layer (random port) + `@AutoConfigureMockMvc`.
- **External collaborators mocked**: `ResourceProcessor` and `TransactionUtil` are `@MockBean`’ed; we verify side effects (e.g., `processResource` is invoked).
- **Scenarios covered**:
  - Upload resource -> **200 OK**, entity saved, S3 location set, processor triggered
  - Get resource by id -> **200 OK** and **binary bytes** are returned with `audio/mpeg` content type
- **Infrastructure setup**:
  - Uses `@Container` **PostgreSQL** and **LocalStack (S3)** with bucket created in `@BeforeAll`
  - App properties are injected via `@DynamicPropertySource` (DB URL/creds, S3 endpoint/creds/bucket)
---

## Proposed Test Mix & Rationale

### 1) Unit Tests (primary investment — 60–70%)
**What**: pure Java logic (domain, validators, mappers, utilities, configuration).  
**How**: JUnit 5 + AssertJ/Mockito.  
**Why**: fastest feedback, cheapest to maintain; prevent regressions at the source.

**Examples**:
- Mapping DTOs ↔ entities
- Validation & parsing utilities
- Business rules (e.g., size/type checks for uploaded resources)

### 2) Integration Tests (15–20%)
**What**: focus on integration boundaries — JPA repositories, JSON (Jackson) serialization, AWS SDK clients, message producers/consumers (without full app boot).  
**How**: `@DataJpaTest`, Spring context slices, **Testcontainers** for PostgreSQL/LocalStack/RabbitMQ.  
**Why**: verify configuration & persistence/messaging mapping is correct.

**Examples**:
- Repository CRUD & query methods against real Postgres
- S3 client: put/get object contract against LocalStack S3
- RabbitMQ template publish/consume (if applicable)

### 3) Component Tests (10–15%)
**What**: boot the whole service “in a box” with real DB & S3 through Testcontainers; **mock cross‑service calls**.  
**How**: `@SpringBootTest` + MockMvc + Testcontainers + `@MockBean` for external service clients or processors.  
**Why**: validate critical HTTP flows, persistence, and storage together; fast enough for CI, very representative for the service.

**Examples** (already present and to expand):
- Upload/Download resource happy-path and error cases
- Content-type enforcement, validation failures
- Processor invocation & transaction boundary behavior

### 4) Contract Tests (5–10%)
**What**: **Provider** (this service) and **Consumer** (other microservices) API contracts.  
**How**: Spring Cloud Contract. Use **generated stubs** in consumer tests, and **provider verification** in this service’s CI.  
**Why**: prevent breaking changes between services without heavy E2E. Faster and more reliable than full environment tests.

**Scope**:
- HTTP APIs exposed by this service (resource upload/download)
- Any synchronous dependencies we call (if/when HTTP clients are introduced)

### 5) End‑to‑End (selective)
**What**: critical user journeys across multiple services & infra.  
**How**: Using Postman.  
**Why**: final “it really works together” check Happy Path;

---

## Coverage Targets (guidance, not dogma)

| Layer            | Target                    |
|------------------|---------------------------|
| Unit             | 80–90% for core logic     |
| Integration      | Essential boundaries only |
| Component        | Critical flows & errors   |
| Contract         | 100% of public endpoints  |
| E2E/Smoke        | 3–5 most critical paths   |

> We do **not** chase 100% line coverage globally. We focus on **risk-based coverage** and **critical behavior**.

---

## Tooling & Conventions

- **JUnit 5**, **Mockito**, **AssertJ** (or Hamcrest), **MockMvc**
- **Testcontainers**: PostgreSQL, LocalStack (S3), RabbitMQ
- **Spring Boot Test slices** for fast integration
- **Pact** (or Spring Cloud Contract) for contract tests
- **RestAssured/Karate/Postman** for smoke/E2E
- **Naming**: `*Test` (unit/integration), `*IT` (if you prefer Maven Failsafe split), `*ComponentTest` for service-in-a-box
- **Packages** mirror main code; test data builders & factories live under `testutil`
