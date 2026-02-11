# InternMatch - AI-Powered Internship & Hiring Platform

> A modern, full-stack AI-powered internship matching platform built with Spring Boot and machine learning. Companies can post internships, students can apply, and the system uses intelligent resume analysis to rank the best candidates.

## 🎯 Quick Overview

**What is InternMatch?**

InternMatch is a complete internship platform that solves the hiring problem: "How do we match the right students with the right internships?"

- **Companies** post internship opportunities with required skills
- **Students** register, upload PDFs of their resumes, and apply to positions
- **AI Engine** analyzes resume text against job descriptions using TF-IDF + Jaccard similarity to provide match scores
- **Smart Ranking** automatically ranks applicants by AI-calculated compatibility score

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────┐
│          REST API Layer (Controllers)               │
│  (Auth, Internships, Applications, Resumes, Scores) │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│       Business Logic Layer (Services)               │
│  (Auth, Internship, Application, Resume, Ranking)   │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│    Data Access Layer (Repositories)                 │
│         Spring Data JPA Repositories                │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│            MySQL Database                           │
│  (Users, Internships, Applications, Resumes, Scores)│
└─────────────────────────────────────────────────────┘
```

## 💻 Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Language** | Java | 21+ |
| **Framework** | Spring Boot | 3.5.10 |
| **Security** | Spring Security + JWT | JJWT 0.12.3 |
| **Database** | MySQL | 8.0+ |
| **ORM** | JPA/Hibernate | 6.6.41 |
| **PDF Processing** | Apache PDFBox | 3.0.1 |
| **ML/NLP** | SMILE ML (TF-IDF) | 3.0.1 |
| **Password Hashing** | BCrypt | Built-in |
| **Build Tool** | Maven | 3.6+ |

## 📋 Features

### For Students
- ✅ User registration with email and password
- ✅ Browse all available internships
- ✅ Apply to multiple internships
- ✅ Upload PDF resume once, auto-attached to applications
- ✅ Track application status (PENDING/ACCEPTED/REJECTED)
- ✅ View resume preview in applications

### For Companies
- ✅ User registration
- ✅ Post internship opportunities with:
  - Title, description, location, job type
  - Required skills (used for AI matching)
  - Stipend information
  - Application deadline
- ✅ View all applicants for each internship
- ✅ AI-powered ranking of candidates
- ✅ See resume text extraction
- ✅ Accept/reject applications

### For System
- ✅ JWT-based stateless authentication (24-hour tokens)
- ✅ Role-based access control (STUDENT/COMPANY/ADMIN)
- ✅ Automatic duplicate application prevention
- ✅ AI resume-to-job matching algorithm
- ✅ Soft delete for internships (archive old postings)
- ✅ Secure password hashing with BCrypt
- ✅ Comprehensive error handling

## 🤖 AI Resume Ranking Algorithm

The system uses **TF-IDF + Text Similarity** to match resumes to job descriptions:

### How It Works

**Step 1: Extract Data**
```
Job Description → Extract required skills: ["Java", "Spring Boot", "MySQL", ...]
Resume PDF → Extract text: "I have 2 years experience with Java and Spring Boot..."
```

**Step 2: Keyword Matching**
```
Count skills in resume:
- Java: Found ✓
- Spring Boot: Found ✓
- MySQL: Found ✓
- React: Not found ✗

Score: 3/4 skills matched = 75%
```

**Step 3: Jaccard Similarity**
```
Job words: {java, spring, boot, mysql, ...}
Resume words: {java, spring, boot, experience, ...}

Jaccard = Intersection / Union
        = 3 / 15 = 0.2 (0-1 scale)
```

**Step 4: Frequency Boost**
```
How many times do important keywords appear?
"Java" appears 5 times = strong match
"Spring Boot" appears 3 times = moderate match

Frequency score: 0.6
```

**Step 5: Combined Score**
```
Final Score = (0.6 × Jaccard) + (0.4 × Frequency)
            = (0.6 × 0.2) + (0.4 × 0.6)
            = 0.12 + 0.24
            = 0.36 = 36% match
