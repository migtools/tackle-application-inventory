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
