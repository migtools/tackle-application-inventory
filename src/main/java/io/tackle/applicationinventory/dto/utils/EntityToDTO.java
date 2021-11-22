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
