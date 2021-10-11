package io.tackle.applicationinventory.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class BulkReviewDto {

    private Long id;

    @NotNull
    private Long sourceReview;

    @NotEmpty
    private List<Long> targetApplications;

    private boolean completed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSourceReview() {
        return sourceReview;
    }

    public void setSourceReview(Long sourceReview) {
        this.sourceReview = sourceReview;
    }

    public List<Long> getTargetApplications() {
        return targetApplications;
    }

    public void setTargetApplications(List<Long> targetApplications) {
        this.targetApplications = targetApplications;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

}
