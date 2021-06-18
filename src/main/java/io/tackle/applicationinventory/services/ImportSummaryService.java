package io.tackle.applicationinventory.services;

import io.tackle.applicationinventory.dto.ImportSummaryDto;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@ApplicationScoped
public class ImportSummaryService {
    @Inject
    EntityManager em;

    public List<ImportSummaryDto> getSummary() {
        Query query = em.createNativeQuery("SELECT max(i.filename) as filename, i.parentid, (select ii.status from application_import ii where ii.id = i.parentid) as status, min(i.createUser) as createuser, min(i.createTime) as createtime, sum(case when i.isValid = true then 1 else 0 end) AS validCount, " +
                "sum(case when i.isValid = false then 1 else 0 end) AS invalidCount  FROM Application_Import i where i.parentid IS NOT NULL GROUP BY i.parentid", "ImportSummaryDtoMapping");
        @SuppressWarnings("unchecked")
        List<ImportSummaryDto> results = query.getResultList();
        return  results;
    }
}
