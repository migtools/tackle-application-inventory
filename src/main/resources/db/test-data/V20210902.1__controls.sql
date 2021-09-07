INSERT INTO business_service (id, name, description, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Home Banking BU', 'Important service to let private customer use their home banking accounts', 'mrizzi', '2019-01-01 00:00:00.407', 'mrizzi', CURRENT_TIMESTAMP, false);
INSERT INTO business_service (id, name, description, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Online Investments service', 'Corporate customers investments management', 'foo', '2020-01-01 00:00:00', 'mrizzi', CURRENT_TIMESTAMP, false);
INSERT INTO business_service (id, name, description, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Credit Cards BS', 'Internal credit card creation and management service', 'foo', '2021-01-01 00:00:00', null, null, false);

INSERT INTO stakeholder (id, displayName, email, createUser, createTime, deleted) VALUES (nextval('hibernate_sequence'), 'Jessica Fletcher', 'jbfletcher@murdershewrote.series', 'mrizzi', CURRENT_TIMESTAMP, false);
INSERT INTO stakeholder (id, displayName, email, createUser, createTime, deleted) VALUES (nextval('hibernate_sequence'), 'Emmett Brown', 'doc@greatscott.movie', 'mrizzi', CURRENT_TIMESTAMP, false);
UPDATE business_service SET owner_id = 59, updateTime = CURRENT_TIMESTAMP WHERE id = 56;
UPDATE business_service SET owner_id = 60, updateTime = CURRENT_TIMESTAMP WHERE id = 57;

UPDATE stakeholder SET jobFunction_id = 10 WHERE id = 59;
UPDATE stakeholder SET jobFunction_id = 21 WHERE id = 60;

INSERT INTO stakeholder_group (id, name, description, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Managers', 'Managers Group', '<pre-filled>', CURRENT_TIMESTAMP, '<pre-filled>', CURRENT_TIMESTAMP, false);
INSERT INTO stakeholder_group (id, name, description, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Engineers', 'Engineers Group', '<pre-filled>', CURRENT_TIMESTAMP, '<pre-filled>', CURRENT_TIMESTAMP, false);
INSERT INTO stakeholder_group (id, name, description, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Marketing', 'Marketing Group', '<pre-filled>', CURRENT_TIMESTAMP, '<pre-filled>', CURRENT_TIMESTAMP, false);

-- INSERT INTO stakeholdergroup_stakeholders (group_id, stakeholder_id) VALUES (62, 4);
-- INSERT INTO stakeholdergroup_stakeholders (group_id, stakeholder_id) VALUES (52, 5);
-- INSERT INTO stakeholdergroup_stakeholders (group_id, stakeholder_id) VALUES (53, 5);
