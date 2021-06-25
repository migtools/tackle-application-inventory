create table import_summary (
                             id int8 not null,
                             createTime timestamp,
                             createUser varchar(255),
                             deleted boolean,
                             updateTime timestamp,
                             updateUser varchar(255),
                             importStatus varchar(255),
                             filename varchar(255),
                             errorMessage varchar(255),
                             primary key (id)
)