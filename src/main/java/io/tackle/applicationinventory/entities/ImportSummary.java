package io.tackle.applicationinventory.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.tackle.commons.annotations.Filterable;
import io.tackle.commons.entities.AbstractEntity;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.ws.rs.DefaultValue;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(
        name = "import_summary"
)
public class ImportSummary extends AbstractEntity {
    @Filterable
    public String filename;
    @Filterable
    public String importStatus;
    public String errorMessage;
    @CreationTimestamp
    @Column(updatable=false)
    public Timestamp importtime;
    @OneToMany(mappedBy = "importSummary", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JsonBackReference
    public List<ApplicationImport> applicationImports = new ArrayList<>();

    @Transient
    public int invalidCount;

    @Transient
    public int validCount;

    @PostLoad
    private void setCounts()
    {
        invalidCount = applicationImports.stream().filter(applicationImport -> applicationImport.isValid.equals(Boolean.FALSE)).collect(Collectors.toList()).size();
        validCount = applicationImports.stream().filter(applicationImport -> applicationImport.isValid.equals(Boolean.TRUE)).collect(Collectors.toList()).size();
    }



}
