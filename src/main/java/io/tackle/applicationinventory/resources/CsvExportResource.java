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
