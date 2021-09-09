package io.tackle.applicationinventory.entities;

import io.tackle.commons.annotations.Filterable;
import io.tackle.commons.entities.AbstractEntity;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.enterprise.inject.spi.CDI;
import javax.persistence.*;

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

    @PreRemove
    private void preRemove() {
        EntityManager em = CDI.current().select(EntityManager.class).get();
        Query query = em.createNativeQuery("delete from Application_tags where tag = ?1");
        query.setParameter(1, this.id.toString());
        query.executeUpdate();
    }

}
