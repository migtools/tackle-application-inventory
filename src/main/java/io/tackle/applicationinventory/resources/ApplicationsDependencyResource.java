package io.tackle.applicationinventory.resources;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheEntityResource;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.quarkus.rest.data.panache.MethodProperties;
import io.quarkus.rest.data.panache.ResourceProperties;
import io.tackle.applicationinventory.entities.ApplicationsDependency;

import javax.ws.rs.core.Response;
import java.util.List;

@ResourceProperties(hal = true)
public interface ApplicationsDependencyResource extends PanacheEntityResource<ApplicationsDependency, Long> {
    @MethodProperties(exposed = false)
    List<ApplicationsDependency> list(Page page, Sort sort);

    // updating a dependency doesn't make sense because it would mean changing
    // at least one of the applications involved and that represents a different dependency
    @MethodProperties(exposed = false)
    Response update(Long id);
}
