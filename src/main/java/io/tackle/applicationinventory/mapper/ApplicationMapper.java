/*
 * Copyright © 2021 the Konveyor Contributors (https://konveyor.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.tackle.applicationinventory.mapper;

import io.tackle.applicationinventory.BusinessService;
import io.tackle.applicationinventory.Tag;
import io.tackle.applicationinventory.entities.ApplicationImport;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import java.util.Set;

public abstract class ApplicationMapper {
    @ApplicationScoped
    Set<Tag> tags;
    @ApplicationScoped
    Set<BusinessService> businessServices;

    public ApplicationMapper(Set<Tag> tags, Set<BusinessService> businessServices)
    {
        this.tags = tags;
        this.businessServices = businessServices;
    }

    public abstract Response map(ApplicationImport importApp, Long parentId);
}
