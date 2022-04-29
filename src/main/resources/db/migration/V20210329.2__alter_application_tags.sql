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

alter table if exists Application_tags 
       add constraint FK1c9mh21b107s7hhqt9aq8g7e7 
       foreign key (Application_id) 
       references application;
alter table if exists Application_tags
    add constraint UK5adrofcv3ygl80g2lyh240u31 unique (Application_id, tag);
