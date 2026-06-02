# Development

## Requirements

- Java 17
- Docker with Docker Compose
- Node.js and npm for the frontend

## Start Local Database

```bash
docker compose up -d
```

PostgreSQL listens on `localhost:5432` with the defaults in `.env.example`.
pgAdmin is available at `http://localhost:5050`.

## Run Backend

```bash
./mvnw spring-boot:run
```

The backend is configured in `src/main/resources/application.properties` and runs on port `8082`.

## Run Frontend

```bash
cd frontend/spring-oauth2-server-ui
npm install
npm run dev
```

The Vite dev server normally runs on `http://localhost:5173`.

## Verify

Backend:

```bash
./mvnw test
```

Frontend:

```bash
cd frontend/spring-oauth2-server-ui
npm run lint
npm run build
```

## Notes

- There is currently no `src/test` directory in the backend.
- The frontend OAuth client settings are currently hard-coded in `src/auth/authService.ts`.
- Flyway migrations are stored in `src/main/resources/db/migration`.

