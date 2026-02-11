# InternMatch Backend

Spring Boot backend for an internship matching platform with JWT auth, role-based access, resume PDF parsing, and applicant scoring.

## Tech Stack
- Java 21
- Spring Boot 3.5.10
- Spring Security + JWT (`jjwt`)
- Spring Data JPA + MySQL
- Apache PDFBox
- Maven

## Features
- User registration and login (`STUDENT`, `COMPANY`)
- JWT-secured APIs with role checks
- Internship CRUD for companies (soft delete)
- Student internship applications with duplicate prevention
- Resume upload (PDF only), text extraction, preview/full retrieval
- Company-only applicant scoring and ranking

## Project Structure
`src/main/java/com/internmatch/backend`
- `controller` REST endpoints
- `service` business logic
- `repository` JPA repositories
- `entity` JPA models
- `dto` request/response models
- `security`, `config`, `exception`, `util`

## Configuration
File: `src/main/resources/application.properties`

Database settings are environment-variable friendly:
- `DB_URL` (default: `jdbc:mysql://localhost:3306/internmatch_db`)
- `DB_USERNAME` (default: `internmatch_user`)
- `DB_PASSWORD` (default: `internmatch_pass_123`)

JWT Authentication:
- `JWT_SECRET_BASE64` (optional, must be Base64-encoded, at least 32 bytes/256 bits)
  - Default: `TXlTZWNyZXRLZXlGb3JJbnRlcm5NYXRjaFByb2plY3QyMDI2` (default key, change in production)

Other important settings:
- `spring.jpa.hibernate.ddl-auto=update` - Auto-update schema on startup
- `spring.jpa.open-in-view=false` - Disable Open Session in View (good practice)
- `spring.jpa.show-sql=true` - Log SQL queries (set to `false` in production)
- Multipart upload max size: `10MB`

## Run Locally
1. Ensure MySQL is running and the database exists.
2. Build:
```bash
./mvnw clean package
```
3. Run:
```bash
./mvnw spring-boot:run
```
Base URL: `http://localhost:8080/api`

## Authentication API
### Register
`POST /api/auth/register`
```json
{
  "email": "john@example.com",
  "password": "securePassword123",
  "fullName": "John Student",
  "role": "STUDENT"
}
```

### Login
`POST /api/auth/login`
```json
{
  "email": "john@example.com",
  "password": "securePassword123"
}
```

### Auth Response Shape
```json
{
  "token": "jwt-token",
  "type": "Bearer",
  "id": 1,
  "email": "john@example.com",
  "fullName": "John Student",
  "role": "STUDENT"
}
```

## Internship API
### Create Internship (COMPANY)
`POST /api/internships`
```json
{
  "title": "Backend Developer Intern",
  "description": "Build REST APIs with Spring Boot",
  "location": "Remote",
  "jobType": "REMOTE",
  "duration": 3,
  "stipend": 20000,
  "skills": "Java,Spring Boot,MySQL",
  "responsibilities": "Build APIs",
  "qualifications": "CS undergrad",
  "applicationDeadline": "2026-12-31"
}
```

### Other Internship Endpoints
- `GET /api/internships` (public)
- `GET /api/internships/{id}` (public)
- `GET /api/internships/my-internships` (COMPANY)
- `PUT /api/internships/{id}` (COMPANY)
- `DELETE /api/internships/{id}` (COMPANY, soft delete)

## Application API
- `POST /api/applications/apply` (STUDENT)
- `GET /api/applications/internship/{internshipId}` (COMPANY)
- `GET /api/applications/my-internships` (COMPANY)

Apply request:
```json
{
  "internshipId": 1
}
```

## Resume API
- `POST /api/resumes/upload?applicationId={id}` (STUDENT, multipart PDF)
- `GET /api/resumes/application/{applicationId}` (STUDENT/COMPANY with ownership checks)
- `GET /api/resumes/application/{applicationId}/preview` (STUDENT/COMPANY with ownership checks)

## Scoring API
- `POST /api/scores/calculate` (COMPANY, internship ownership enforced)
- `GET /api/scores/internship/{internshipId}/ranked` (COMPANY, internship ownership enforced)

Score request:
```json
{
  "applicationId": 1,
  "internshipId": 1
}
```

## Security Notes
- Stateless JWT authentication
- Password hashing with BCrypt
- Role-based method security via `@PreAuthorize`
- Global exception handling returns structured JSON errors

## Current Limitations
- Minimal automated test coverage (`contextLoads` smoke test)
- Resume file bytes are stored in DB (`LONGBLOB`), which may be costly at scale
- No refresh-token flow
- No email verification/password reset flow

## Build Commands
```bash
./mvnw test
./mvnw clean package
```
