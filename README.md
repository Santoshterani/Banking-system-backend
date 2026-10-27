# Banking System Backend

A RESTful core banking backend built with **Java, Spring Boot, Spring Security, JWT, Spring Data JPA, and MySQL**. It provides APIs for managing branches, employees, customers, accounts, transactions, KYC, loans, fixed deposits, recurring deposits, approval workflows, notifications, audit logs, and account statements.

> **Project status:** This README describes the source code included in this repository archive. The project is intended for learning and demonstration; it has not been independently security-audited or validated for production banking use.

## Key Features

- **Authentication:** Login/logout, JWT validation, and an HTTP-only `jwt` cookie.
- **Account protection:** Configurable failed-login limit and temporary account lockout.
- **Password reset:** Email-delivered OTP, hashed OTP storage, configurable expiry, and password reset.
- **Role-based access control (RBAC):** Method-level authorization through Spring Security `@PreAuthorize`.
- **Branch-level access:** Branch-scoped access checks for relevant operations.
- **Banking operations:** Branch, employee, customer, account, deposit, withdrawal, and transfer APIs.
- **Large-transaction approval:** Deposits, withdrawals, and transfers above a configurable threshold can be held for approval.
- **KYC and loan workflows:** Submit, review, approve, and reject records.
- **Fixed and recurring deposits:** Create/close deposit products and run a scheduled maturity check.
- **Notifications:** Email notifications, with customer notification records stored in the database.
- **Audit trail:** AOP-based audit logging for selected mutating operations.
- **Caching:** In-process cache for branch reference data.
- **Reporting:** Dashboard metrics and PDF/Excel account statement exports.
- **API documentation and observability:** OpenAPI/Swagger UI, Spring Boot Actuator, and Prometheus metrics.
- **Validation and error handling:** Request DTO validation and centralized exception responses.

## Technology Stack

| Area | Technology |
|---|---|
| Language | Java (Maven compiler target: Java 17) |
| Framework | Spring Boot 4.0.5 |
| Web/API | Spring MVC, REST controllers |
| Security | Spring Security, JWT (JJWT 0.12.5), BCrypt password hashing |
| Persistence | Spring Data JPA, Hibernate |
| Database | MySQL |
| Validation | Jakarta Bean Validation / Spring Boot Validation |
| Email | Spring Boot Mail |
| Cross-cutting concerns | Spring AOP, Spring Cache |
| API documentation | Springdoc OpenAPI 2.7.0 |
| Monitoring | Spring Boot Actuator, Micrometer Prometheus registry |
| Reports | Apache PDFBox 3.0.3, Apache POI 5.3.0 |
| Build | Maven / Maven Wrapper |

## Architecture

The application follows a layered architecture:

```text
REST Controller
      |
      v
Service / Business Logic
      |
      v
Spring Data JPA Repository
      |
      v
MySQL Database
```

Cross-cutting features support the main flow:

```text
JWT Filter + Spring Security
          |
          v
Controller authorization (@PreAuthorize)
          |
          v
Service layer ---- Audit AOP
          |--------- Branch access checks
          |--------- Branch cache
          v
      Repositories --> MySQL
```

## Main Modules

| Module | Responsibility |
|---|---|
| Authentication | Login, logout, current-user details, forgot password, reset password |
| Branches and employees | Branch management, employee creation/updates, activation/deactivation |
| Customers and accounts | Customer records, account creation, updates, lookup, pagination |
| Transactions | Deposits, withdrawals, transfers, transaction history and balance validation |
| Approvals | Review and approve/reject qualifying large transactions |
| KYC | KYC submission, approval, rejection and status tracking |
| Loans | Loan application and approval/rejection workflows |
| Fixed deposits (FD) | Create, list, close and maturity processing |
| Recurring deposits (RD) | Create, list, close and maturity processing |
| Notifications | Customer notifications and email delivery |
| Audit | Review selected administrative and business actions |
| Reporting | Dashboard statistics and PDF/Excel account statements |
| Operations | Swagger/OpenAPI, health information, metrics and Prometheus endpoint |

## Roles

The role initializer creates the following roles when the application starts:

- `SUPER_ADMIN`
- `ADMIN`
- `REGIONAL_MANAGER`
- `BRANCH_MANAGER`
- `CASHIER`
- `CUSTOMER_SERVICE_EXECUTIVE`
- `LOAN_OFFICER`
- `KYC_OFFICER`
- `AUDITOR`

