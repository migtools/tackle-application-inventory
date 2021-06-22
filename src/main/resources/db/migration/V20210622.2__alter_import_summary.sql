alter table if exists import_summary
    drop column importtime,
    add column importTime timestamp default current_timestamp;