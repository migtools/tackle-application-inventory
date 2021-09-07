package io.tackle.applicationinventory.resources;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheEntityResource;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.quarkus.rest.data.panache.MethodProperties;
import io.quarkus.rest.data.panache.ResourceProperties;
import io.tackle.applicationinventory.entities.TagType;

import java.util.List;

@ResourceProperties(hal = true)
public interface TagTypeResource extends PanacheEntityResource<TagType, Long> {
    @MethodProperties(exposed = false)
    List<TagType> list(Page page, Sort sort);
}
