/*
 * Copyright © 2021 Konveyor (https://konveyor.io/)
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
package io.tackle.applicationinventory.resources;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheEntityResource;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.quarkus.rest.data.panache.MethodProperties;
import io.quarkus.rest.data.panache.ResourceProperties;
import io.tackle.applicationinventory.entities.ApplicationsDependency;

import javax.ws.rs.core.Response;
import java.util.List;

@ResourceProperties(hal = true)
public interface ApplicationsDependencyResource extends PanacheEntityResource<ApplicationsDependency, Long> {
    @MethodProperties(exposed = false)
    List<ApplicationsDependency> list(Page page, Sort sort);

    // updating a dependency doesn't make sense because it would mean changing
    // at least one of the applications involved and that represents a different dependency
    @MethodProperties(exposed = false)
    Response update(Long id);
}
