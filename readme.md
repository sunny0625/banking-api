# Banking API Gateway Simulator

A production-style Banking REST API built with Java Spring Boot — simulating core banking operations including account management, fund transfers, and transaction history. Secured with JWT authentication, documented with Swagger UI, containerized with Docker, and deployed on AWS EC2.

> Built as Portfolio Project 1 by a DevOps + API Integration Engineer with hands-on FinTech experience (Oracle FlexCube UBS).

---

## Live Demo

| Resource | Link |
|---|---|
| Swagger UI (live) | `http://YOUR_EC2_IP:8080/swagger-ui.html` |
| GitHub Repo | `https://github.com/sunny0625/banking-api` |

> **To test the API:** Open Swagger UI → Register → Login → Copy token → Click Authorize → Test all endpoints

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5.x |
| Security | Spring Security + JWT (JJWT 0.11.5) |
| Database | H2 In-Memory (JPA / Hibernate) |
| API Docs | Swagger UI (springdoc-openapi 2.5.0) |
| Build | Maven |
| Container | Docker |
| Cloud | AWS EC2 (Ubuntu 22.04, t2.micro) |
| IDE | IntelliJ IDEA |

---

## API Endpoints

### Authentication (no token required)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and receive JWT token |

### Accounts (JWT required)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/accounts` | Create a new bank account |
| GET | `/api/accounts` | List all accounts |
| GET | `/api/accounts/{accountNumber}` | Get account details + balance |
| GET | `/api/accounts/{accountNumber}/balance` | Get balance only |
| POST | `/api/accounts/{accountNumber}/deposit` | Deposit funds |

### Transactions (JWT required)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/transactions/transfer` | Transfer funds between accounts |
| GET | `/api/transactions/history/{accountNumber}` | Full transaction history |
| GET | `/api/transactions/history/{accountNumber}/success` | Successful transactions only |
| GET | `/api/transactions` | All transactions (admin) |

---

## Project Structure

```
src/main/java/com/banking/bankingapi/
├── controller/          # REST endpoints (Auth, Account, Transaction)
├── service/             # Business logic layer
├── repository/          # Spring Data JPA interfaces
├── model/               # JPA entities (Account, Transaction, User)
├── dto/                 # Request and Response objects
└── security/            # JWT filter, SecurityConfig, SwaggerConfig
```

---

## How to Run Locally

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker (optional)

### Option 1 — Run with Maven

```bash
# Clone the repo
git clone https://github.com/sunny0625/banking-api.git
cd banking-api

# Set your JWT secret in application.properties
# jwt.secret=YOUR_BASE64_256BIT_SECRET_HERE
# Generate one: echo -n "your-32-char-string" | base64

# Run
./mvnw spring-boot:run
```

Open: `http://localhost:8080/swagger-ui.html`

### Option 2 — Run with Docker

```bash
# Pull from Docker Hub
docker pull sunny0625/banking-api:v1

# Run with your JWT secret as environment variable
docker run -d -p 8080:8080 \
  -e JWT_SECRET=YOUR_BASE64_SECRET_HERE \
  --name banking-api \
  sunny0625/banking-api:v1
```

---

## Quick Test Walkthrough

1. Open Swagger UI at `http://localhost:8080/swagger-ui.html`
2. **Register** — `POST /api/auth/register` with username + password
3. **Login** — `POST /api/auth/login` → copy the token from response
4. **Authorize** — click the green Authorize button → paste `Bearer <token>`
5. **Create accounts** — `POST /api/accounts` for two accounts
6. **Deposit** — `POST /api/accounts/{number}/deposit?amount=10000`
7. **Transfer** — `POST /api/transactions/transfer` between accounts
8. **Verify** — `GET /api/accounts/{number}` confirms balances updated correctly

---

## Key Design Decisions

**JWT Stateless Auth** — No server-side sessions. Every request carries a self-contained signed token. The `JwtAuthFilter` validates the token on every request before it reaches any controller.

**@Transactional on transfers** — Fund transfers debit one account and credit another in a single atomic database transaction. If either operation fails, both roll back — money can never disappear mid-transfer.

**Layered architecture** — Controllers only handle HTTP. Business logic lives in Services. Database access lives in Repositories. Clean separation means any layer can be swapped independently.

**H2 in-memory database** — Chosen deliberately for the portfolio version. The JPA layer is database-agnostic — swapping to PostgreSQL or Oracle (from my FlexCube experience) requires only a single dependency and one config line change.

---

## What I Learned Building This

- Implementing stateless JWT authentication from scratch in Spring Security 6.x (the new lambda-style config, not the deprecated `WebSecurityConfigurerAdapter`)
- Why `@Transactional` is non-negotiable in financial systems — atomicity prevents data corruption
- Docker multi-stage builds to keep image size under 200MB
- Deploying a containerized Java app to AWS EC2 from scratch

---

## Background

This project was built to demonstrate production-style API development skills gained from:
- 14+ months working on **Oracle FlexCube Universal Banking System** at a FinTech company
- Current role as **DevOps + API Integration Engineer at Wipro**

The banking domain knowledge (account types, transaction flows, audit trails) comes from real-world exposure to core banking systems used by major Indian banks.

---

## Roadmap

- [ ] Switch to PostgreSQL for persistent storage
- [ ] Add rate limiting per account
- [ ] Add Spring Boot Actuator + Prometheus metrics
- [ ] Add Grafana dashboard for transaction monitoring
- [ ] Write unit tests with JUnit 5 + Mockito
- [ ] CI/CD pipeline with GitHub Actions

---

## Connect

**LinkedIn:** [your-linkedin-url]  
**Email:** [your-email]  
**Portfolio:** [your-portfolio-url]

---

*Part of a series of portfolio projects built while transitioning into freelance and startup engineering.*