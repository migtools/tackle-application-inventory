package io.tackle.applicationinventory.entities;

import io.tackle.commons.annotations.Filterable;
import io.tackle.commons.entities.AbstractEntity;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "business_service")
@SQLDelete(sql = "UPDATE business_service SET deleted = true WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted = false")
public class BusinessService extends AbstractEntity {
    @Filterable
    public String name;
    @Filterable
    public String description;
    @ManyToOne
    @Filterable(filterName = "owner.displayName")
    public Stakeholder owner;
}
