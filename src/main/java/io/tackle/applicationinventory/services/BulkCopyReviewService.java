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
            Application application = Application.findById(detail.application.id);

            Review oldReview = Review.find("application.id", detail.application.id).firstResult();
            if (oldReview != null) {
                oldReview = copyReviewFromSourceToTarget(bulk.sourceReview, oldReview);
                oldReview.persist();
            } else {
                Review newReview = copyReviewFromSourceToTarget(bulk.sourceReview, new Review());
                newReview.application = application;
                newReview.persist();
            }
        });

        bulk.completed = true;
        bulk.persist();
    }

    private Review copyReviewFromSourceToTarget(Review source, Review target) {
        target.proposedAction = source.proposedAction;
        target.effortEstimate = source.effortEstimate;
        target.businessCriticality = source.businessCriticality;
        target.workPriority = source.workPriority;
        target.comments = source.comments;

        target.copiedFromReviewId = source.id;
        return target;
    }

}
