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
package io.tackle.applicationinventory.resources;

import io.tackle.applicationinventory.dto.AdoptionPlanAppDto;
import io.tackle.applicationinventory.dto.ApplicationDto;
import io.tackle.applicationinventory.services.ReportService;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import java.util.List;
import java.util.stream.Collectors;

@Path("report")
public class ReportsResource {
    @Inject
    ReportService reportService;

    @Path("adoptionplan")
    @Produces("application/json")
    @Consumes("application/json")
    @POST
    public List<AdoptionPlanAppDto> getAdoptionPlanGantt(@NotNull List<ApplicationDto> applicationIds) {
        return reportService.getAdoptionPlanAppDtos(applicationIds.stream().map(a -> a.applicationId).collect(Collectors.toList()));
    }


}
