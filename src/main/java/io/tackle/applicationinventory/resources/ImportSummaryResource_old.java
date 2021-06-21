package io.tackle.applicationinventory.resources;

import io.tackle.applicationinventory.dto.ImportSummaryDto;
import io.tackle.applicationinventory.services.ImportSummaryService;
import io.tackle.commons.resources.hal.HalCollectionEnrichedWrapper;
import org.jboss.resteasy.links.LinkResource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

@Path("import-summary-old")
public class ImportSummaryResource_old {
    @Inject
    ImportSummaryService svc;

    @Path("summary")
    @Produces("application/json")
    @GET
    public List<ImportSummaryDto> getImportSummary() {
        return svc.getSummary();
    }

    @Produces("application/hal+json")
    @GET
    @LinkResource(
            entityClassName = "io.tackle.applicationinventory.dto.ImportSummaryDto",
            rel = "list"
    )
    public Response getImportSummaryHal() {
        final List<ImportSummaryDto> importSummaryDtoList = svc.getSummary();
        final HalCollectionEnrichedWrapper halCollectionEnrichedWrapper =
                new HalCollectionEnrichedWrapper(Collections.unmodifiableCollection(importSummaryDtoList),
                        ImportSummaryDto.class, "import-summary", importSummaryDtoList.size());
        return Response.ok(halCollectionEnrichedWrapper).build();
    }
}
