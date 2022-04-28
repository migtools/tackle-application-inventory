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

    private void handleSimpleRollback(UserTransaction transaction) {
        try {
            transaction.rollback();
        } catch (SystemException e) {
            throw new IllegalStateException(e);
        }
    }

    @POST
    public BulkReviewDto createBulkCopyReview(
            @NotNull @Valid BulkReviewDto bulkReviewInput
    ) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        transaction.begin();

        // Find source review
        Long sourceReviewId = bulkReviewInput.getSourceReview();
        Review sourceReview = Review.<Review>findByIdOptional(sourceReviewId)
                .orElseThrow(() -> {
                    handleSimpleRollback(transaction);
                    return new BadRequestException("Source review not valid");
                });

        // Find target applications
        List<Application> applicationsTarget = bulkReviewInput.getTargetApplications().stream()
                .map(appId -> Application.<Application>findById(appId))
                .collect(Collectors.toList());
        if (applicationsTarget.stream().anyMatch(Objects::isNull)) {
            handleSimpleRollback(transaction);
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
