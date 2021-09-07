package io.tackle.applicationinventory.entities;

import io.tackle.commons.annotations.Filterable;
import io.tackle.commons.entities.AbstractEntity;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "stakeholder_group")
@SQLDelete(sql = "UPDATE stakeholder_group SET deleted = true WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted = false")
public class StakeholderGroup extends AbstractEntity {
    @Filterable
    public String name;
    @Filterable
    public String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "stakeholdergroup_stakeholders",
            joinColumns = {@JoinColumn(name = "group_id")},
            inverseJoinColumns = {@JoinColumn(name = "stakeholder_id")}
    )
    @Filterable(filterName = "stakeholders.displayName")
    public Set<Stakeholder> stakeholders = new HashSet<>();

    @PreRemove
    private void preRemove() {
        stakeholders.forEach(stakeholder -> stakeholder.stakeholderGroups.remove(this));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StakeholderGroup)) return false;
        StakeholderGroup group = (StakeholderGroup) o;
        return Objects.equals(id, group.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
