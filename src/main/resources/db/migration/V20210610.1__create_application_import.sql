--
-- Copyright © 2021 Konveyor (https://konveyor.io/)
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

create table application_import (
    id int8 not null,
    createTime timestamp,
    createUser varchar(255),
    deleted boolean,
    updateTime timestamp,
    updateUser varchar(255),
    businessService varchar(255),
    description varchar(255),
    applicationname varchar(255),
    comments varchar(255),
    recordType1 varchar(255),
    tag1 varchar(255),
    tagType1 varchar(255),
    tag2 varchar(255),
    tagType2 varchar(255),
    tag3 varchar(255),
    tagType3 varchar(255),
    tag4 varchar(255),
    tagType4 varchar(255),
    errorMessage varchar(255),
    isValid boolean,
    primary key (id)
)