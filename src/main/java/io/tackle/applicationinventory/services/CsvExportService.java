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
package io.tackle.applicationinventory.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import io.tackle.applicationinventory.entities.ApplicationImport;
import io.tackle.applicationinventory.entities.ApplicationImportForCsv;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class CsvExportService {

    public String getCsvExportForImportSummaryId(Long importSummaryId)
    {
        List<ApplicationImport> importList = ApplicationImport.list("importSummary_id=?1 and isValid=?2", importSummaryId, false);
        final CsvMapper mapper = new CsvMapper();
        mapper.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
        final CsvSchema schema = mapper.schemaFor(ApplicationImportForCsv.class);
        mapper.addMixIn(ApplicationImport.class, ApplicationImportForCsv.class);

        String csv = null;
        try {
            csv = mapper.writer(schema.withUseHeader(true)).writeValueAsString(importList);
        }catch(JsonProcessingException jpe){
            jpe.printStackTrace();
            throw new RuntimeException(jpe);
        }
        return csv;
    }
}
