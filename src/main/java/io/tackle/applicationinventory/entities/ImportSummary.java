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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.tackle.commons.annotations.Filterable;
import io.tackle.commons.entities.AbstractEntity;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
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
    public Timestamp importTime;
    @OneToMany(mappedBy = "importSummary", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JsonIgnore
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
