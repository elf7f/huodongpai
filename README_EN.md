# Huodongpai

[English](README_EN.md) | [中文](README.md)

Huodongpai is an event registration and check-in management system for lectures, training sessions, competitions, salons, campus activities, and small conferences.  
The project uses a **Spring Boot monolithic backend + Vue 3 frontend** and covers the full business flow of event publishing, registration, approval, on-site check-in, and statistics.

## Features

- Create, edit, publish, cancel, and delete events
- Online registration with capacity control
- Registration approval workflow
- On-site attendee check-in
- Dashboard, hot events, and signup trend statistics
- JWT + Redis based authentication
- MySQL persistence
- Ready-to-import Postman collection
- Docker Compose for local MySQL and Redis only

## Tech Stack

### Backend

- Spring Boot 3
- MyBatis-Plus
- MySQL 8
- Redis 7
- JWT
- Lombok

### Frontend

- Vue 3
- Vite
- Element Plus
- Axios
- Vue Router
- Pinia

## Project Structure

```text
huodongpai
├── src                         # Spring Boot backend
├── frontend                    # Vue 3 frontend
├── sql                         # schema and seed scripts
├── docs                        # design and usage docs
├── postman                     # Postman collection and environment
└── docker-compose.yml          # local MySQL/Redis infrastructure
```

## Modules

### User Side

- Login
- Event list
- Event detail
- Apply for an event
- My registrations
- Cancel registration

### Admin Side

- Dashboard
- User management
- Event category management
- Event management
- Registration approval
- Check-in management
- Statistics

## Core Status Model

### Event Base Status

- `draft`: draft
- `published`: published
- `cancelled`: cancelled

### Event Runtime Status

- `signup_open`: open for signup
- `signup_closed`: signup closed
- `ongoing`: ongoing
- `finished`: finished

### Signup Status

- `pending`: pending approval
- `approved`: approved
- `rejected`: rejected
- `cancelled`: cancelled

## Default Test Accounts

- Admin: `admin / 123456`
- User: `test01 / 123456`

See `sql/02_huodongpai_seed.sql` for seed data.

## Local Startup

### 0. Environment Variables

Copy the template:

```bash
cp .env.example .env
```

Notes:

- `docker compose` reads the root `.env` automatically
- the backend can reuse the same `.env` when started locally

### 1. Prerequisites

- JDK 17
- Maven 3.9+
- Node.js 20+
- Docker / Docker Compose for MySQL and Redis only

### 2. Start MySQL and Redis

```bash
cp .env.example .env
docker compose up -d
```

Default ports:

- MySQL: `127.0.0.1:3306`
- Redis: `127.0.0.1:6379`

### 3. Initialize the Database

On the first `docker compose up -d`, MySQL automatically loads the scripts under `sql/`:

- `sql/01_huodongpai_schema.sql`
- `sql/02_huodongpai_seed.sql`

Both scripts explicitly use `utf8mb4` client encoding to avoid garbled Chinese seed data.

If you already started MySQL before and kept the volume, reset the volume and initialize again:

```bash
docker compose down -v
docker compose up -d
```

If the old database already contains garbled values such as `ç³»ç»Ÿç®¡ç†å‘˜`, run:

```bash
mysql -uroot -p123456 huodongpai < sql/03_huodongpai_fix_seed_utf8.sql
```

### 4. Start the Backend

```bash
mvn spring-boot:run
```

Backend URL:

- `http://localhost:8080`

### 5. Start the Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend URL:

- `http://localhost:5173`

## Docker Compose Scope

Docker Compose is used only for:

- MySQL
- Redis

### Start

```bash
cp .env.example .env
docker compose up -d
```

### Stop

```bash
docker compose down
```

To remove MySQL and Redis volumes as well:

```bash
docker compose down -v
```

## Environment Variables

Template file:

- `.env.example`

Common variables:

- `MYSQL_ROOT_PASSWORD`: MySQL root password
- `MYSQL_DATABASE`: initial database name
- `DB_HOST` / `DB_PORT` / `DB_NAME` / `DB_USERNAME` / `DB_PASSWORD`: backend database connection
- `REDIS_HOST` / `REDIS_PORT` / `REDIS_DATABASE` / `REDIS_PASSWORD`: Redis connection
- `JWT_SECRET`: JWT signing secret
- `SERVER_PORT`: backend server port

## Runtime Notes

### MySQL

- Service name: `mysql`
- Container name: `huodongpai-mysql`
- Volume: `mysql-data`
- Init directory: `./sql`

### Redis

- Service name: `redis`
- Container name: `huodongpai-redis`
- Volume: `redis-data`

### Backend

- Runs locally with `mvn spring-boot:run`
- Default port: `8080`

### Frontend

- Runs locally with `cd frontend && npm run dev`
- Default port: `5173`

## API Testing

Provided Postman files:

- Collection: `postman/huodongpai.postman_collection.json`
- Environment: `postman/huodongpai.local.postman_environment.json`

Usage:

- `docs/postman-usage.md`

## Documentation

- Database design: `docs/schema-design.md`
- Seed data: `docs/init-data.md`
- Postman guide: `docs/postman-usage.md`
- GitHub repository metadata suggestions: `docs/github-repo-metadata.md`

## Current Scope

Completed:

- Core backend business APIs
- Admin and user frontend pages
- Docker Compose based local infrastructure
- Postman collection

Not included yet:

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
