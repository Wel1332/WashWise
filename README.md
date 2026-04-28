# WashWise

A laundry service management system with three clients sharing one Spring Boot backend:

| Module | Stack | Purpose |
| --- | --- | --- |
| [`washwise-backend`](./washwise-backend) | Spring Boot 3.5 · Java 17 · PostgreSQL · JWT | REST API, persistence, auth, role-based authorization |
| [`washwise-frontend`](./washwise-frontend) | React 19 · TypeScript · Vite · Tailwind · Zustand | Web app for customers, staff, and admins |
| [`washwise-mobile`](./washwise-mobile) | Android · Kotlin · MVP · Retrofit | Native Android app for customers, staff, and admins |

## Features

- **Three roles** — `CUSTOMER`, `STAFF`, `ADMIN` with role-based routing on web, native shells on mobile, and `@PreAuthorize` guards on the API.
- **Order pipeline** — `PENDING → RECEIVED → WASHING → DRYING → READY → COMPLETED` with a six-step tracking timeline.
- **Service catalog** — Wash Only, Wash-Dry-Fold, Dry Cleaning, Premium Care; admins manage the catalog from web or mobile.
- **Auth** — JWT access tokens with refresh tokens, BCrypt password hashing.
- **Profiles** — update info, change password, upload profile image (BLOB-backed).

## Architecture

Both the backend and the mobile app are organized as **vertical slices** by feature (`auth`, `order`, `service`, `user`, `profile`, `admin`, `staff`, `dashboard`), each with its own data, presentation, and UI layers. The mobile app applies an **MVP** pattern with `Contract` interfaces per screen — see [washwise-mobile/ARCHITECTURE.md](./washwise-mobile/ARCHITECTURE.md).

```
washwise-backend/   → API + persistence + JWT + role guards
washwise-frontend/  → React SPA, calls /api/v1
washwise-mobile/    → Android app, calls /api/v1
```

## Getting started

### Backend

```bash
cd washwise-backend
./mvnw spring-boot:run
```

The API serves at `http://localhost:8080/api/v1`. Configuration lives in `src/main/resources/application.properties`. Swagger UI is mounted at `/swagger-ui.html`.

### Frontend

```bash
cd washwise-frontend
npm install
npm run dev
```

Defaults to `http://localhost:5173` and talks to the backend at `http://localhost:8080/api/v1`. Override with a `.env` file:

```
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

### Mobile

Open `washwise-mobile/` in Android Studio. The base URL is set in the network module — point it at your backend (`http://10.0.2.2:8080/api/v1/` for the emulator).

## API surface

| Area | Endpoints |
| --- | --- |
| Auth | `POST /auth/register`, `POST /auth/login`, `POST /auth/refresh` |
| Profile | `GET/PUT /profile`, `PUT /profile/change-password`, `POST /profile/upload-image`, `DELETE /profile/account` |
| Services | `GET /services`, `GET /services/active`, `POST/PUT/DELETE /services` (ADMIN) |
| Orders | `POST /orders`, `GET /orders/my-orders`, `GET /orders` (ADMIN/STAFF), `PUT /orders/{id}` (ADMIN/STAFF), `DELETE /orders/{id}` |
| Users | `GET /users`, `PUT /users/{id}/role` (ADMIN) |

All responses are wrapped in `ApiResponse<T>` with `success`, `data`, `message`, `statusCode`, `timestamp`.

## Tech notes

- **Database** — PostgreSQL (Supabase pooler in dev). JPA `ddl-auto=update`.
- **Security** — `@EnableMethodSecurity` is on, so `@PreAuthorize` is enforced at the controller layer in addition to path-based rules.
- **Caching** — active service queries are cached with separate keys for the list and the paginated views.
- **Errors** — typed exceptions (`ResourceNotFoundException`, `DuplicateResourceException`, `InvalidCredentialsException`, `AccessDeniedException`, `IllegalArgumentException`) flow through a single `GlobalExceptionHandler` that returns consistent JSON.
