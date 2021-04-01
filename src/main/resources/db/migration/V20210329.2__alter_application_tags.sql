alter table if exists Application_tags 
       add constraint FK1c9mh21b107s7hhqt9aq8g7e7 
       foreign key (Application_id) 
       references application;
alter table if exists Application_tags
    add constraint UKajtkwngylkio3007ksyreg2yu unique (Application_id, tags);
