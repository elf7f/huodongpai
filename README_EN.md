# Huodongpai

[English](README_EN.md) | [中文](README.md)

Huodongpai is an event registration and check-in management system for lectures, training sessions, competitions, salons, campus activities, and small conferences.  
It uses a `Spring Boot` monolithic backend and a `Vue 3` frontend, covering the full business flow of event publishing, registration, approval, on-site check-in, and statistics.

## Features

- Create, edit, publish, cancel, and delete events
- Online registration with capacity control
- Registration cancellation and approval workflow
- On-site attendee check-in
- Dashboard, hot events, and signup trend statistics
- `JWT + Redis` authentication
- `MySQL` persistence
- Ready-to-import Postman collection

## Tech Stack

**Backend**
- `Spring Boot 3`
- `MyBatis-Plus`
- `MySQL 8`
- `Redis 7`
- `JWT`

**Frontend**
- `Vue 3`
- `Vite`
- `Element Plus`
- `Axios`
- `Vue Router`
- `Pinia`

## Project Structure

```text
huodongpai
├── src                         # Spring Boot backend
├── frontend                    # Vue 3 frontend
├── sql                         # schema and seed scripts
├── docs                        # supplemental docs
├── postman                     # Postman collection and environment
└── docker-compose.yml          # local MySQL / Redis infrastructure
```

## Modules

**User Side**
- Login
- Event list / detail
- Apply for an event
- My registrations
- Cancel registration

**Admin Side**
- Dashboard
- User management
- Event category management
- Event management
- Registration approval
- Check-in management
- Statistics

## Status Model

**Event Base Status**
- `draft`
- `published`
- `cancelled`

**Event Runtime Status**
- `signup_open`
- `signup_closed`
- `ongoing`
- `finished`

**Signup Status**
- `pending`
- `approved`
- `rejected`
- `cancelled`

## Default Test Accounts

- Admin: `admin / 123456`
- User: `test01 / 123456`

Seed data lives in `sql/02_huodongpai_seed.sql`, with extra notes in `docs/init-data.md`.

## Local Startup

### 1. Prerequisites

- `JDK 17`
- `Maven 3.9+`
- `Node.js 20+`
- `Docker / Docker Compose` for `MySQL` and `Redis` only

### 2. Copy environment variables

```bash
cp .env.example .env
```

Common variables:
- `DB_HOST` / `DB_PORT` / `DB_NAME` / `DB_USERNAME` / `DB_PASSWORD`
- `REDIS_HOST` / `REDIS_PORT` / `REDIS_DATABASE` / `REDIS_PASSWORD`
- `JWT_SECRET`
- `SERVER_PORT`

### 3. Start MySQL and Redis

```bash
docker compose up -d
```

Default ports:
- `MySQL: 127.0.0.1:3306`
- `Redis: 127.0.0.1:6379`

On the first startup, MySQL loads:
- `sql/01_huodongpai_schema.sql`
- `sql/02_huodongpai_seed.sql`

To reset old volumes and reinitialize:

```bash
docker compose down -v
docker compose up -d
```

### 4. Start backend

```bash
mvn spring-boot:run
```

Backend URL: `http://localhost:8080`

### 5. Start frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend URL: `http://localhost:5173`

## API Testing

Provided Postman files:
- Collection: `postman/huodongpai.postman_collection.json`
- Environment: `postman/huodongpai.local.postman_environment.json`

See `docs/postman-usage.md` for import and variable notes.

## Documentation

- Database design: `docs/schema-design.md`
- Seed data: `docs/init-data.md`
- Postman guide: `docs/postman-usage.md`
- GitHub repository metadata suggestions: `docs/github-repo-metadata.md`

## Current Scope

**Completed**
- Core backend business APIs
- Admin and user frontend pages
- `Docker Compose` based local infrastructure
- Postman collection

**Not included yet**
- QR code check-in
- SMS / email notifications
- Payment
- Real-time WebSocket notifications
- Multi-tenant or distributed architecture

## Suggested Demo Flow

1. Login as admin
2. Create event categories
3. Create and publish an event
4. Login as a normal user and register
5. Review the registration as admin
6. Perform attendee check-in
7. Open the statistics pages
