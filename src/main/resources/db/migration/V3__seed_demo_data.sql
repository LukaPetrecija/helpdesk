-- =========================================================
-- Demo data for development and the milestone demos.
-- All demo users have the password "password" (BCrypt hash below).
-- =========================================================

-- Users: 1 admin, 2 agents, 3 requesters
INSERT INTO app_user (username, full_name, email, password_hash, role) VALUES
                                                                           ('petra', 'Petra Novak',   'petra.novak@helpdesk.test',   '$2a$10$vurK3NvHyXNI13S1dSA1p.NuQPlXyCeMaa.zHa1L9Ky6iSKKfTNAS', 'ADMIN'),
                                                                           ('marko', 'Marko Horvat',  'marko.horvat@helpdesk.test',  '$2a$10$vurK3NvHyXNI13S1dSA1p.NuQPlXyCeMaa.zHa1L9Ky6iSKKfTNAS', 'AGENT'),
                                                                           ('ivan',  'Ivan Babić',    'ivan.babic@helpdesk.test',    '$2a$10$vurK3NvHyXNI13S1dSA1p.NuQPlXyCeMaa.zHa1L9Ky6iSKKfTNAS', 'AGENT'),
                                                                           ('ana',   'Ana Kovač',     'ana.kovac@helpdesk.test',     '$2a$10$vurK3NvHyXNI13S1dSA1p.NuQPlXyCeMaa.zHa1L9Ky6iSKKfTNAS', 'USER'),
                                                                           ('maja',  'Maja Jurić',    'maja.juric@helpdesk.test',    '$2a$10$vurK3NvHyXNI13S1dSA1p.NuQPlXyCeMaa.zHa1L9Ky6iSKKfTNAS', 'USER'),
                                                                           ('tomo',  'Tomislav Perić','tomislav.peric@helpdesk.test','$2a$10$vurK3NvHyXNI13S1dSA1p.NuQPlXyCeMaa.zHa1L9Ky6iSKKfTNAS', 'USER');

-- Teams
INSERT INTO team (name, description) VALUES
                                         ('IT Support', 'First-line support: accounts, email, software'),
                                         ('Network',    'Network, VPN and connectivity'),
                                         ('Hardware',   'Laptops, printers and peripherals');

-- Team memberships (many-to-many with attributes)
INSERT INTO team_membership (user_id, team_id, team_role, joined_at) VALUES
                                                                         ((SELECT id FROM app_user WHERE username = 'marko'), (SELECT id FROM team WHERE name = 'IT Support'), 'LEAD',   DATE '2024-03-01'),
                                                                         ((SELECT id FROM app_user WHERE username = 'marko'), (SELECT id FROM team WHERE name = 'Hardware'),   'MEMBER', DATE '2025-01-15'),
                                                                         ((SELECT id FROM app_user WHERE username = 'ivan'),  (SELECT id FROM team WHERE name = 'Hardware'),   'LEAD',   DATE '2023-09-10'),
                                                                         ((SELECT id FROM app_user WHERE username = 'ivan'),  (SELECT id FROM team WHERE name = 'Network'),    'MEMBER', DATE '2024-06-01');

-- Categories ("Other" has no default team: the optional relation)
INSERT INTO category (name, description, default_team_id) VALUES
                                                              ('Email',    'Mailbox, Outlook, webmail',        (SELECT id FROM team WHERE name = 'IT Support')),
                                                              ('Access',   'Accounts, passwords, permissions', (SELECT id FROM team WHERE name = 'IT Support')),
                                                              ('Software', 'Installing and using applications',(SELECT id FROM team WHERE name = 'IT Support')),
                                                              ('Network',  'Wi-Fi, VPN, internet',             (SELECT id FROM team WHERE name = 'Network')),
                                                              ('Hardware', 'Laptops, printers, monitors',      (SELECT id FROM team WHERE name = 'Hardware')),
                                                              ('Other',    'Anything else',                    NULL);

-- Business calendar: Croatian working week
INSERT INTO business_calendar (name, time_zone, work_day_start, work_day_end, working_days) VALUES
    ('Croatia – standard', 'Europe/Zagreb', TIME '08:00', TIME '16:00', 'MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY');

