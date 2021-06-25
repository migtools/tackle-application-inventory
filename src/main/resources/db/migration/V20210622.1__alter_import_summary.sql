alter table if exists import_summary
    add column importTime time default current_time ;