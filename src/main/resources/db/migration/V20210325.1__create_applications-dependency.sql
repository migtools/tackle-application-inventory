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

create table applications_dependency (
    id int8 not null,
    createTime timestamp,
    createUser varchar(255),
    deleted boolean,
    updateTime timestamp,
    updateUser varchar(255),
    from_id int8,
    to_id int8,
    primary key (id)
);
create unique INDEX UKlgwufxhgndoa6198mawite3fo
on applications_dependency (from_id, to_id)
where (deleted = false);
alter table if exists applications_dependency
    add constraint FKfwaxbxio04nk2gj90ssf8p82f
    foreign key (from_id)
    references application;
alter table if exists applications_dependency
    add constraint FKlracllg4jlktabdvo2mp41hhj
    foreign key (to_id)
    references application;
