alter table if exists application_import
    add column dependency varchar (255),
    add column dependencyDirection varchar (255);