```

**Step 6: Automatic Ranking**
```
All applicants for job are sorted by score:
1. Priya Sharma  - 85% match ("Java Spring Boot expert")
2. John Doe      - 72% match ("Java developer with MySQL")
3. Alice Smith   - 45% match ("Learning Java")
```

## 🚀 Getting Started

### Prerequisites

- **Java 21+** - [Download](https://www.oracle.com/java/technologies/downloads/#java21)
- **Maven 3.6+** - [Download](https://maven.apache.org/download.cgi)
- **MySQL 8.0+** - [Download](https://dev.mysql.com/downloads/mysql/)
- **Git** - [Download](https://git-scm.com/downloads)

### Installation Steps

#### 1. Clone Repository
```bash
git clone https://github.com/Lakshya5876/internmatch-backend.git
cd internmatch-backend
```

#### 2. Create Database
```bash
mysql -u root -p
CREATE DATABASE internmatch_db;
CREATE USER 'internmatch_user'@'localhost' IDENTIFIED BY 'internmatch_pass_123';
GRANT ALL PRIVILEGES ON internmatch_db.* TO 'internmatch_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

#### 3. Configure Application (Optional)
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/internmatch_db
spring.datasource.username=internmatch_user
spring.datasource.password=internmatch_pass_123
```

Or set environment variables for production:
```bash
# Linux/Mac
export DB_URL=jdbc:mysql://your-server:3306/internmatch_db
export DB_USERNAME=your_user
export DB_PASSWORD=your_password

# Windows PowerShell
$env:DB_URL="jdbc:mysql://your-server:3306/internmatch_db"
$env:DB_USERNAME="your_user"
$env:DB_PASSWORD="your_password"
```

#### 4. Build Project
```bash
mvn clean compile
```

#### 5. Run Application
```bash
mvn spring-boot:run
```

**Expected Output:**
```
...
Tomcat initialized with port 8080 (http)
...
Started InternmatchBackendApplication in 4.098 seconds
```

Application running at: **`http://localhost:8080`**

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication Endpoints

#### Register User
```http
POST /auth/register
Content-Type: application/json

{
  "fullName": "John Student",
  "email": "john@example.com",
  "password": "securePassword123",
  "role": "STUDENT"
}

Response:
{
  "userId": 1,
  "email": "john@example.com",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "role": "STUDENT",
  "fullName": "John Student"
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "securePassword123"
}

Response:
{
  "userId": 1,
  "email": "john@example.com",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "role": "STUDENT",
  "fullName": "John Student"
}
```

### Internship Endpoints

#### Get All Internships (Public)
```http
GET /internships
```

#### Create Internship (Company Only)
```http
POST /internships
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Backend Developer Intern",
  "description": "Build REST APIs with Spring Boot",
  "location": "Remote",
  "jobType": "REMOTE",
  "stipend": 20000,
  "skills": "Java,Spring Boot,MySQL",
  "deadline": "2026-12-31"
}
```

#### Get My Internships (Company Only)
```http
GET /internships/my-internships
Authorization: Bearer {token}
```

### Application Endpoints

#### Apply to Internship (Student Only)
```http
POST /applications/apply
Authorization: Bearer {token}
Content-Type: application/json

{
  "internshipId": 1
}
```

#### Get Applicants (Company Only)
```http
GET /applications/internship/{internshipId}
Authorization: Bearer {token}
```

#### Get My Applications (Company Only)
```http
GET /applications/my-internships
Authorization: Bearer {token}
```

### Resume Endpoints

#### Upload Resume (Student Only)
```http
POST /resumes/upload?applicationId=1
Authorization: Bearer {token}
Content-Type: multipart/form-data

[Upload PDF file]
```

#### Get Resume Full Text
```http
GET /resumes/application/{applicationId}
Authorization: Bearer {token}
```

#### Get Resume Preview (500 chars)
```http
GET /resumes/application/{applicationId}/preview
Authorization: Bearer {token}
```

### Scoring Endpoints

