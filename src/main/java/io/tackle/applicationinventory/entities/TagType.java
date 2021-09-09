package io.tackle.applicationinventory.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.tackle.commons.annotations.Filterable;
import io.tackle.commons.entities.AbstractEntity;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.enterprise.inject.spi.CDI;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tag_type")
@SQLDelete(sql = "UPDATE tag_type SET deleted = true WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted = false")
public class TagType extends AbstractEntity {
    @Filterable
    public String name;
    @Filterable
    public Integer rank;
    @Filterable
    public String colour;
    @OneToMany(mappedBy = "tagType", fetch = FetchType.LAZY)
    @Filterable(filterName = "tags.name")
    @JsonBackReference
    public List<Tag> tags = new ArrayList<>();

    @PreRemove
    private void preRemove() {
        EntityManager em = CDI.current().select(EntityManager.class).get();
        Query query = em.createNativeQuery("update tag set deleted=true where tagType_id = ?1");
        query.setParameter(1, this.id);
        query.executeUpdate();

        query = em.createNativeQuery("delete from Application_tags where tag in (select cast(id as text) from tag where tagType_id = ?1)");
        query.setParameter(1, this.id);
        query.executeUpdate();
    }
}
