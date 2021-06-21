alter table if exists application_import
    drop column parentid,
    add column importSummary_id int8;;