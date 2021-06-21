alter table if exists application_import
    drop column importSummary_id,
    add column importSummary_id int8 not null,
    add constraint FKp755h0bv2vgcmrsj1p4ebqjn6
    foreign key (importSummary_id)
    references import_summary;