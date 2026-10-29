# ADR 0001: PostgreSQL with Flyway migrations and Spring Data JPA

## Status
Accepted – 2026-10-29

## Context
The helpdesk stores relational data: tickets belong to users, categories, teams and SLA policies. Comments belong to tickets, attachments belong to comments. Agents are linked to teams through the `TeamMembership` association entity. These relations need foreign keys, constraints and transactions so a relational database is the natural fit. The assignment requires PostgreSQL or MySQL for the graded version, a schema managed by Flyway or Liquibase, and a managed free database in the deployed instance. The application will be deployed on Render with a Neon database.

## Decision
- PostgreSQL 17 as the database. Locally it runs in Docker (`docker-compose.yml`), so the local database has the same version as production and the project starts the same way on any machine.
- Flyway owns the schema. Every change is a versioned SQL file in `src\main\resources\db\migration` (V1, V2...) applied automatically on startup.
- Spring Data JPA with Hibernate for data access, with `ddl-auto: validate`: Hibernate only checks that the entities match the tables and never changes the schema.

## Alternatives considered
- MySQL – allowed by the assignment, but Neon (the free managed database I will
  deploy to) offers only PostgreSQL. PostgreSQL also has built-in full-text search,
  which is useful for searching ticket fields and comments (AR3).
- H2 only – convenient in the lectures, but not allowed for the graded version and
  it behaves differently from PostgreSQL, so some errors would only appear after
  deployment.
- Hibernate `ddl-auto: update` instead of migrations – keeps no history of schema
  changes, cannot be reviewed or reproduced reliably, and an auto-generated schema is
  not allowed in the final version.
- Liquibase instead of Flyway – also allowed, but Flyway migrations are plain SQL
  files that are easy to read and need almost no configuration, which suits a
  project of this size.

## Consequences
- The same database engine runs locally and in production, so behavior matches.
- The schema has a versioned, reproducible history that is visible in Git.
- A new developer (or the examiner) starts the database with one command:
  `docker compose up -d`.
- Every schema change needs a new migration file, a migration that has already been
  applied must never be edited.
- Docker must be running during local development.