alter table if exists Application_tags 
       add constraint FK1c9mh21b107s7hhqt9aq8g7e7 
       foreign key (Application_id) 
       references application;
alter table if exists Application_tags
    add constraint UK5adrofcv3ygl80g2lyh240u31 unique (Application_id, tag);