#### Calculate AI Score (Company Only)
```http
POST /scores/calculate
Authorization: Bearer {token}
Content-Type: application/json

{
  "applicationId": 1,
  "internshipId": 1
}

Response:
{
  "id": 1,
  "similarityPercentage": 75,
  "keywordMatches": 6,
  "totalKeywords": 8,
  "explanation": "Good match - 6 out of 8 required skills found in resume",
  "rank": 1
}
```

#### Get Ranked Applicants (Company Only)
```http
GET /scores/internship/{internshipId}/ranked
Authorization: Bearer {token}

Response: [List of applicants ranked by score]
```

## 🗄️ Database Schema

### Users Table
```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL, -- BCrypt hashed
  full_name VARCHAR(255) NOT NULL,
  role ENUM('STUDENT', 'COMPANY', 'ADMIN'),
  organization VARCHAR(255), -- For companies
  phone VARCHAR(20),
  enabled BOOLEAN DEFAULT true,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Internships Table
```sql
CREATE TABLE internships (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL,
  description TEXT NOT NULL,
  location VARCHAR(255) NOT NULL,
  job_type VARCHAR(50), -- FULL_TIME, PART_TIME, REMOTE
  duration INT, -- in months
  stipend DECIMAL(10,2),
  skills VARCHAR(500), -- comma-separated
  responsibilities TEXT,
  qualifications TEXT,
  application_deadline DATE,
  company_id BIGINT NOT NULL,
  active BOOLEAN DEFAULT true,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (company_id) REFERENCES users(id)
);
```

### Applications Table
```sql
CREATE TABLE applications (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id BIGINT NOT NULL,
  internship_id BIGINT NOT NULL,
  status ENUM('PENDING', 'ACCEPTED', 'REJECTED', 'WITHDRAWN'),
  rejection_reason TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY unique_application (student_id, internship_id),
  FOREIGN KEY (student_id) REFERENCES users(id),
  FOREIGN KEY (internship_id) REFERENCES internships(id)
);
```

### Resumes Table
```sql
CREATE TABLE resumes (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  application_id BIGINT NOT NULL UNIQUE,
  file_name VARCHAR(255),
  file_size BIGINT,
  mime_type VARCHAR(100),
  extracted_text LONGTEXT,
  file_data LONGBLOB, -- PDF bytes
  uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (application_id) REFERENCES applications(id)
);
```

### Scores Table
```sql
CREATE TABLE scores (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  application_id BIGINT NOT NULL,
  internship_id BIGINT NOT NULL,
  similarity_score DECIMAL(5,2), -- 0.0 to 1.0
  similarity_percentage INT, -- 0 to 100
  keyword_matches INT,
  total_keywords INT,
  explanation TEXT,
  applicant_rank INT,
  scored_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY unique_score (application_id),
  FOREIGN KEY (application_id) REFERENCES applications(id),
  FOREIGN KEY (internship_id) REFERENCES internships(id)
);
```

## 🔐 Security Features

### Authentication
- **JWT (JSON Web Tokens)** - Stateless authentication
- **Token Expiration** - 24 hours
- **Bearer Token** - Sent in `Authorization` header
- **Secret Key** - Securely stored and fixed (never changes)

### Authorization
- **Role-Based Access Control (RBAC)**
  - `STUDENT` - Apply to internships, upload resumes
  - `COMPANY` - Post internships, view applicants, score candidates
  - `ADMIN` - Full access (reserved for future)

### Password Security
- **BCrypt Hashing** - Industry standard password hashing
- **Salted** - Each password gets unique salt
- **One-way** - Cannot be reversed (lost passwords require reset)

### API Security
- **CSRF Protection** - Disabled for REST API (stateless)
- **CORS** - Configured for localhost (adjust for production)
- **Input Validation** - Request validation annotations
- **Error Messages** - Generic server errors (no information disclosure)

### Data Protection
- **Unique Constraint** - Prevents duplicate applications
- **Soft Delete** - Internships marked inactive, not permanently deleted
- **Encrypted Credentials** - Database user has limited privileges

## 📊 Testing

### Manual Testing
The system includes sample data for testing:
- 8 registered companies
- 8 registered students
- 6 internship postings
- 5 student applications

### Test Data Access
```bash
# View test data in MySQL
mysql -u root -p
USE internmatch_db;
SELECT * FROM users;
SELECT * FROM internships;
SELECT * FROM applications;
```

### API Testing with curl
```bash
# Register a student
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test Student",
    "email": "test@example.com",
    "password": "password123",
    "role": "STUDENT"
  }'

