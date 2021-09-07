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

-- Controls data
INSERT INTO job_function (id, role, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Business Analyst', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
INSERT INTO job_function (id, role, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Business Service Owner / Manager', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
-- INSERT INTO stakeholder (id, displayName, email, jobFunction_id, createUser, createTime, deleted) VALUES (nextval('hibernate_sequence'), 'Jessica Fletcher', 'jbfletcher@murdershewrote.com', 1, 'mrizzi', CURRENT_TIMESTAMP, false);
-- INSERT INTO stakeholder (id, displayName, email, jobFunction_id, createUser, createTime, deleted) VALUES (nextval('hibernate_sequence'), 'Emmett Brown', 'doc@greatscott.movie', 2, 'mrizzi', CURRENT_TIMESTAMP, false);
-- INSERT INTO business_service (id, name, description, owner_id, createUser, createTime, deleted) VALUES (nextval('hibernate_sequence'), 'Home Banking BU', 'Important service to let private customer use their home banking accounts', 3, 'mrizzi', CURRENT_TIMESTAMP, false);
-- INSERT INTO business_service (id, name, description, owner_id, createUser, createTime, deleted) VALUES (nextval('hibernate_sequence'), 'Online Investments service', 'Corporate customers investments management', 4, 'foo', CURRENT_TIMESTAMP, false);
-- INSERT INTO business_service (id, name, description, deleted) VALUES (nextval('hibernate_sequence'), 'Credit Cards BS', 'Internal credit card creation and management service', false);
INSERT INTO job_function (id, role, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Consultant', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
INSERT INTO job_function (id, role, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'DBA', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
INSERT INTO job_function (id, role, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Developer / Software Engineer', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
INSERT INTO job_function (id, role, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'IT Operations', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
INSERT INTO job_function (id, role, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Program Manager', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
INSERT INTO job_function (id, role, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Project Manager', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
INSERT INTO job_function (id, role, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Service Owner', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
INSERT INTO job_function (id, role, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Solution Architect', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
INSERT INTO job_function (id, role, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'System Administrator', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
INSERT INTO job_function (id, role, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Test Analyst / Manager', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
INSERT INTO tag_type (id, name, rank, colour, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Runtime', 1, '#123456', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
INSERT INTO tag_type (id, name, rank, colour, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Operating System', 3, '#111111', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
INSERT INTO tag_type (id, name, rank, colour, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Data Center', 101, '#999999', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
-- INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'EAP', 18, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
-- INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'JWS', 18, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
-- INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Quarkus', 18, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
-- INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Spring Boot', 18, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
-- INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'RHEL 8', 19, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
-- INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Windows Server 2016', 19, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
INSERT INTO stakeholder_group (id, name, description, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Managers', 'Managers Group', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
INSERT INTO stakeholder_group (id, name, description, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Engineers', 'Engineers Group', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
-- INSERT INTO stakeholdergroup_stakeholders (group_id, stakeholder_id) VALUES (27, 4);
-- INSERT INTO stakeholdergroup_stakeholders (group_id, stakeholder_id) VALUES (28, 3);
