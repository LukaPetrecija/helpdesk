-- Teams and membership (Agent ↔ Team through TeamMembership)
CREATE TABLE team (
                      id          BIGSERIAL PRIMARY KEY,
                      name        VARCHAR(100) NOT NULL UNIQUE,
                      description VARCHAR(500)
);

CREATE TABLE team_membership (
                                 id        BIGSERIAL PRIMARY KEY,
                                 user_id   BIGINT      NOT NULL REFERENCES app_user (id),
                                 team_id   BIGINT      NOT NULL REFERENCES team (id),
                                 team_role VARCHAR(20) NOT NULL CHECK (team_role IN ('LEAD', 'MEMBER')),
                                 joined_at DATE        NOT NULL DEFAULT CURRENT_DATE,
                                 CONSTRAINT uq_membership_user_team UNIQUE (user_id, team_id)
);

-- Categories (optional default team)
CREATE TABLE category (
                          id              BIGSERIAL PRIMARY KEY,
                          name            VARCHAR(100) NOT NULL UNIQUE,
                          description     VARCHAR(500),
                          default_team_id BIGINT REFERENCES team (id)
);

-- Business calendar + holidays (AR1)
CREATE TABLE business_calendar (
                                   id             BIGSERIAL PRIMARY KEY,
                                   name           VARCHAR(100) NOT NULL UNIQUE,
                                   time_zone      VARCHAR(50)  NOT NULL,
                                   work_day_start TIME         NOT NULL,
                                   work_day_end   TIME         NOT NULL,
                                   working_days   VARCHAR(50)  NOT NULL,   -- e.g. 'MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY'
                                   CONSTRAINT ck_calendar_hours CHECK (work_day_end > work_day_start)
);

CREATE TABLE holiday (
                         id                   BIGSERIAL PRIMARY KEY,
                         name                 VARCHAR(100) NOT NULL,
                         holiday_date         DATE         NOT NULL,
                         business_calendar_id BIGINT       NOT NULL REFERENCES business_calendar (id) ON DELETE CASCADE,
                         CONSTRAINT uq_holiday_calendar_date UNIQUE (business_calendar_id, holiday_date)
);

-- SLA policies
CREATE TABLE sla_policy (
                            id                     BIGSERIAL PRIMARY KEY,
                            name                   VARCHAR(100) NOT NULL UNIQUE,
                            priority               VARCHAR(20)  NOT NULL
                                CHECK (priority IN ('INFORMATION', 'LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
                            first_response_minutes INT          NOT NULL CHECK (first_response_minutes > 0),
                            resolution_minutes     INT          NOT NULL CHECK (resolution_minutes > 0),
                            business_calendar_id   BIGINT       NOT NULL REFERENCES business_calendar (id)
);

-- Tickets (the parent of the aggregate)
CREATE TABLE ticket (
                        id                    BIGSERIAL PRIMARY KEY,
                        title                 VARCHAR(200) NOT NULL,
                        description           TEXT         NOT NULL,
                        status                VARCHAR(20)  NOT NULL
                            CHECK (status IN ('NEW', 'OPEN', 'IN_PROGRESS', 'ON_HOLD', 'RESOLVED', 'CLOSED')),
                        priority              VARCHAR(20)  NOT NULL
                            CHECK (priority IN ('INFORMATION', 'LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
                        created_at            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        resolved_at           TIMESTAMP,
                        first_response_due_at TIMESTAMP,
                        resolution_due_at     TIMESTAMP,
                        requester_id          BIGINT       NOT NULL REFERENCES app_user (id),
                        assignee_id           BIGINT       REFERENCES app_user (id),   -- optional
                        category_id           BIGINT       NOT NULL REFERENCES category (id),
                        team_id               BIGINT       REFERENCES team (id),       -- optional
                        sla_policy_id         BIGINT       NOT NULL REFERENCES sla_policy (id)
);

-- Comments (the child) and attachments
CREATE TABLE comment (
                         id         BIGSERIAL PRIMARY KEY,
                         body       TEXT      NOT NULL,
                         internal   BOOLEAN   NOT NULL DEFAULT FALSE,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         edited_at  TIMESTAMP,
                         ticket_id  BIGINT    NOT NULL REFERENCES ticket (id) ON DELETE CASCADE,
                         user_id    BIGINT    NOT NULL REFERENCES app_user (id)
);

CREATE TABLE attachment (
                            id           BIGSERIAL PRIMARY KEY,
                            file_name    VARCHAR(255) NOT NULL,
                            content_type VARCHAR(100) NOT NULL,
                            size_bytes   BIGINT       NOT NULL CHECK (size_bytes >= 0),
                            path         VARCHAR(500) NOT NULL,
                            uploaded_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            comment_id   BIGINT       NOT NULL REFERENCES comment (id) ON DELETE CASCADE
);

-- Escalations (AR2)
CREATE TABLE escalation_event (
                                  id           BIGSERIAL PRIMARY KEY,
                                  level        INT          NOT NULL CHECK (level > 0),
                                  reason       VARCHAR(500) NOT NULL,
                                  triggered_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  ticket_id    BIGINT       NOT NULL REFERENCES ticket (id) ON DELETE CASCADE,
                                  CONSTRAINT uq_escalation_ticket_level UNIQUE (ticket_id, level)
);

-- Indexes on foreign keys and common filters
CREATE INDEX ix_ticket_requester ON ticket (requester_id);
CREATE INDEX ix_ticket_assignee  ON ticket (assignee_id);
CREATE INDEX ix_ticket_status    ON ticket (status);
CREATE INDEX ix_comment_ticket   ON comment (ticket_id);
CREATE INDEX ix_attachment_comment ON attachment (comment_id);
CREATE INDEX ix_membership_team  ON team_membership (team_id);