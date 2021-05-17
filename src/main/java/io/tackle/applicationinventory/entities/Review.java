package io.tackle.applicationinventory.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.tackle.commons.entities.AbstractEntity;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "review")
@SQLDelete(sql = "UPDATE review SET deleted = true WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted = false")
public class Review extends AbstractEntity {
    public String proposedAction;
    public String effortEstimate;
    public Integer businessCriticality;
    public Integer workPriority;
    @Column(length = 1024)
    public String comments;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnoreProperties(value = {"review"}, allowSetters = true)
    public Application application;
}