-- Croatian public holidays 2026 and 2027
INSERT INTO holiday (name, holiday_date, business_calendar_id)
SELECT h.name, h.d, (SELECT id FROM business_calendar WHERE name = 'Croatia – standard')
FROM (VALUES
          ('New Year''s Day',             DATE '2026-01-01'),
          ('Epiphany',                    DATE '2026-01-06'),
          ('Easter Sunday',               DATE '2026-04-05'),
          ('Easter Monday',               DATE '2026-04-06'),
          ('Labour Day',                  DATE '2026-05-01'),
          ('Statehood Day',               DATE '2026-05-30'),
          ('Corpus Christi',              DATE '2026-06-04'),
          ('Anti-Fascist Struggle Day',   DATE '2026-06-22'),
          ('Victory and Homeland Thanksgiving Day', DATE '2026-08-05'),
          ('Assumption of Mary',          DATE '2026-08-15'),
          ('All Saints'' Day',            DATE '2026-11-01'),
          ('Remembrance Day',             DATE '2026-11-18'),
          ('Christmas Day',               DATE '2026-12-25'),
          ('St. Stephen''s Day',          DATE '2026-12-26'),
          ('New Year''s Day',             DATE '2027-01-01'),
          ('Epiphany',                    DATE '2027-01-06'),
          ('Easter Sunday',               DATE '2027-03-28'),
          ('Easter Monday',               DATE '2027-03-29'),
          ('Labour Day',                  DATE '2027-05-01'),
          ('Corpus Christi',              DATE '2027-05-27'),
          ('Statehood Day',               DATE '2027-05-30'),
          ('Anti-Fascist Struggle Day',   DATE '2027-06-22'),
          ('Victory and Homeland Thanksgiving Day', DATE '2027-08-05'),
          ('Assumption of Mary',          DATE '2027-08-15'),
          ('All Saints'' Day',            DATE '2027-11-01'),
          ('Remembrance Day',             DATE '2027-11-18'),
          ('Christmas Day',               DATE '2027-12-25'),
          ('St. Stephen''s Day',          DATE '2027-12-26')
     ) AS h(name, d);

-- SLA policies: one per priority (minutes of BUSINESS time)
INSERT INTO sla_policy (name, priority, first_response_minutes, resolution_minutes, business_calendar_id)
SELECT p.name, p.priority, p.fr, p.res, (SELECT id FROM business_calendar WHERE name = 'Croatia – standard')
FROM (VALUES
          ('Critical priority',    'CRITICAL',     30,  240),
          ('High priority',        'HIGH',        120,  480),
          ('Medium priority',      'MEDIUM',      240,  960),
          ('Low priority',         'LOW',         480, 2400),
          ('Information priority', 'INFORMATION', 960, 4800)
     ) AS p(name, priority, fr, res);

-- Tickets (due dates are placeholders until the business-hours calculation exists)
INSERT INTO ticket (title, description, status, priority, created_at,
                    first_response_due_at, resolution_due_at, resolved_at,
                    requester_id, assignee_id, category_id, team_id, sla_policy_id)