Access is enforced through Spring Security and method-level role checks. Some operations also apply branch-level authorization. The exact permissions differ by endpoint; review the controller's `@PreAuthorize` annotation and service-level checks before changing authorization rules.

## Project Structure

```text
src/
├── main/
│   ├── java/com/example/demo/
│   │   ├── Controller/        # REST controllers
│   │   ├── Services/          # Business logic
│   │   ├── Repositories/      # Spring Data repositories
│   │   ├── DTO/               # Authentication DTOs
│   │   ├── dto/request/       # API request DTOs and validation
│   │   ├── dto/response/      # Standard API/error response DTOs
│   │   ├── entity/            # JPA entities
│   │   ├── config/            # Security, CORS, cache, bootstrap, OpenAPI
│   │   ├── jwt/               # JWT request filter
│   │   ├── security/          # JWT utilities and security helpers
│   │   ├── exception/         # Exceptions and global exception handling
│   │   ├── audit/             # Audit annotation and AOP aspect
│   │   ├── reporting/         # PDF/Excel statement generation
│   │   └── scheduler/         # FD/RD maturity job
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/example/demo/
        └── BankingSystemApplicationTests.java
```

> Package names reflect the current source tree. A future cleanup could standardize package names to lowercase (`controller`, `service`, `repository`, etc.).

## Prerequisites

- JDK 17 or later (the Maven compiler target is Java 17)
- MySQL 8.x or a compatible MySQL server
- Git (optional, for cloning)
- Maven is not required globally if you use the included Maven Wrapper

## Setup and Run Locally

### 1. Clone the repository

```bash
git clone https://github.com/Santoshterani/Banking-system-backend.git
cd Banking-system-backend
```

### 2. Create the database

Run this in MySQL:

```sql
CREATE DATABASE bank_db;
```

The application is configured with `spring.jpa.hibernate.ddl-auto=update`, so Hibernate updates the schema at startup. For a production system, use a database migration tool such as Flyway or Liquibase rather than relying on automatic schema updates.

### 3. Configure environment variables

The application supports these environment variables. Do not commit real credentials or secrets to GitHub.

| Variable | Purpose | Local development example |
|---|---|---|
| `DB_URL` | JDBC connection URL | `jdbc:mysql://localhost:3306/bank_db?useSSL=false&serverTimezone=UTC` |
| `DB_USERNAME` | MySQL username | `root` |
| `DB_PASSWORD` | MySQL password | Your local MySQL password |
| `JWT_SECRET` | JWT signing secret; use a random secret of at least 32 bytes | Generate your own secret |
| `JWT_EXPIRATION_MS` | JWT lifetime in milliseconds | `86400000` (24 hours) |
| `BOOTSTRAP_ADMIN_EMAIL` | Email for initial Super Admin | `admin@bank.com` |
| `BOOTSTRAP_ADMIN_PASSWORD` | Initial Super Admin password | Set a strong, unique value |
| `MAIL_HOST` | SMTP host | `smtp.gmail.com` |
| `MAIL_PORT` | SMTP port | `587` |
| `MAIL_USERNAME` | SMTP account username | Your SMTP email |
| `MAIL_PASSWORD` | SMTP/app password | Your SMTP app password |
| `OTP_EXPIRY_MINUTES` | Password-reset OTP expiry | `10` |
| `MAX_FAILED_LOGIN_ATTEMPTS` | Failed-login attempts before lockout | `5` |
| `ACCOUNT_LOCK_MINUTES` | Account lockout duration | `15` |
| `LARGE_TXN_THRESHOLD` | Approval threshold for large transactions | `50000` |

The application properties contain local development defaults for the database and bootstrap administrator. **Override them before sharing or deploying the application.** The bootstrap administrator is created only when the users table is empty. Change its password immediately after the first login. `JWT_SECRET` must be at least 32 bytes or application startup will fail.

#### Windows PowerShell example

