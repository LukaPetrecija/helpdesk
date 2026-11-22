# Helpdesk – P12 Helpdesk and issue tracking

Individual project for *Rapid Development of Java Applications Using Programming
Frameworks* (Algebra Bernays University, 2026/27).

Users raise tickets, agents work them under service-level agreements (SLA).
SLA deadlines are calculated in business hours using a configurable calendar of
working days and holidays, an escalation job acts before a deadline is breached,
and an AI feature suggests a category, a priority and similar resolved tickets.

## Current status – Milestone 2
- **Persistence layer:** 11 JPA entities over PostgreSQL, Spring Data repositories,
  schema managed by Flyway (`V1` users, `V2` all domain tables with constraints and
  indexes, `V3` seed data).
- **Parent–child CRUD on the screens:** tickets (parent) and comments (child) can be
  listed, viewed, created, edited and deleted, with Bean Validation, POST-redirect-GET
  and flash messages.
- Domain model: [docs/domain-model.md](docs/domain-model.md)
- Architecture decisions: [ADR 0001 – persistence model](docs/adr/0001-persistence-model.md)
- Low-fidelity wireframes: [Figma – Helpdesk wireframes](https://www.figma.com/design/PtoVRKd0L7lSsQ2Y2iKt8y/Helpdesk)

### Seed data
Flyway `V3` creates demo data: 6 users (1 admin, 2 agents, 3 requesters), 3 teams,
6 categories, a Croatian business calendar with the 2026–2027 public holidays,
5 SLA policies (one per priority), 8 tickets and 7 comments.
All demo users have the password `password` (login arrives in Milestone 3).

### Screens
| URL | Screen | Template |
|---|---|---|
| `/tickets` | Ticket list | `tickets/list.html` |
| `/tickets/{id}` | Ticket detail with comments (add / edit / delete) | `tickets/detail.html` |
| `/tickets/new` | New ticket | `tickets/form.html` |
| `/tickets/{id}/edit` | Edit ticket (status, assignee, priority…) | `tickets/form.html` |
| `/tickets/{id}/delete` | Delete confirmation | `tickets/confirm-delete.html` |
| `/tickets/{id}/comments/{commentId}/edit` | Edit comment | `comments/form.html` |

### Figma → Thymeleaf mapping
| Figma frame | Template | Deviation and reason |
|---|---|---|
| 01 Ticket list | `tickets/list.html` | Search, filters and pagination not implemented yet (AR3, planned for M3/M4). |
| 02 Ticket details | `tickets/detail.html` | Edit/Delete buttons instead of "Assign to me" / "Change status" (assignment and status are changed in the edit form until roles exist in M3). |
| 03 New ticket | `tickets/form.html` | Requester is chosen from a drop-down until login exists (M3); no attachment upload yet. |
| 04 Form with validation errors | `tickets/form.html` | As designed. |
| 05 Confirmation (flash message) | flash message in `fragments/layout.html` | The flash is shown on the new ticket's detail page instead of the list. |
| 06 AI suggestion | – | AI feature planned for M3. |
| – | `tickets/confirm-delete.html`, `comments/form.html` | Not in the wireframes; added for safe deletion and comment editing. |

## Known limitations (to be addressed)
- SLA due dates currently add clock minutes; business-hours calculation with the
  calendar and holidays (AR1) is planned.
- No authentication yet: requester and comment author are selected manually.
- The header language switch (HR/EN) from the wireframes comes with i18n (LO7).

## Next steps (Milestone 3)
- Spring Security with the three roles (USER, AGENT, ADMIN), BCrypt, method security
- REST API for tickets and comments with OpenAPI documentation
- AI feature (category/priority suggestion, similar tickets) behind a port with a stub
- Document the N+1 fix (`@EntityGraph` on the ticket list) with before/after SQL
