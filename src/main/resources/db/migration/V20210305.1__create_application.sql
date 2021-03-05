create sequence hibernate_sequence start 1 increment 1;
create table application (
     id int8 not null,
     createTime timestamp,
     createUser varchar(255),
     deleted boolean,
     updateTime timestamp,
     updateUser varchar(255),
     businessService varchar(255),
     description varchar(255),
     name varchar(255),
     primary key (id)
)
