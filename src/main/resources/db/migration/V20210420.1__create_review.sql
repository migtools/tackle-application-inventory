--
-- Copyright © 2021 the Konveyor Contributors (https://konveyor.io/)
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

create table review (
    id int8 not null,
    createTime timestamp,
    createUser varchar(255),
    deleted boolean,
    updateTime timestamp,
    updateUser varchar(255),
    businessCriticality int4,
    comments varchar(1024),
    effortEstimate varchar(255),
    proposedAction varchar(255),
    workPriority int4,
    application_id int8 not null,
    primary key (id)
);
alter table if exists review
    add constraint FKeqil1mqbskys3qm3rb71e39uv
    foreign key (application_id)
    references application;
create unique INDEX UK_nwofjmf0nwbb9u45qj72ao40u
on review (application_id)
where (deleted = false);
