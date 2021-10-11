package io.tackle.applicationinventory.services;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;
import io.tackle.applicationinventory.entities.Application;
import io.tackle.applicationinventory.entities.BulkCopyReview;
import io.tackle.applicationinventory.entities.Review;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
public class BulkCopyReviewService {

    public static final String BUS_EVENT = "process-bulk-review-copy";

    @Transactional
    @ConsumeEvent(BUS_EVENT)
    @Blocking
    public void processBulkCopyReview(Long bulkId) {
        BulkCopyReview bulk = BulkCopyReview.findById(bulkId);

        // Copy review to all apps
        bulk.targetApplications.forEach(detail -> {
            // Delete previous review if exists
            Review.find("application.id", detail.application.id)
                    .<Review>firstResultOptional()
                    .ifPresent(oldReview -> {
                        oldReview.delete();
                        oldReview.flush(); // Needed for updating the indexes
                    });

            // Create new Review
            Application targetApplication = Application.findById(detail.application.id);

            Review newReview = createReviewFromSourceAndTarget(bulk.sourceReview, targetApplication);
            newReview.persist();
        });

        bulk.completed = true;
        bulk.persist();
    }

    private Review createReviewFromSourceAndTarget(Review source, Application target) {
        Review result = new Review();

        result.proposedAction = source.proposedAction;
        result.effortEstimate = source.effortEstimate;
        result.businessCriticality = source.businessCriticality;
        result.workPriority = source.workPriority;
        result.comments = source.comments;

        result.application = target;
        return result;
    }

}
