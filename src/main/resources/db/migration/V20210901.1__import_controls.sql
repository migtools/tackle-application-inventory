create table business_service
(
    id          int8 not null,
    description varchar(255),
    name        varchar(255),
    createUser  varchar(255),
    createTime  timestamp,
    owner_id    int8,
    deleted     boolean,
    updateTime  timestamp,
    updateUser  varchar(255),
    primary key (id)
);

create table stakeholder
(
    id             int8 not null,
    createTime     timestamp,
    createUser     varchar(255),
    deleted        boolean,
    updateTime     timestamp,
    updateUser     varchar(255),
    email          varchar(255),
    displayName    varchar(255),
    jobFunction_id int8,
    primary key (id)
);

create table stakeholder_group
(
    id          int8 not null,
    createTime  timestamp,
    createUser  varchar(255),
    deleted     boolean,
    updateTime  timestamp,
    updateUser  varchar(255),
    description varchar(255),
    members     int4,
    name        varchar(255),
    primary key (id)
);

create table stakeholdergroup_stakeholders
(
    group_id       int8 not null,
    stakeholder_id int8 not null
);

create table job_function
(
    id         int8 not null,
    createTime timestamp,
    createUser varchar(255),
    deleted    boolean,
    updateTime timestamp,
    updateUser varchar(255),
    role       varchar(255),
    primary key (id)
);

create table tag_type
(
    id         int8 not null,
    createTime timestamp,
    createUser varchar(255),
    deleted    boolean,
    updateTime timestamp,
    updateUser varchar(255),
    colour     varchar(255),
    name       varchar(255),
    rank       int4,
    primary key (id)
);

create table tag
(
    id         int8 not null,
    createTime timestamp,
    createUser varchar(255),
    deleted    boolean,
    updateTime timestamp,
    updateUser varchar(255),
    name       varchar(255),
    tagType_id int8 not null,
    primary key (id)
);

-- FOREIGN  keys
alter table if exists business_service
    add constraint FK64f0d7vxcp60lyjlttb7dqk3t
    foreign key (owner_id)
    references stakeholder;

alter table if exists stakeholder
    add constraint FK298p7tiy7e6pljwoxiwn9tjx2
    foreign key (jobFunction_id)
    references job_function;

alter table if exists stakeholdergroup_stakeholders
    add constraint FKrvendnekhlrq2hggkwdkxg4j0
    foreign key (stakeholder_id)
    references stakeholder;
alter table if exists stakeholdergroup_stakeholders
    add constraint FKmklbx86c9ehi6njo6d37nud50
    foreign key (group_id)
    references stakeholder_group;

alter table if exists tag
    add constraint FKq664i9aw1wjdlptk0q5dapim7
    foreign key (tagType_id)
    references tag_type;

-- UNIQUE indexes
create
unique INDEX UKanFYz2R9rvRpRVWscg5uKqQOkJxZ2M
on stakeholder (email)
where (deleted = false);

create
unique INDEX UKG6ei7H1djG0d5FhuGndHzs3xUadjQa
on stakeholder_group (name)
where (deleted = false);

create
unique INDEX UKanFYz2R9rvRpRVWscg5uKqQOkJxZ2N
on job_function (role)
where (deleted = false);

create
unique INDEX UKanFYz2R9rvRpRVWscg5uKqQOkJxZ2O
on business_service (name)
where (deleted = false);

create
unique INDEX UKanFYz2R9rvRpRVWscg5uKqQOkJxZ2P
on tag_type (name)
where (deleted = false);

create
unique INDEX UKanFYz2R9rvRpRVWscg5uKqQOkJxZ2Q
on tag (name, tagType_id)
where (deleted = false);

-- JOB_FUNCTION pre-defined data
INSERT INTO job_function (id, role, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Business Analyst', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
INSERT INTO job_function (id, role, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Business Service Owner / Manager', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
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

-- TAG_TYPE pre-defined data
INSERT INTO tag_type (id, name, rank, colour, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Application Type', 6, '#ec7a08', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
INSERT INTO tag_type (id, name, rank, colour, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Data Center', 5, '#2b9af3', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
INSERT INTO tag_type (id, name, rank, colour, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Database', 4, '#6ec664', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
INSERT INTO tag_type (id, name, rank, colour, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Language', 1, '#009596', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
INSERT INTO tag_type (id, name, rank, colour, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Operating System', 2, '#a18fff', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);
INSERT INTO tag_type (id, name, rank, colour, createUser, createTime, updateUser, updateTime, deleted) VALUES (nextval('hibernate_sequence'), 'Runtime', 3, '#7d1007', '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false);

-- TAG pre-defined data
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'COTS', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Application Type';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'In house', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Application Type';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'SaaS', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Application Type';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'Boston (USA)', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Data Center';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'London (UK)', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Data Center';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'Paris (FR)', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Data Center';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'Sydney (AU)', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Data Center';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'DB2', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Database';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'MongoDB', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Database';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'Oracle', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Database';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'Postgresql', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Database';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'SQL Server', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Database';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'C# ASP .Net', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Language';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'C++', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Language';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'COBOL', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Language';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'Java', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Language';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'Javascript', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Language';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'Python', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Language';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'RHEL 8', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Operating System';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'Windows Server 2016', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Operating System';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'Z/OS', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Operating System';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'EAP', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Runtime';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'JWS', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Runtime';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'Quarkus', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Runtime';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'Spring Boot', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Runtime';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'Tomcat', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Runtime';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'WebLogic', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Runtime';
INSERT INTO tag (id, name, tagType_id, createUser, createTime, updateUser, updateTime, deleted)
SELECT nextval('hibernate_sequence'), 'WebSphere', id, '<shipped-data>', CURRENT_TIMESTAMP, '<shipped-data>', CURRENT_TIMESTAMP, false
FROM tag_type
WHERE name = 'Runtime';
