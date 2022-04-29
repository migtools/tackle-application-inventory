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


import io.tackle.applicationinventory.services.CsvExportService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;

@Path("csv-export")
@ApplicationScoped
public class CsvExportResource {
    @Inject
    CsvExportService csvExportService;


    @Produces("text/csv")
    @Consumes("application/json")
    @GET
    public String getCsvExportForImportSummaryId(@QueryParam("importSummaryId") Long id) {
        return csvExportService.getCsvExportForImportSummaryId(id);
    }
}
