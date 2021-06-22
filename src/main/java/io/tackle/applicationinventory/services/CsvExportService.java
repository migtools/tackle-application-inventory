package io.tackle.applicationinventory.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import io.tackle.applicationinventory.entities.ApplicationImport;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class CsvExportService {

    public String getCsvExportForImportSummaryId(Long importSummaryId)
    {
        List<ApplicationImport> importList = ApplicationImport.list("importSummary_id", importSummaryId);
        final CsvMapper mapper = new CsvMapper();
        final CsvSchema schema = mapper.schemaFor(ApplicationImport.class);

        String csv = null;
        try {
            csv = mapper.writer(schema.withUseHeader(true)).writeValueAsString(importList);
        }catch(JsonProcessingException jpe){
            jpe.printStackTrace();

        }
        return csv;
    }
}
