# LibTrack

A Libary management system for tracking books, members, loans and fine.
Members can browse the catalog and borrow book; administrators manage the collection members and fines.

Entire system runs with a single command with Docker Compose

## Tech Stack

- Backend;
  - Spring Boot (java 26)
  - Spring Data JPA
  - Spring Security with JWT
  - Liquibase for db migrations

- Frontend;
  - Angular
  - nginx acting as a proxy

- Database
  - PostgreSQL

- Chache
  - Redis

- Docker

## Running the project

Prerequisites
 - Docker Desktop

## 1. Clone repo 
```bash
 git clone https://github.com/Berk-Cinek/LibTrack
```

## 2. Set up enviorment variables ##

the project uses reads secrets from a .env file at the project root, Copy the provided template of .env.example as just .env

```env
JWT_SECRET=<a long random string>
POSTGRES_DB=LibTrackDB
POSTGRES_USER=<your db user>
POSTGRES_PASSWORD=<a strong password>
```

All checksums are computed using SHA-256 and represented as 64-character lowercase hexadecimal strings.
If you need to generate one, use this in powershell;

```powershell
[Convert]::ToBase64String((1..48 | ForEach-Object { Get-Random -Maximum 256 }))
```

## 3.Start the system 
```bash
docker compose up --build
```
This builds the backend and frontend images, then starts all four containers. On first run,
the database is initialized and Liquibase creates all tables automatically.

## Default admin account 

- username; admin
- password; 1

## Stopping the system 
```bash
docker compose down
```

 # Architecture
 LibTrack is a four-container system orchestrated by Docker Compose.
 Only the frontend is exposed to the outside world; every other service communicates over Docker's internal network.

## Request Flow

1. The browser loads the angular side single page app from nginx on port 80
2. When the app needs data, it makes a request to '/api/....'
3. nginx reverse-proxies the request to the backend container on 8080
4. Backend handles auth, bussiness logic, data access, and presistance with PotsgreSQL and redis chaching
5. Response travels back through nginx

Because the browser only ever talks to a single origin (nginx),
there are no cross-origin (CORS) problems and httpOnly authentication cookies work seamlessly.

Authentication with JWT in httpOnly cookie

# Example API Requests
All examples use 'curl'. beacuse auth uses httpONly cookie (not a bearer token),
the login request saves the cookie to a file with -c, and authenticated requests send it back with -b.

- Base URL is http://localhost/api when running through Docker 

## Register a new member
```bash
curl -X POST http://localhost/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "jane",
    "password": "password123",
    "fullName": "Jane Doe",
    "email": "jane@example.com"
  }'
```

## Login (save auth cookie)
```bash
curl -X POST http://localhost/api/auth/login \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "username": "jane",
    "password": "password123"
  }'
```
  
- The '-c cookies.txt' flag saves the httpOnly auth cookie to a local file for use in the next requests.

## Browse the book catalog (authenticated)
```bash
 curl http://localhost/api/books \
  -b cookies.txt
```

## Interactive API documentation
The backend exposes Swagger UI, where you can browse and try every endpoint:
```
http://localhost/swagger-ui/index.html
```

