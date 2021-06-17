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

    public List getSummary() {
        Query query = em.createNativeQuery("ApplicationImport.getSummary");
        return  query.getResultList();
    }
}
