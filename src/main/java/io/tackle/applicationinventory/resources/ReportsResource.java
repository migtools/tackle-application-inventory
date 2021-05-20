package io.tackle.applicationinventory.resources;

import io.tackle.applicationinventory.dto.AdoptionPlanAppDto;
import io.tackle.applicationinventory.services.ReportService;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

@Path("report")
public class ReportsResource {
    @Inject
    ReportService reportService;

    @Path("adoptionplan")
    @Produces("application/json")
    @GET
    public List<AdoptionPlanAppDto> getAdoptionPlanGantt(@NotNull @QueryParam("applicationId") List<Long> applicationIds) {
        return reportService.getAdoptionPlanAppDtos(applicationIds);
    }


}
