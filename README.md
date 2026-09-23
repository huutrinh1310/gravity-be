# Gravity API

Spring Boot portfolio API structured as a pragmatic Hexagonal Architecture / DDD modular monolith.

## Prerequisites

- Java 25
- PostgreSQL 18+ available locally or over the network
- Docker Desktop (optional)

## Configure the database

The repository includes a local `.env` file with placeholder values. Replace them with real PostgreSQL credentials; do not commit `.env`. To reset it, copy the safe template:

```powershell
Copy-Item .env.example .env
```

For a local run, set the variables in your PowerShell session (Spring Boot does not automatically load `.env`):

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/gravity"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "your-password"
$env:CORS_ALLOWED_ORIGINS = "http://localhost:3000,http://localhost:5173"
```

The API allows requests from the frontend origins listed in `CORS_ALLOWED_ORIGINS`, which is important when the FE calls the API with `fetch()` from a different domain/port.

Create the `gravity` database if it does not exist. Hibernate creates or updates the tables on startup in the current development configuration.

## Run locally

```powershell
.\gradlew.bat test
.\gradlew.bat bootRun
```

The API runs at `http://localhost:8080`. Swagger UI is available at `http://localhost:8080/swagger-ui.html` and OpenAPI JSON at `http://localhost:8080/api-docs`.

## Run with Docker

Set `DB_URL` in `.env` to a PostgreSQL host reachable from the container. With PostgreSQL running on the Docker Desktop host, use:

```dotenv
DB_URL=jdbc:postgresql://host.docker.internal:5432/gravity
DB_USERNAME=postgres
DB_PASSWORD=your-password
```

Then build and run:

```powershell
docker build -t gravity-api .
docker run --rm --env-file .env -p 8080:8080 gravity-api
```

For environment-specific Docker runs, the repository includes `.test.env` and `.production.env` plus a Compose file:

```powershell
docker compose --profile test up --build
docker compose --profile production up --build
```

The test profile starts the API on `http://localhost:8081` together with a local PostgreSQL test database on port `5433`.

## Architecture

- `adapter/in/web/profile`: HTTP controllers and API request/response DTOs.
- `application/port/in/profile`: explicit profile, skill, and project use cases plus commands.
- `application/service`: use-case implementation and transaction boundary.
- `domain`: dependency-free aggregate and domain rules.
- `adapter/out/persistence/profile`: JPA adapter implementing the outbound repository port.

See [docs/architecture.md](docs/architecture.md) for the boundary overview.
