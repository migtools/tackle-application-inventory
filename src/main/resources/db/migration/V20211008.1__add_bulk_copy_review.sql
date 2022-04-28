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

create table bulk_copy_review
(
    id         int8 not null,

    createTime timestamp,
    createUser varchar(255),
    deleted    boolean,
    updateTime timestamp,
    updateUser varchar(255),

    completed  boolean,
    review_id  int8 not null,
    primary key (id)
);

create table bulk_copy_review_details
(
    id                  int8 not null,

    createTime          timestamp,
    createUser          varchar(255),
    deleted             boolean,
    updateTime          timestamp,
    updateUser          varchar(255),

    bulk_copy_review_id int8 not null,
    application_id      int8 not null,
    primary key (id)
);

alter table if exists bulk_copy_review_details
    add constraint fk_bulk_copy_review_details_application
    foreign key (application_id)
    references application;

-- Add audit data

alter table if exists review
    add column copiedFromReviewId int8;