VALUES
    ('Can''t log in to Outlook',
     'Since this morning Outlook keeps asking for my password and rejects it. Webmail works. I need email for a client meeting at 15:00.',
     'OPEN', 'HIGH', now() - INTERVAL '3 hours', now() - INTERVAL '1 hour', now() + INTERVAL '5 hours', NULL,
     (SELECT id FROM app_user WHERE username = 'ana'), (SELECT id FROM app_user WHERE username = 'marko'),
     (SELECT id FROM category WHERE name = 'Email'), (SELECT id FROM team WHERE name = 'IT Support'),
     (SELECT id FROM sla_policy WHERE priority = 'HIGH')),

    ('VPN disconnects every hour',
     'When working from home the VPN drops roughly every 60 minutes and I have to reconnect.',
     'IN_PROGRESS', 'MEDIUM', now() - INTERVAL '1 day', now() - INTERVAL '20 hours', now() + INTERVAL '1 day', NULL,
     (SELECT id FROM app_user WHERE username = 'maja'), (SELECT id FROM app_user WHERE username = 'ivan'),
     (SELECT id FROM category WHERE name = 'Network'), (SELECT id FROM team WHERE name = 'Network'),
     (SELECT id FROM sla_policy WHERE priority = 'MEDIUM')),

    ('New laptop for new employee',
     'A new colleague starts on Monday in Accounting and needs a laptop with the standard software.',
     'NEW', 'LOW', now() - INTERVAL '2 hours', now() + INTERVAL '6 hours', now() + INTERVAL '5 days', NULL,
     (SELECT id FROM app_user WHERE username = 'tomo'), NULL,
     (SELECT id FROM category WHERE name = 'Hardware'), NULL,
     (SELECT id FROM sla_policy WHERE priority = 'LOW')),

    ('Printer on 2nd floor jammed',
     'The big printer next to the kitchen shows "paper jam" but there is no paper stuck inside.',
     'ON_HOLD', 'LOW', now() - INTERVAL '2 days', now() - INTERVAL '1 day', now() + INTERVAL '3 days', NULL,
     (SELECT id FROM app_user WHERE username = 'ana'), (SELECT id FROM app_user WHERE username = 'ivan'),
     (SELECT id FROM category WHERE name = 'Hardware'), (SELECT id FROM team WHERE name = 'Hardware'),
     (SELECT id FROM sla_policy WHERE priority = 'LOW')),

    ('Cannot access shared drive',
     'The S: drive with all department documents is not visible since the update last night. The whole team is blocked.',
     'OPEN', 'CRITICAL', now() - INTERVAL '30 minutes', now(), now() + INTERVAL '3 hours', NULL,
     (SELECT id FROM app_user WHERE username = 'maja'), (SELECT id FROM app_user WHERE username = 'marko'),
     (SELECT id FROM category WHERE name = 'Access'), (SELECT id FROM team WHERE name = 'IT Support'),
     (SELECT id FROM sla_policy WHERE priority = 'CRITICAL')),

    ('Install Visual Studio Code',
     'Please install VS Code on my laptop, I need it for editing configuration files.',
     'RESOLVED', 'INFORMATION', now() - INTERVAL '5 days', now() - INTERVAL '4 days', now() - INTERVAL '1 day', now() - INTERVAL '3 days',
     (SELECT id FROM app_user WHERE username = 'tomo'), (SELECT id FROM app_user WHERE username = 'marko'),
     (SELECT id FROM category WHERE name = 'Software'), (SELECT id FROM team WHERE name = 'IT Support'),
     (SELECT id FROM sla_policy WHERE priority = 'INFORMATION')),

    ('Outlook password rejected after sync',
     'After the weekend Outlook rejected my password although it was correct.',
     'CLOSED', 'HIGH', now() - INTERVAL '30 days', now() - INTERVAL '30 days' + INTERVAL '2 hours', now() - INTERVAL '29 days', now() - INTERVAL '29 days',
     (SELECT id FROM app_user WHERE username = 'maja'), (SELECT id FROM app_user WHERE username = 'marko'),
     (SELECT id FROM category WHERE name = 'Email'), (SELECT id FROM team WHERE name = 'IT Support'),
     (SELECT id FROM sla_policy WHERE priority = 'HIGH')),

    ('Question about the new expense form',
     'Where can I find the new travel expense form?',
     'NEW', 'INFORMATION', now() - INTERVAL '10 minutes', now() + INTERVAL '2 days', now() + INTERVAL '10 days', NULL,
     (SELECT id FROM app_user WHERE username = 'ana'), NULL,
     (SELECT id FROM category WHERE name = 'Other'), NULL,
     (SELECT id FROM sla_policy WHERE priority = 'INFORMATION'));

-- Comments (one internal note)
INSERT INTO comment (body, internal, created_at, ticket_id, user_id) VALUES
                                                                         ('It also happens on my phone.', FALSE, now() - INTERVAL '170 minutes',
                                                                          (SELECT id FROM ticket WHERE title = 'Can''t log in to Outlook'), (SELECT id FROM app_user WHERE username = 'ana')),
                                                                         ('Please try resetting your password at the self-service portal and let me know.', FALSE, now() - INTERVAL '140 minutes',
                                                                          (SELECT id FROM ticket WHERE title = 'Can''t log in to Outlook'), (SELECT id FROM app_user WHERE username = 'marko')),
                                                                         ('Possibly linked to last night''s AD sync, check with Network.', TRUE, now() - INTERVAL '138 minutes',
                                                                          (SELECT id FROM ticket WHERE title = 'Can''t log in to Outlook'), (SELECT id FROM app_user WHERE username = 'marko')),
                                                                         ('Can you tell me which VPN client version you have installed?', FALSE, now() - INTERVAL '20 hours',
                                                                          (SELECT id FROM ticket WHERE title = 'VPN disconnects every hour'), (SELECT id FROM app_user WHERE username = 'ivan')),
                                                                         ('Version 5.2.1, installed by IT last year.', FALSE, now() - INTERVAL '19 hours',
                                                                          (SELECT id FROM ticket WHERE title = 'VPN disconnects every hour'), (SELECT id FROM app_user WHERE username = 'maja')),
                                                                         ('Waiting for a spare part from the supplier.', FALSE, now() - INTERVAL '1 day',
                                                                          (SELECT id FROM ticket WHERE title = 'Printer on 2nd floor jammed'), (SELECT id FROM app_user WHERE username = 'ivan')),
                                                                         ('Installed, please restart your laptop.', FALSE, now() - INTERVAL '3 days',
                                                                          (SELECT id FROM ticket WHERE title = 'Install Visual Studio Code'), (SELECT id FROM app_user WHERE username = 'marko'));

-- One escalation on the critical ticket
INSERT INTO escalation_event (level, reason, triggered_at, ticket_id) VALUES
    (1, 'First response due in 10 minutes, no agent reply yet', now() - INTERVAL '10 minutes',
     (SELECT id FROM ticket WHERE title = 'Cannot access shared drive'));