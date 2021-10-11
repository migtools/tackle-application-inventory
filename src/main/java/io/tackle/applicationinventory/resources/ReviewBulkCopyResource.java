package io.tackle.applicationinventory.resources;

import io.tackle.applicationinventory.dto.BulkReviewDto;
import io.tackle.applicationinventory.dto.utils.EntityToDTO;
import io.tackle.applicationinventory.entities.Application;
import io.tackle.applicationinventory.entities.BulkCopyReview;
import io.tackle.applicationinventory.entities.BulkCopyReviewDetails;
import io.tackle.applicationinventory.entities.Review;
import io.tackle.applicationinventory.services.BulkCopyReviewService;
import io.vertx.core.eventbus.EventBus;

import javax.inject.Inject;
import javax.transaction.NotSupportedException;
import javax.transaction.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Path("/review/bulk")
@Produces("application/json")
@Consumes("application/json")
public class ReviewBulkCopyResource {

    @Inject
    UserTransaction transaction;

    @Inject
    EventBus eventBus;

    @POST
    public BulkReviewDto createBulkCopyReview(
            @NotNull @Valid BulkReviewDto bulkReviewInput
    ) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        transaction.begin();

        // Find source review
        Long sourceReviewId = bulkReviewInput.getSourceReview();
        Review sourceReview = Review.<Review>findByIdOptional(sourceReviewId)
                .orElseThrow(() -> new BadRequestException("Source review not valid"));

        // Find target applications
        List<Application> applicationsTarget = bulkReviewInput.getTargetApplications().stream()
                .map(appId -> Application.<Application>findById(appId))
                .collect(Collectors.toList());
        if (applicationsTarget.stream().anyMatch(Objects::isNull)) {
            throw new BadRequestException("One or more target applications is not valid");
        }

        // Prepare JPA object to be persisted
        BulkCopyReview bulkCopyReviewOutput = new BulkCopyReview();

        bulkCopyReviewOutput.sourceReview = sourceReview;
        bulkCopyReviewOutput.targetApplications = applicationsTarget.stream()
                .map(application -> {
                    BulkCopyReviewDetails detail = new BulkCopyReviewDetails();
                    detail.bulkCopyReview = bulkCopyReviewOutput;
                    detail.application = application;

                    return detail;
                })
                .collect(Collectors.toSet());
        bulkCopyReviewOutput.completed = false;

        bulkCopyReviewOutput.persist();
        transaction.commit();

        // Fire bus event
        eventBus.send(BulkCopyReviewService.BUS_EVENT, bulkCopyReviewOutput.id);

        // Generate response
        return EntityToDTO.toDTO(bulkCopyReviewOutput);
    }

    @GET
    @Path("/{id}")
    public BulkReviewDto getBulkCopyReview(@PathParam("id") Long id) {
        BulkCopyReview entity = BulkCopyReview
                .<BulkCopyReview>findByIdOptional(id)
                .orElseThrow(NotFoundException::new);

        return EntityToDTO.toDTO(entity);
    }

}
