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
        Query query = em.createNativeQuery("SELECT i.filename, min(i.createUser) as createuser, min(i.createTime) as createtime, sum(case when i.isValid = true then 1 else 0 end) AS validCount, " +
                "sum(case when i.isValid = false then 1 else 0 end) AS invalidCount  FROM Application_Import i GROUP BY i.filename", "ImportSummaryDtoMapping");
        @SuppressWarnings("unchecked")
        List<ImportSummaryDto> results = query.getResultList();
        return  results;
    }
}
