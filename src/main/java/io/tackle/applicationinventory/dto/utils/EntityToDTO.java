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
package io.tackle.applicationinventory.dto.utils;

import io.tackle.applicationinventory.dto.BulkReviewDto;
import io.tackle.applicationinventory.entities.BulkCopyReview;

import java.util.stream.Collectors;

public class EntityToDTO {

    public static BulkReviewDto toDTO(BulkCopyReview entity) {
        BulkReviewDto dto = new BulkReviewDto();

        dto.setId(entity.id);
        dto.setCompleted(entity.completed);
        dto.setSourceReview(entity.sourceReview.id);
        dto.setTargetApplications(entity.targetApplications.stream()
                .map(f -> f.application.id)
                .collect(Collectors.toList())
        );

        return dto;
    }
}
