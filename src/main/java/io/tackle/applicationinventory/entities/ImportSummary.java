package io.tackle.applicationinventory.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.tackle.commons.annotations.Filterable;
import io.tackle.commons.entities.AbstractEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    @OneToMany(mappedBy = "importSummary", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JsonBackReference
    public List<ApplicationImport> applicationImports = new ArrayList<>();



}
