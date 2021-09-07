package io.tackle.applicationinventory.resources;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheEntityResource;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.quarkus.rest.data.panache.MethodProperties;
import io.quarkus.rest.data.panache.ResourceProperties;
import io.tackle.applicationinventory.entities.Stakeholder;

import java.util.List;

@ResourceProperties(hal = true)
public interface StakeholderResource extends PanacheEntityResource<Stakeholder, Long> {
    @MethodProperties(exposed = false)
    List<Stakeholder> list(Page page, Sort sort);

    @MethodProperties(exposed = false)
    Stakeholder update(Long id, Stakeholder stakeholder);
}
