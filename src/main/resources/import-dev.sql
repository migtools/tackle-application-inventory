INSERT INTO application (id, name, description, deleted) VALUES (nextval('hibernate_sequence'), 'Home Banking BU', 'Important service to let private customer use their home banking accounts', false);
INSERT INTO application (id, name, description, deleted) VALUES (nextval('hibernate_sequence'), 'Online Investments service', 'Corporate customers investments management', false);
INSERT INTO application (id, name, description, deleted) VALUES (nextval('hibernate_sequence'), 'Credit Cards BS', 'Internal credit card creation and management service', false);
INSERT INTO applications_dependency (id, from_id, to_id, deleted) VALUES (nextval('hibernate_sequence'), 1, 2, false);
INSERT INTO applications_dependency (id, from_id, to_id, deleted) VALUES (nextval('hibernate_sequence'), 1, 3, false);
INSERT INTO applications_dependency (id, from_id, to_id, deleted) VALUES (nextval('hibernate_sequence'), 3, 2, false);

-- REVIEWS
INSERT INTO review
    (id, businesscriticality, comments, effortestimate, proposedaction, workpriority, application_id, deleted)
VALUES
   (nextval('hibernate_sequence'), 1, 'first comment', 1, 'Rehost', 2, 1, false);

INSERT INTO review
    (id, businesscriticality, comments, effortestimate, proposedaction, workpriority, application_id, deleted)
VALUES
   (nextval('hibernate_sequence'), 1, 'other comment', 2, 'Replatform', 1, 2, false);

INSERT INTO review
    (id, businesscriticality, comments, effortestimate, proposedaction, workpriority, application_id, deleted)
VALUES
   (nextval('hibernate_sequence'), 1, 'another comment', 3, 'Refactor', 1, 3, false);

