alter table if exists application_import
    add column status varchar (255),
    add column parentid int8;