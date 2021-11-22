package io.tackle.applicationinventory.entities;

import io.tackle.commons.entities.AbstractEntity;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "bulk_copy_review_details")
@SQLDelete(sql = "UPDATE bulk_copy_review_details SET deleted = true WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted = false")
public class BulkCopyReviewDetails extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey, name = "bulk_copy_review_id")
    public BulkCopyReview bulkCopyReview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey, name = "application_id")
    public Application application;

}
