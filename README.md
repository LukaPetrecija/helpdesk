# Helpdesk – P12 Helpdesk and issue tracking

Individual project for *Rapid Development of Java Applications Using Programming
Frameworks* (Algebra Bernays University, 2026/27).

Users raise tickets, agents work them under service-level agreements (SLA).
SLA deadlines are calculated in business hours using a configurable calendar of
working days and holidays, an escalation job acts before a deadline is breached,
and an AI feature suggests a category, a priority and similar resolved tickets.

## Current status – Milestone 1
- Domain model: [docs/domain-model.md](docs/domain-model.md)
- Architecture decisions: [ADR 0001 – persistence model](docs/adr/0001-persistence-model.md)
- Low-fidelity wireframes (Figma): [https://www.figma.com/design/PtoVRKd0L7lSsQ2Y2iKt8y/Helpdesk?node-id=10-2118&t=NU7t3cRscPU9Pe1x-1](https://www.figma.com/design/PtoVRKd0L7lSsQ2Y2iKt8y/Helpdesk?node-id=10-2118&t=NU7t3cRscPU9Pe1x-1)
- Spring Boot application running against PostgreSQL in Docker, schema managed by Flyway

## Technology choices
| Area | Choice |
|---|---|
| Language / framework | Java 25, Spring Boot 4.1.2 |
| Web layer | Spring MVC + Thymeleaf |
| Database | PostgreSQL 17 (Docker locally), schema via Flyway |
| ORM | Spring Data JPA / Hibernate 7 |
| **Hosting (planned)** | **Render** (web service from Dockerfile) + **Neon** (managed PostgreSQL) |
| **AI provider (planned)** | **Google Gemini API** – chat model for category/priority suggestion, Gemini Embedding for finding similar resolved tickets |

## How to run locally
Requirements: JDK 25, Docker Desktop.

1. Start the database: `docker compose up -d`
2. Start the application (IntelliJ: run `HelpdeskApplication`, or): `./mvnw spring-boot:run` (Windows: `mvnw spring-boot:run`)
3. Open http://localhost:8080 – health check at http://localhost:8080/actuator/health

## Next steps (Milestone 2)
- Flyway migrations for the full domain model and seed data
- JPA entities and repositories, parent-child CRUD screens for tickets
- High-fidelity Figma screens and component set