Set the values for your machine in the current PowerShell session before starting the app:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/bank_db?useSSL=false&serverTimezone=UTC"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "YOUR_MYSQL_PASSWORD"
$env:JWT_SECRET = "REPLACE_WITH_A_RANDOM_SECRET_AT_LEAST_32_BYTES_LONG"
$env:BOOTSTRAP_ADMIN_EMAIL = "admin@bank.com"
$env:BOOTSTRAP_ADMIN_PASSWORD = "REPLACE_WITH_A_STRONG_PASSWORD"
```

For email-based OTP and notification delivery, also set the `MAIL_*` variables.

### 4. Start the backend

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

The API runs at:

```text
http://localhost:9090
```

### 5. Build and test

Windows:

```powershell
.\mvnw.cmd clean package
.\mvnw.cmd test
```

Linux/macOS:

```bash
./mvnw clean package
./mvnw test
```

The uploaded source currently includes a Spring application-context smoke test. Add unit and integration tests for authentication, transaction correctness, branch authorization, approval workflows, and password-reset behavior before treating the project as production-ready.

## Authentication Flow

1. Send `POST /auth/login` with the user's email and password.
2. On successful login, the API sets a JWT in an HTTP-only cookie named `jwt`.
3. The JWT filter reads that cookie on subsequent requests, validates the token, loads the current user, and populates Spring Security's `SecurityContext`.
4. Role checks and branch-level checks control access to protected operations.
5. Send `POST /auth/logout` to clear the cookie.

Example login request:

```http
POST http://localhost:9090/auth/login
Content-Type: application/json

{
  "email": "admin@bank.com",
  "password": "YOUR_BOOTSTRAP_PASSWORD"
}
```

The JWT is delivered as a cookie rather than as a token in the JSON response. For a browser-based frontend, configure requests to include credentials/cookies. The current CORS configuration allows `http://localhost:5173`.

## API Reference

Unless stated otherwise, protected endpoints require a valid JWT cookie. Role and branch permissions apply to the individual endpoints.

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/auth/login` | Authenticate and set the JWT cookie |
| `POST` | `/auth/logout` | Clear the JWT cookie |
| `GET` | `/auth/me` | Get the authenticated user's details |
| `POST` | `/auth/forgot-password` | Request a password-reset OTP by email |
| `POST` | `/auth/reset-password` | Reset password using email, OTP and new password |

### Branches

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/admin/branch/create` | Create a branch |
| `GET` | `/admin/branch/all` | List branches |
| `GET` | `/admin/branch/{id}` | Get a branch by ID |
| `PUT` | `/admin/branch/update/{id}` | Update a branch |
| `DELETE` | `/admin/branch/delete/{id}` | Delete a branch |

### Employees

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/admin/employee/create` | Create an employee/user |
| `GET` | `/admin/employee/all` | List employees |
| `GET` | `/admin/employee/page` | Get a paginated employee list |
| `GET` | `/admin/employee/{id}` | Get an employee by ID |
| `PUT` | `/admin/employee/update/{id}` | Update employee details |
| `PUT` | `/admin/employee/deactivate/{id}` | Deactivate an employee |
| `PUT` | `/admin/employee/activate/{id}` | Activate an employee |

### Customers and accounts

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/admin/customer/create` | Create a customer |
| `GET` | `/admin/customer/all` | List customers |
| `GET` | `/admin/customer/page` | Get a paginated customer list |
| `GET` | `/admin/customer/{id}` | Get a customer by ID |
| `PUT` | `/admin/customer/update/{id}` | Update customer details |
| `DELETE` | `/admin/customer/delete/{id}` | Delete a customer |
| `POST` | `/admin/account/create` | Create a customer account (`SAVINGS` or `CURRENT`) |
| `GET` | `/admin/account/all` | List accounts |
| `GET` | `/admin/account/page` | Get a paginated account list |
| `GET` | `/admin/account/{id}` | Get an account by ID |
| `PUT` | `/admin/account/update/{id}` | Update an account |
| `DELETE` | `/admin/account/delete/{id}` | Delete an account |

Pagination uses Spring Data `Pageable` parameters such as `?page=0&size=20`. The default page size is 20 and the configured maximum is 100.