# Get all internships
curl http://localhost:8080/api/internships

# Apply to internship (requires token)
curl -X POST http://localhost:8080/api/applications/apply \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"internshipId": 1}'
```

## 📦 Project Structure

```
internmatch-backend/
├── src/
│   ├── main/
│   │   ├── java/com/internmatch/backend/
│   │   │   ├── controller/        (6 REST endpoints)
│   │   │   ├── service/           (5 business logic services)
│   │   │   ├── repository/        (5 data access layers)
│   │   │   ├── entity/            (7 database entities)
│   │   │   ├── dto/               (11 data transfer objects)
│   │   │   ├── security/          (JWT & authentication)
│   │   │   ├── config/            (Spring configuration)
│   │   │   ├── exception/         (global error handling)
│   │   │   └── util/              (utility functions)
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── (minimal test coverage)
├── pom.xml                         (Maven dependencies)
├── README.md                       (This file)
├── HELP.md                         (Spring Boot help)
└── process.txt                     (Development phases log)
```

## 🔧 Build & Deployment

### Local Development
```bash
# Clean build
mvn clean compile

# Run with hot reload
mvn spring-boot:run

# Run tests
mvn test
```

### Production Build
```bash
# Create JAR package
mvn clean package

# Run JAR
java -jar target/internmatch-backend-0.0.1-SNAPSHOT.jar

# Set environment-specific properties
java -jar target/internmatch-backend-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:mysql://prod-server:3306/internmatch_db \
  --spring.datasource.username=prod_user \
  --spring.datasource.password=prod_password
```

### Docker (Optional)
```dockerfile
FROM openjdk:21-slim
WORKDIR /app
COPY target/internmatch-backend-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 📈 Performance Considerations

- **Indexing**: Database has unique constraints on (student_id, internship_id)
- **Lazy Loading**: Resume text only loaded when needed
- **Pagination**: Not yet implemented (consider adding for large datasets)
- **Caching**: Not yet implemented (consider Redis for internship listings)

## 🐛 Known Issues & Limitations

1. **Test Coverage** - Minimal unit/integration tests (2% coverage)
2. **File Storage** - PDFs stored in LONGBLOB (use S3 for production)
3. **Database Credentials** - Hardcoded in properties (use environment variables)
4. **PDF Size Limit** - 10MB max (configurable in properties)
5. **Token Expiration** - No refresh token mechanism
6. **Email Verification** - No email confirmation for registration

## 🚀 Future Enhancements

- [ ] Email verification and password reset
- [ ] Refresh token mechanism
- [ ] Dashboard analytics
- [ ] Advanced job search with filters
- [ ] Notification system (email/push)
- [ ] Improved ML model with NLP
- [ ] Video interview scheduling
- [ ] Integration with LinkedIn profiles
- [ ] Admin dashboard
- [ ] Rate limiting and request throttling
- [ ] Comprehensive API documentation (Swagger)
- [ ] Docker and Kubernetes deployment configs
- [ ] CI/CD pipeline (GitHub Actions)

## 📄 License

This project is open source and available under the MIT License.

## 👨‍💻 Author

**Lakshya Daga**
- GitHub: [@Lakshya5876](https://github.com/Lakshya5876)
- Email: lakshyadaga@gmail.com

## 📞 Support

For issues, questions, or contributions:
1. Create an issue on [GitHub Issues](https://github.com/Lakshya5876/internmatch-backend/issues)
2. Submit a pull request for improvements
3. Contact: lakshyadaga@gmail.com

## 🙏 Acknowledgments

- Spring Boot and Spring Security documentation
- Apache PDFBox for PDF processing
- SMILE ML library for text similarity algorithms
- MySQL and Java communities

---

**Last Updated:** February 2026
**Status:** Production-Ready ✅
**Version:** 1.0.0
