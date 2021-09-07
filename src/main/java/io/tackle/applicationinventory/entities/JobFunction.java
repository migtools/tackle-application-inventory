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
@Table(name = "job_function")
@SQLDelete(sql = "UPDATE job_function SET deleted = true WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted = false")
public class JobFunction extends AbstractEntity {
    @Filterable
    public String role;
    @OneToMany(mappedBy = "jobFunction", fetch = FetchType.LAZY)
    @JsonBackReference
    public List<Stakeholder> stakeholders = new ArrayList<>();

    @PreRemove
    private void preRemove() {
        stakeholders.forEach(stakeholder -> stakeholder.jobFunction = null);
    }

}
