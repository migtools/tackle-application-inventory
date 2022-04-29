--
-- Copyright © 2021 the Konveyor Contributors (https://konveyor.io/)
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

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
INSERT INTO review (id, businesscriticality, comments, effortestimate, proposedaction, workpriority, application_id, deleted) VALUES (nextval('hibernate_sequence'), 1, 'first comment', 'Extra_Large', 'Rehost', 2, 7, false);
INSERT INTO review (id, businesscriticality, comments, effortestimate, proposedaction, workpriority, application_id, deleted) VALUES (nextval('hibernate_sequence'), 1, 'other comment', 'Extra_Large', 'Replatform', 8, 8, false);
INSERT INTO review (id, businesscriticality, comments, effortestimate, proposedaction, workpriority, application_id, deleted) VALUES (nextval('hibernate_sequence'), 1, 'another comment', 'Extra_Large', 'Refactor', 9, 9, false);
