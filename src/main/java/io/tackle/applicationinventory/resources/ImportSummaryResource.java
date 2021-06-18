package io.tackle.applicationinventory.resources;

import io.tackle.applicationinventory.dto.ImportSummaryDto;
import io.tackle.applicationinventory.services.ImportSummaryService;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;

@Path("import-summary")
public class ImportSummaryResource {
    @Inject
    ImportSummaryService svc;


    @Path("summary")
    @Produces("application/hal+json")
    @GET
    public List<ImportSummaryDto> getImportSummary() {
        return svc.getSummary();
    }
}
