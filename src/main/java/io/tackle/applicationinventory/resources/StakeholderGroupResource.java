package io.tackle.applicationinventory.resources;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheEntityResource;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.quarkus.rest.data.panache.MethodProperties;
import io.quarkus.rest.data.panache.ResourceProperties;
import io.tackle.applicationinventory.entities.StakeholderGroup;

import java.util.List;

@ResourceProperties(hal = true)
public interface StakeholderGroupResource extends PanacheEntityResource<StakeholderGroup, Long> {
    @MethodProperties(exposed = false)
    List<StakeholderGroup> list(Page page, Sort sort);
}
