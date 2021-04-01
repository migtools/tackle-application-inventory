create table applications_dependency (
    id int8 not null,
    createTime timestamp,
    createUser varchar(255),
    deleted boolean,
    updateTime timestamp,
    updateUser varchar(255),
    from_id int8,
    to_id int8,
    primary key (id)
);
alter table if exists applications_dependency
    add constraint UKlgwufxhgndoa6198mawite3fo unique (from_id, to_id);
alter table if exists applications_dependency
    add constraint FKfwaxbxio04nk2gj90ssf8p82f
    foreign key (from_id)
    references application;
alter table if exists applications_dependency
    add constraint FKlracllg4jlktabdvo2mp41hhj
    foreign key (to_id)
    references application;
