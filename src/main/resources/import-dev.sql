INSERT INTO application (id, name, description, deleted) VALUES (nextval('hibernate_sequence'), 'Home Banking BU', 'Important service to let private customer use their home banking accounts', false);
INSERT INTO application (id, name, description, deleted) VALUES (nextval('hibernate_sequence'), 'Online Investments service', 'Corporate customers investments management', false);
INSERT INTO application (id, name, description, deleted) VALUES (nextval('hibernate_sequence'), 'Credit Cards BS', 'Internal credit card creation and management service', false);
INSERT INTO application (id, name, description, deleted) VALUES (nextval('hibernate_sequence'), 'App 4', 'Important service to let private customer use their home banking accounts', false);
INSERT INTO application (id, name, description, deleted) VALUES (nextval('hibernate_sequence'), 'App 5', 'Corporate customers investments management', false);
INSERT INTO application (id, name, description, deleted) VALUES (nextval('hibernate_sequence'), 'App 6', 'Internal credit card creation and management service', false);
INSERT INTO application (id, name, description, deleted) VALUES (nextval('hibernate_sequence'), 'App 7', 'Important service to let private customer use their home banking accounts', false);
INSERT INTO application (id, name, description, deleted) VALUES (nextval('hibernate_sequence'), 'App 8', 'Corporate customers investments management', false);
INSERT INTO application (id, name, description, deleted) VALUES (nextval('hibernate_sequence'), 'App 9', 'Internal credit card creation and management service', false);



INSERT INTO applications_dependency (id, from_id, to_id, deleted) VALUES (nextval('hibernate_sequence'), 1, 2, false);
INSERT INTO applications_dependency (id, from_id, to_id, deleted) VALUES (nextval('hibernate_sequence'), 1, 3, false);
INSERT INTO applications_dependency (id, from_id, to_id, deleted) VALUES (nextval('hibernate_sequence'), 1, 6, false);
INSERT INTO applications_dependency (id, from_id, to_id, deleted) VALUES (nextval('hibernate_sequence'), 2, 5, false);
INSERT INTO applications_dependency (id, from_id, to_id, deleted) VALUES (nextval('hibernate_sequence'), 3, 2, false);
INSERT INTO applications_dependency (id, from_id, to_id, deleted) VALUES (nextval('hibernate_sequence'), 3, 5, false);
INSERT INTO applications_dependency (id, from_id, to_id, deleted) VALUES (nextval('hibernate_sequence'), 5, 7, false);
INSERT INTO applications_dependency (id, from_id, to_id, deleted) VALUES (nextval('hibernate_sequence'), 5, 8, false);
INSERT INTO applications_dependency (id, from_id, to_id, deleted) VALUES (nextval('hibernate_sequence'), 6, 8, false);
INSERT INTO applications_dependency (id, from_id, to_id, deleted) VALUES (nextval('hibernate_sequence'), 6, 9, false);
INSERT INTO applications_dependency (id, from_id, to_id, deleted) VALUES (nextval('hibernate_sequence'), 7, 9, false);

-- REVIEWS
INSERT INTO review (id, businesscriticality, comments, effortestimate, proposedaction, workpriority, application_id, deleted) VALUES (nextval('hibernate_sequence'), 1, 'first comment', 'Small', 'Rehost', 2, 1, false);
INSERT INTO review (id, businesscriticality, comments, effortestimate, proposedaction, workpriority, application_id, deleted) VALUES (nextval('hibernate_sequence'), 1, 'other comment', 'Small', 'Replatform', 1, 2, false);
INSERT INTO review (id, businesscriticality, comments, effortestimate, proposedaction, workpriority, application_id, deleted) VALUES (nextval('hibernate_sequence'), 1, 'another comment', 'Medium', 'Refactor', 1, 3, false);
INSERT INTO review (id, businesscriticality, comments, effortestimate, proposedaction, workpriority, application_id, deleted) VALUES (nextval('hibernate_sequence'), 1, 'first comment', 'Medium', 'Rehost', 2, 4, false);
INSERT INTO review (id, businesscriticality, comments, effortestimate, proposedaction, workpriority, application_id, deleted) VALUES (nextval('hibernate_sequence'), 1, 'other comment', 'Large', 'Replatform', 5, 5, false);
INSERT INTO review (id, businesscriticality, comments, effortestimate, proposedaction, workpriority, application_id, deleted) VALUES (nextval('hibernate_sequence'), 1, 'another comment', 'Large', 'Refactor', 6, 6, false);
INSERT INTO review (id, businesscriticality, comments, effortestimate, proposedaction, workpriority, application_id, deleted) VALUES (nextval('hibernate_sequence'), 1, 'first comment', 'XLarge', 'Rehost', 2, 7, false);
INSERT INTO review (id, businesscriticality, comments, effortestimate, proposedaction, workpriority, application_id, deleted) VALUES (nextval('hibernate_sequence'), 1, 'other comment', 'XLarge', 'Replatform', 8, 8, false);
INSERT INTO review (id, businesscriticality, comments, effortestimate, proposedaction, workpriority, application_id, deleted) VALUES (nextval('hibernate_sequence'), 1, 'another comment', 'XLarge', 'Refactor', 9, 9, false);
