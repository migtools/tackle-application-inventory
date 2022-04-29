/*
 * Copyright © 2021 the Konveyor Contributors (https://konveyor.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.tackle.applicationinventory.entities;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import io.tackle.commons.annotations.CheckType;
import io.tackle.commons.annotations.Filterable;
import io.tackle.commons.entities.AbstractEntity;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    @OneToOne(mappedBy = "application", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    public Review review;

    /**
     * The unidirectional {@link javax.persistence.ManyToOne} associations from {@link ApplicationsDependency}
     * and the soft delete approach adopted, prevents the "standard" 'on cascade delete' approach
     * on the FK constraint definition from working.
     * So the cascade has been implemented here as a pre-remove application task.
     */
    @PreRemove
    public void preRemove() {
        ApplicationsDependency.list("from_id = :id OR to_id = :id", Parameters.with("id", id))
                .forEach(PanacheEntityBase::delete);
    }

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
