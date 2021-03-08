package io.tackle.applicationinventory.entities;

import io.tackle.commons.annotations.Filterable;
import io.tackle.commons.entities.AbstractEntity;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "application")
@SQLDelete(sql = "UPDATE application SET deleted = true WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted = false")
public class Application extends AbstractEntity {
    @Filterable
    public String name;
    @Filterable
    public String description;
    public String businessService;
    @Filterable
    public String comments;
}