### Transactions and approvals

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/admin/transaction/deposit` | Deposit money into an account |
| `POST` | `/admin/transaction/withdraw` | Withdraw money from an account |
| `POST` | `/admin/transaction/transfer` | Transfer money between accounts |
| `GET` | `/admin/transaction/account/{accountId}` | List transactions for an account |
| `GET` | `/admin/approval/pending` | List pending approval requests |
| `PUT` | `/admin/approval/approve/{id}` | Approve and execute a pending transaction |
| `PUT` | `/admin/approval/reject/{id}` | Reject a pending transaction |

Deposit request example:

```json
{
  "accountId": 1,
  "amount": 1000.0
}
```

Transfer request example:

```json
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 500.0
}
```

The default large-transaction threshold is `50000`. Qualifying transactions from branch-level roles can be recorded as pending approval rather than executed immediately. The threshold is configurable through `LARGE_TXN_THRESHOLD`.

### KYC and loans

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/admin/kyc/submit` | Submit KYC information |
| `GET` | `/admin/kyc/all` | List KYC records |
| `GET` | `/admin/kyc/customer/{id}` | Get KYC details for a customer |
| `PUT` | `/admin/kyc/approve/{id}` | Approve KYC |
| `PUT` | `/admin/kyc/reject/{id}` | Reject KYC |
| `POST` | `/admin/loan/apply` | Submit a loan application |
| `GET` | `/admin/loan/customer/{customerId}` | List loans for a customer |
| `GET` | `/admin/loan/all` | List loans |
| `GET` | `/admin/loan/page` | Get a paginated loan list |
| `PUT` | `/admin/loan/approve/{loanId}` | Approve a loan |
| `PUT` | `/admin/loan/reject/{loanId}` | Reject a loan |

### Fixed and recurring deposits

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/admin/fd/create` | Create a fixed deposit |
| `GET` | `/admin/fd/all` | List fixed deposits |
| `GET` | `/admin/fd/customer/{id}` | List fixed deposits for a customer |
| `PUT` | `/admin/fd/close/{id}` | Close a fixed deposit |
| `POST` | `/admin/rd/create` | Create a recurring deposit |
| `GET` | `/admin/rd/all` | List recurring deposits |
| `GET` | `/admin/rd/customer/{id}` | List recurring deposits for a customer |
| `PUT` | `/admin/rd/close/{id}` | Close a recurring deposit |

A scheduled job checks active FDs/RDs nightly at 1:00 AM server time and marks products past their calculated maturity date as matured. Email delivery depends on SMTP configuration.

### Notifications, reports, statements, roles and audit logs

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/admin/notification/customer/{id}` | List notifications for a customer |
| `GET` | `/admin/report/dashboard` | Retrieve dashboard statistics |
| `GET` | `/admin/account/{id}/statement/pdf` | Download an account statement as PDF |
| `GET` | `/admin/account/{id}/statement/excel` | Download an account statement as Excel (`.xlsx`) |
| `POST` | `/admin/role/create` | Create a role (restricted to admins) |
| `GET` | `/admin/audit-log?page=0&size=20` | Retrieve paginated audit logs (restricted to admin/auditor roles) |

### API documentation and monitoring

| Endpoint | Description |
|---|---|
| `/swagger-ui.html` | Swagger UI |
| `/v3/api-docs` | OpenAPI JSON |
| `/actuator/health` | Application health |
| `/actuator/info` | Application information |
| `/actuator/metrics` | Application metrics (authenticated) |
| `/actuator/prometheus` | Prometheus metrics (authenticated) |

## Configuration Notes

- API port: `9090`.
- Database: `bank_db` by default; configure it with `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`.
- JWT lifetime: 24 hours by default (`JWT_EXPIRATION_MS=86400000`).
- OTP expiry: 10 minutes by default.
- Login lockout: 5 failed attempts and a 15-minute lock by default.
- CORS currently permits `http://localhost:5173` and credentialed requests.
- The JWT cookie currently uses `Secure=false` for local HTTP development. Enable `Secure` when deployed behind HTTPS and review cookie, CSRF, CORS, and proxy settings for the production environment.

## Implementation Scope and Limitations

- This archive contains a UPI entity and repository, but no dedicated UPI REST controller/service was found in the source tree.
- Although the Maven artifact name mentions fraud detection, there is no separate fraud-detection engine or dedicated fraud-detection module in this code snapshot. Large-transaction approval is implemented, but it should not be described as ML-based or comprehensive fraud detection.
- Automated tests are currently limited to an application-context smoke test. Add service-level and API integration tests, particularly around money movement, concurrency, authorization, and approval state transitions.
- This is a demonstration project, not a production banking platform. A real deployment requires security review, secure secret management, database migrations, stronger monitoring/alerting, reliable email configuration, and tests for financial correctness and concurrency.

## License

No license file was included in the provided archive. Add a `LICENSE` file if you intend to publish or distribute this project under a specific license.
