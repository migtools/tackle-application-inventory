create table bulk_copy_review
(
    id         int8 not null,

    createTime timestamp,
    createUser varchar(255),
    deleted    boolean,
    updateTime timestamp,
    updateUser varchar(255),

    completed  boolean,
    review_id  int8 not null,
    primary key (id)
);

create table bulk_copy_review_details
(
    id                  int8 not null,

    createTime          timestamp,
    createUser          varchar(255),
    deleted             boolean,
    updateTime          timestamp,
    updateUser          varchar(255),

    bulk_copy_review_id int8 not null,
    application_id      int8 not null,
    primary key (id)
);

alter table if exists bulk_copy_review_details
    add constraint fk_bulk_copy_review_details_application
    foreign key (application_id)
    references application;
