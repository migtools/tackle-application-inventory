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
@Table(name = "tag")
@SQLDelete(sql = "UPDATE tag SET deleted = true WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted = false")
public class Tag extends AbstractEntity {
    @Filterable
    public String name;
    @ManyToOne(optional = false)
    @Filterable(filterName = "tagType.id")
    public TagType tagType;

    public Long getId() {
        return this.id;
    }

}
