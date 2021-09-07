package io.tackle.applicationinventory.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.tackle.commons.annotations.Filterable;
import io.tackle.commons.entities.AbstractEntity;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

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
    @OneToMany(mappedBy = "tagType", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @Filterable(filterName = "tags.name")
    @JsonBackReference
    public List<Tag> tags = new ArrayList<>();
}
