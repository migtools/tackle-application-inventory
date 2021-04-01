package io.tackle.applicationinventory.entities;

import io.tackle.commons.annotations.CheckType;
import io.tackle.commons.annotations.Filterable;
import io.tackle.commons.entities.AbstractEntity;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.Set;
import java.util.HashSet;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.ElementCollection;
import javax.persistence.UniqueConstraint;
import java.util.Objects;

@Entity
@Table(name = "application")
@SQLDelete(sql = "UPDATE application SET deleted = true WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted = false")
public class Application extends AbstractEntity {
    @Filterable
    public String name;
    @Filterable
    public String description;
    @Filterable(check = CheckType.EQUAL)
    public String businessService;
    @Filterable
    public String comments;

    @ElementCollection
    @CollectionTable(
            name = "Application_tags",
            joinColumns = @JoinColumn(name = "Application_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"Application_id", "tag"})
    )
    @Column(name = "tag")
    @Filterable(filterName = "tags.tag", check = CheckType.EQUAL)
    public Set<String> tags = new HashSet<>();

    /**
     * equals and hashCode methods overridden for being able to use this bean with the {@link org.jgrapht.Graph}
     * in the {@link io.tackle.applicationinventory.entities.ApplicationsDependency#preChangesCheck()}
     * considering it's mandatory to have to address the "logical equality" of Application beans
     * which is based exclusively on the id value because all of the other fields can change for a bean.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Application)) return false;
        Application that = (Application) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
